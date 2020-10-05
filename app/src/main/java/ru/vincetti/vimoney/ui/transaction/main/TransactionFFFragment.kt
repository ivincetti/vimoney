package ru.vincetti.vimoney.ui.transaction.main

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_add_all.*
import kotlinx.android.synthetic.main.fragment_add_all.view.*
import kotlinx.android.synthetic.main.fragment_add_spent.*
import ru.vincetti.vimoney.R
import ru.vincetti.vimoney.data.models.AccountListModel
import ru.vincetti.vimoney.data.sqlite.AppDatabase
import java.text.DateFormat
import java.util.*

open class TransactionFFFragment(contentLayoutId: Int) : Fragment(contentLayoutId) {

    val viewModel: TransactionMainViewModel by viewModels({ requireParentFragment() }) { viewModelFactory }

    private lateinit var viewModelFactory: TransactionMainViewModelFactory
    private lateinit var date: Date

    val dialogFrag = CategoryListDialog()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val application = requireNotNull(activity).application
        val db = AppDatabase.getInstance(application)
        viewModelFactory = TransactionMainViewModelFactory(db)
        initFragmentViews()
        initFragmentPlus()
    }

    override fun onResume() {
        super.onResume()

        add_sum.isFocusableInTouchMode = true
        add_sum.requestFocus()

        showKeyboard()
    }

    override fun onPause() {
        viewModel.setSum(add_sum.text.toString())
        viewModel.setDescription(add_desc.text.toString())

        super.onPause()
    }

    open fun initFragmentPlus() {
        /** Sample for transfer*/
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) setCategoryID(resultCode)
    }

    open fun loadAccounts(list: List<AccountListModel>) {
        add_acc_name.setOnClickListener { popUpShow(list, it) }
    }

    open fun save(actionType: Int) {
        viewModel.saveTransaction(
            actionType,
            fragment_add_content.add_desc.text.toString(),
            add_sum.text.toString()
        )
    }

    fun popUpShow(list: List<AccountListModel>, view: View) {
        val popUp = PopupMenu(requireContext(), view)
        for (acc in list) {
            popUp.menu.add(Menu.NONE, acc.id, acc.id, acc.name)
        }
        popUp.setOnMenuItemClickListener {
            viewModel.setAccount(it.itemId)
            true
        }
        popUp.show()
    }

    private fun initFragmentViews() {
        viewModel.needSum.observe(viewLifecycleOwner) {
            if (it) showNoSumToast()
        }
        viewModel.needAccount.observe(viewLifecycleOwner) {
            if (it) showNoAccountToast()
        }
        viewModel.needToUpdate.observe(viewLifecycleOwner) {
            if (it) add_btn.text = getString(R.string.add_btn_update)
        }
        viewModel.account.observe(viewLifecycleOwner) {
            it?.let { add_acc_name.text = it.name }
        }
        viewModel.currency.observe(viewLifecycleOwner) {
            it?.let { add_acc_cur.text = it.symbol }
        }
        viewModel.sum.observe(viewLifecycleOwner) {
            if (it > 0) add_sum.setText(it.toString())
        }
        viewModel.date.observe(viewLifecycleOwner) {
            it?.let {
                date = it
                add_date_txt.text = DateFormat.getDateInstance(DateFormat.MEDIUM).format(it)
            }
        }
        viewModel.description.observe(viewLifecycleOwner) {
            fragment_add_content.add_desc.setText(it)
        }
        viewModel.accountNotArchiveNames.observe(viewLifecycleOwner) {
            it?.let { loadAccounts(it) }
        }
        viewModel.needToNavigate.observe(viewLifecycleOwner) {
            if (it) {
                findNavController().navigateUp()
                viewModel.navigatedBack()
            }
        }
        add_date_block.setOnClickListener { showDateDialog() }
    }

    private fun showKeyboard() {
        val imm: InputMethodManager = requireActivity()
            .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
    }

    private fun showNoSumToast() {
        Toast.makeText(
            requireContext(),
            getString(R.string.add_check_no_sum_warning),
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun showNoAccountToast() {
        Toast.makeText(
            requireContext(),
            getString(R.string.add_check_no_account_warning),
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun showDateDialog() {
        val calendar = GregorianCalendar()
        calendar.time = date

        DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                viewModel.setDate(GregorianCalendar(year, month, day).time)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    fun showCategoryDialog() {
        dialogFrag.show(parentFragmentManager, "Categories")
    }

    private fun setCategoryID(categoryID: Int) {
        viewModel.setCategoryID(categoryID)
    }
}
