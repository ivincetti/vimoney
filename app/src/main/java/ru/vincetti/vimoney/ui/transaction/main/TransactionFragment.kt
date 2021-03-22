package ru.vincetti.vimoney.ui.transaction.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.vincetti.modules.core.models.Transaction
import ru.vincetti.vimoney.R
import ru.vincetti.vimoney.databinding.FragmentAddAllBinding
import java.text.DateFormat
import java.util.*

abstract class TransactionFragment(@LayoutRes layoutId: Int) : Fragment(layoutId) {

    abstract val sumText: EditText

    abstract val bindingAllContent: FragmentAddAllBinding

    val viewModel: TransactionMainViewModel by viewModels({ requireParentFragment() })

    private lateinit var date: Date

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initViewsInternal()
        initObserversInternal()
    }

    override fun onResume() {
        super.onResume()

        sumText.apply {
            isFocusableInTouchMode = true
            requestFocus()
        }
        TransactionFragmentUtils.showKeyboard(requireActivity())
    }

    override fun onPause() {
        viewModel.setSum(sumText.text.toString())
        viewModel.setDescription(bindingAllContent.addDesc.text.toString())

        super.onPause()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) setCategoryID(resultCode)
    }

    abstract fun initViews()
    abstract fun initObservers()

    private fun initViewsInternal() {
        bindingAllContent.addBtn.setOnClickListener { save() }
        bindingAllContent.addDateBlock.setOnClickListener {
            TransactionFragmentUtils.showDateDialog(
                requireContext(),
                date,
                viewModel::setDate
            )
        }
        initViews()
    }

    private fun initObserversInternal() {
        viewModel.contentState.observe(viewLifecycleOwner) { state ->
            when (state) {
                TransactionMainContentState.Empty -> TODO()
                is TransactionMainContentState.Filled -> {
                    setCommonTransactionState(state.update, state.transaction)
                }
            }
        }
        viewModel.action.observe(viewLifecycleOwner, ::handleAction)
        initObservers()
    }

    private fun setCommonTransactionState(isUpdated: Boolean, transaction: Transaction) {
        if (isUpdated) setUpdateButton()
        if (transaction.sum > 0) sumText.setText(transaction.sum.toString())
        transaction.date.let {
            date = it
            bindingAllContent.addDateTxt.text = DateFormat.getDateInstance(DateFormat.MEDIUM).format(it)
        }
        bindingAllContent.addDesc.setText(transaction.description)
    }

    private fun setUpdateButton() {
        bindingAllContent.addBtn.text = getString(R.string.add_btn_update)
    }

    private fun save() {
        viewModel.saveTransaction(
            Transaction.TRANSACTION_TYPE_INCOME,
            bindingAllContent.addDesc.text.toString(),
            sumText.text.toString()
        )
    }

    private fun handleAction(action: TransactionMainContentAction) {
        when (action) {
            is TransactionMainContentAction.CloseSelf -> findNavController().navigateUp()
            is TransactionMainContentAction.AccountError -> TransactionFragmentUtils.showNoAccountToast(requireContext())
            is TransactionMainContentAction.SumError -> TransactionFragmentUtils.showNoSumToast(requireContext())
        }
    }

    private fun setCategoryID(categoryID: Int) {
        viewModel.setCategoryID(categoryID)
    }
}
