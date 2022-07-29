package ru.vincetti.vimoney.ui.transaction.spent

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.vincetti.modules.core.models.Transaction
import ru.vincetti.modules.core.ui.viewBinding
import ru.vincetti.vimoney.R
import ru.vincetti.vimoney.databinding.FragmentAddSpentBinding
import ru.vincetti.vimoney.ui.transaction.main.CategoryListDialog
import ru.vincetti.vimoney.ui.transaction.main.TransactionFragmentUtils
import ru.vincetti.vimoney.ui.transaction.main.TransactionMainViewModel
import java.text.DateFormat
import java.util.*

class TransactionSpentFragment : Fragment(R.layout.fragment_add_spent) {

    val viewModel: TransactionMainViewModel by viewModels({ requireParentFragment() })

    private lateinit var date: Date

    private val binding: FragmentAddSpentBinding by viewBinding(FragmentAddSpentBinding::bind)

    private val dialogFrag = CategoryListDialog()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initViews()
        initObservers()
    }

    override fun onResume() {
        super.onResume()

        binding.addSum.apply {
            isFocusableInTouchMode = true
            requestFocus()
        }
        TransactionFragmentUtils.showKeyboard(requireActivity())
    }

    override fun onPause() {
        viewModel.setSum(binding.addSum.text.toString())
        viewModel.setDescription(binding.fragmentAddAllContent.addDesc.text.toString())

        super.onPause()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) setCategoryID(resultCode)
    }

    private fun save() {
        viewModel.saveTransaction(
            Transaction.TRANSACTION_TYPE_SPENT,
            binding.fragmentAddAllContent.addDesc.text.toString(),
            binding.addSum.text.toString()
        )
    }

    private fun initViews() {
        binding.fragmentAddAllContent.addDateBlock.setOnClickListener {
            TransactionFragmentUtils.showDateDialog(
                requireContext(),
                date,
                viewModel::setDate
            )
        }
        binding.addAccCategoryBlock.setOnClickListener { showCategoryDialog() }
        binding.fragmentAddAllContent.addBtn.setOnClickListener { save() }

        viewModel.category.observe(viewLifecycleOwner) {
            it?.let {
                binding.addAccCategoryIcon.text = it.symbol
                binding.addAccCategoryName.text = it.name
            }
        }
        viewModel.categoriesList.observe(viewLifecycleOwner) {
            it?.let {
                dialogFrag.setTargetFragment(this, 1)
                dialogFrag.setList(it)
            }
        }
    }

    private fun initObservers() {
        viewModel.needSum.observe(viewLifecycleOwner) {
            if (it) TransactionFragmentUtils.showNoSumToast(requireContext())
        }
        viewModel.needAccount.observe(viewLifecycleOwner) {
            if (it) TransactionFragmentUtils.showNoAccountToast(requireContext())
        }
        viewModel.needToUpdate.observe(viewLifecycleOwner) {
            if (it) binding.fragmentAddAllContent.addBtn.text = getString(R.string.add_btn_update)
        }
        viewModel.account.observe(viewLifecycleOwner) {
            it?.let { binding.addAccName.text = it.name }
        }
        viewModel.currency.observe(viewLifecycleOwner) {
            it?.let { binding.addAccCur.text = it.symbol }
        }
        viewModel.sum.observe(viewLifecycleOwner) {
            if (it > 0) binding.addSum.setText(it.toString())
        }
        viewModel.date.observe(viewLifecycleOwner) {
            it?.let {
                date = it
                binding.fragmentAddAllContent.addDateTxt.text = DateFormat.getDateInstance(DateFormat.MEDIUM).format(it)
            }
        }
        viewModel.description.observe(viewLifecycleOwner) {
            binding.fragmentAddAllContent.addDesc.setText(it)
        }
        viewModel.accountNotArchiveNames.observe(viewLifecycleOwner) {
            it?.let { list ->
                binding.addAccName.setOnClickListener { view ->
                    TransactionFragmentUtils.showListPopUp(
                        requireContext(),
                        view,
                        list,
                        viewModel::setAccount
                    )
                }
            }
        }
        viewModel.needToNavigate.observe(viewLifecycleOwner) {
            if (it) {
                findNavController().navigateUp()
                viewModel.navigatedBack()
            }
        }
    }

    private fun showCategoryDialog() {
        TransactionFragmentUtils.hideKeyboard(requireActivity())
        dialogFrag.show(parentFragmentManager, "Categories")
    }

    private fun setCategoryID(categoryID: Int) {
        viewModel.setCategoryID(categoryID)
    }
}
