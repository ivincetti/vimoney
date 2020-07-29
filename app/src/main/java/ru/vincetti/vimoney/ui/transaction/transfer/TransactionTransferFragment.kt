package ru.vincetti.vimoney.ui.transaction.transfer

import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.View
import androidx.appcompat.widget.PopupMenu
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.fragment_add_all.*
import kotlinx.android.synthetic.main.fragment_add_all.view.*
import kotlinx.android.synthetic.main.fragment_add_transfer.*
import ru.vincetti.vimoney.R
import ru.vincetti.vimoney.data.models.AccountListModel
import ru.vincetti.vimoney.data.models.TransactionModel
import ru.vincetti.vimoney.ui.transaction.main.TransactionFFFragment

class TransactionTransferFragment : TransactionFFFragment(R.layout.fragment_add_transfer) {

    override fun initFragmentPlus() {
        viewModel.nestedTransaction.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            it?.let {
                if (it.sum > 0) add_sum_to.setText(it.sum.toString())
            }
        })
        viewModel.accountTo.observe(viewLifecycleOwner, Observer {
            it?.let { add_acc_name_to.text = it.name }
        })
        viewModel.currencyTo.observe(viewLifecycleOwner, Observer {
            it?.let { add_acc_cur_to.text = it.symbol }
        })

        add_sum.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                add_sum_to.setText(s)
            }
        })
        add_btn.setOnClickListener { save(TransactionModel.TRANSACTION_TYPE_TRANSFER) }
    }

    override fun loadAccounts(list: List<AccountListModel>) {
        add_acc_name.setOnClickListener { popUpShow(list, it) }
        add_acc_name_to.setOnClickListener { popUpShowTo(list, it) }
    }

    private fun popUpShowTo(list: List<AccountListModel>, view: View) {
        val popUp = PopupMenu(requireContext(), view)
        for (acc in list) {
            popUp.menu.add(Menu.NONE, acc.id, acc.id, acc.name)
        }
        popUp.setOnMenuItemClickListener {
            viewModel.setAccountTo(it.itemId)
            true
        }
        popUp.show()
    }

    override fun save(actionType: Int) {
        viewModel.saveTransactionTo(
                actionType,
                fragment_add_content.add_desc.text.toString(),
                add_sum.text.toString(),
                add_sum_to.text.toString()
        )
    }
}
