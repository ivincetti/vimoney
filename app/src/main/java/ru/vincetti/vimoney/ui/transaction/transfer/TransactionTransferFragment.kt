package ru.vincetti.vimoney.ui.transaction.transfer

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import ru.vincetti.modules.core.ui.viewBinding
import ru.vincetti.vimoney.R
import ru.vincetti.vimoney.databinding.FragmentAddAllBinding
import ru.vincetti.vimoney.databinding.FragmentAddTransferBinding
import ru.vincetti.vimoney.ui.transaction.main.TransactionFragment
import ru.vincetti.vimoney.ui.transaction.main.TransactionFragmentUtils
import ru.vincetti.vimoney.ui.transaction.main.TransactionMainContentState

class TransactionTransferFragment : TransactionFragment(R.layout.fragment_add_transfer) {

    override val sumText: EditText
        get() = binding.addSum

    override val bindingAllContent: FragmentAddAllBinding
        get() = binding.fragmentAddAllContent

    private val binding: FragmentAddTransferBinding by viewBinding(FragmentAddTransferBinding::bind)

    override fun initViews() {
        sumText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) = Unit
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.addSumTo.setText(s)
            }
        })
    }

    override fun initObservers() {
        viewModel.contentState.observe(viewLifecycleOwner) { state ->
            if (state is TransactionMainContentState.Filled) {
//                binding.addAccName.text = ACC_NAME - viewModel.accountName
//                binding.addAccCur.text = currency
//                viewModel.accountToName.observe(viewLifecycleOwner) { binding.addAccNameTo.text = it }
//                viewModel.currencyTo.observe(viewLifecycleOwner) { binding.addAccCurTo.text = it }

                if (state is TransactionMainContentState.Filled.Transfer) {
                    if (state.nestedTransaction.sum > 0) binding.addSumTo.setText(
                        state.nestedTransaction.sum.toString()
                    )
                }
            }
        }

        viewModel.accountNotArchiveNames.observe(viewLifecycleOwner) {
            binding.addAccName.setOnClickListener { view ->
                TransactionFragmentUtils.showListPopUp(
                    requireContext(),
                    view,
                    it,
                    viewModel::setAccount
                )
            }
            binding.addAccNameTo.setOnClickListener { view ->
                TransactionFragmentUtils.showListPopUp(
                    requireContext(),
                    view,
                    it,
                    viewModel::setAccountTo
                )
            }
        }
    }
}
