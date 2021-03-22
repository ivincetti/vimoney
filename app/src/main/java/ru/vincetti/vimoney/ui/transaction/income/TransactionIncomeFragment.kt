package ru.vincetti.vimoney.ui.transaction.income

import android.widget.EditText
import ru.vincetti.modules.core.models.Transaction
import ru.vincetti.modules.core.ui.viewBinding
import ru.vincetti.vimoney.R
import ru.vincetti.vimoney.databinding.FragmentAddAllBinding
import ru.vincetti.vimoney.databinding.FragmentAddSpentBinding
import ru.vincetti.vimoney.ui.transaction.main.CategoryListDialog
import ru.vincetti.vimoney.ui.transaction.main.TransactionFragment
import ru.vincetti.vimoney.ui.transaction.main.TransactionFragmentUtils
import ru.vincetti.vimoney.ui.transaction.main.TransactionMainContentState

class TransactionIncomeFragment : TransactionFragment(R.layout.fragment_add_spent) {

    override val sumText: EditText
        get() = binding.addSum

    override val bindingAllContent: FragmentAddAllBinding
        get() = binding.fragmentAddAllContent

    private val binding: FragmentAddSpentBinding by viewBinding(FragmentAddSpentBinding::bind)

    private val dialogFrag = CategoryListDialog()

    private fun save() {
        viewModel.saveTransaction(
            Transaction.TRANSACTION_TYPE_INCOME,
            binding.fragmentAddAllContent.addDesc.text.toString(),
            binding.addSum.text.toString()
        )
    }

    override fun initViews() {
        binding.addAccCategoryBlock.setOnClickListener { showCategoryDialog() }
    }

    override fun initObservers() {
        viewModel.contentState.observe(viewLifecycleOwner) { state ->
            if (state is TransactionMainContentState.Filled) {
//                binding.addAccName.text = ACC_NAME - viewModel.accountName
//                binding.addAccCur.text = currency
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
        }
//        viewModel.category.observe(viewLifecycleOwner) {
//            it?.let {
//                binding.addAccCategoryIcon.text = it.symbol
//                binding.addAccCategoryName.text = it.name
//            }
//        }
        viewModel.categoriesList.observe(viewLifecycleOwner) {
            dialogFrag.setTargetFragment(this, 1)
            dialogFrag.setList(it)
        }
    }

    private fun showCategoryDialog() {
        TransactionFragmentUtils.hideKeyboard(requireActivity())
        dialogFrag.show(parentFragmentManager, "Categories")
    }
}
