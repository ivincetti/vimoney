package ru.vincetti.vimoney.ui.transaction.main

import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.ViewPager
import dagger.hilt.android.AndroidEntryPoint
import ru.vincetti.modules.core.models.Transaction
import ru.vincetti.modules.core.ui.viewBinding
import ru.vincetti.vimoney.R
import ru.vincetti.vimoney.databinding.FragmentTransactionMainBinding
import ru.vincetti.vimoney.extensions.top
import ru.vincetti.vimoney.extensions.updateMargin
import ru.vincetti.vimoney.ui.check.add.AddCheckFragment
import ru.vincetti.vimoney.ui.transaction.TransactionConst

@AndroidEntryPoint
class TransactionMainFragment : Fragment(R.layout.fragment_transaction_main) {

    private val viewModel: TransactionMainViewModel by viewModels()

    private lateinit var fragmentBundle: Bundle

    private val binding: FragmentTransactionMainBinding by viewBinding(FragmentTransactionMainBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragmentBundle = Bundle()
        arguments?.let { bundle ->
            val extraTransactionId = bundle.getInt(TransactionConst.EXTRA_TRANS_ID)
            val extraAccId = bundle.getInt(TransactionConst.EXTRA_ACCOUNT_ID)
            if (extraTransactionId > 0) {
                binding.transactionNavigationDeleteBtn.visibility = View.VISIBLE
                viewModel.loadTransaction(extraTransactionId)
            } else if (extraAccId > 0) viewModel.setAccount(extraAccId)
        }

        binding.viewPager.apply {
            adapter = TabsFragmentPagerAdapter(childFragmentManager, fragmentBundle)
            addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrollStateChanged(state: Int) = Unit
                override fun onPageScrolled(p: Int, pOffset: Float, pOffsetPixels: Int) = Unit

                override fun onPageSelected(position: Int) {
                    setActivityTitle(position)
                }
            })
        }
        initViews()
        insetsInit()
    }

    override fun onResume() {
        super.onResume()
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            showUnsavedDialog()
        }
    }

    override fun onPause() {
        super.onPause()
        TransactionFragmentUtils.hideKeyboard(requireActivity())
    }

    private fun initViews() {
        binding.transactionTabs.setupWithViewPager(binding.viewPager)
        viewModel.contentState.observe(viewLifecycleOwner, ::handleContentType)

        binding.transactionNavigationDeleteBtn.setOnClickListener { showDeleteDialog() }
        binding.settingNavigationBackBtn.setOnClickListener { showUnsavedDialog() }
    }

    fun setActivityTitle(position: Int) {
        binding.transactionNavigationTxt.text = when (position) {
            Transaction.TRANSACTION_TYPE_SPENT_TAB -> getString(R.string.add_title_home_spent)
            Transaction.TRANSACTION_TYPE_TRANSFER_TAB -> getString(R.string.add_title_home_transfer)
            Transaction.TRANSACTION_TYPE_DEBT_TAB -> getString(R.string.add_title_home_debt)
            else -> getString(R.string.add_title_home_income)
        }
    }

    private fun handleContentType(contentState: TransactionMainContentState) {
        when (contentState) {
            is TransactionMainContentState.Filled.Spending -> loadSpending()
            is TransactionMainContentState.Filled.Income -> loadIncome()
            is TransactionMainContentState.Filled.Transfer -> loadTransfer()
            is TransactionMainContentState.Empty -> loadSpending()
        }
    }

    private fun loadIncome() {
        binding.viewPager.setCurrentItem(Transaction.TRANSACTION_TYPE_INCOME_TAB, true)
        setActivityTitle(Transaction.TRANSACTION_TYPE_INCOME_TAB)
    }

    private fun loadSpending() {
        binding.viewPager.setCurrentItem(Transaction.TRANSACTION_TYPE_SPENT_TAB, true)
        setActivityTitle(Transaction.TRANSACTION_TYPE_SPENT_TAB)
    }

    private fun loadTransfer() {
        binding.viewPager.setCurrentItem(Transaction.TRANSACTION_TYPE_DEBT_TAB, true)
        setActivityTitle(Transaction.TRANSACTION_TYPE_DEBT_TAB)
    }

    // future
    private fun loadDebt() {
        binding.viewPager.setCurrentItem(Transaction.TRANSACTION_TYPE_DEBT_TAB, true)
        setActivityTitle(Transaction.TRANSACTION_TYPE_DEBT_TAB)
    }

    private fun showDeleteDialog() {
        AlertDialog.Builder(requireContext(), R.style.AlertDialog)
            .setMessage(R.string.transaction_delete_alert_question)
            .setNegativeButton(R.string.transaction_delete_alert_negative) { dialogInterface, _ ->
                dialogInterface?.dismiss()
            }
            .setPositiveButton(R.string.transaction_delete_alert_positive) { _, _ ->
                viewModel.delete()
            }
            .create()
            .show()
    }

    private fun showUnsavedDialog() {
        AlertDialog.Builder(requireContext(), R.style.AlertDialog)
            .setMessage(R.string.transaction_add_alert_question)
            .setNegativeButton(R.string.transaction_add_alert_negative) { _, _ ->
                findNavController().navigateUp()
            }
            .setPositiveButton(R.string.transaction_add_alert_positive) { dialogInterface, _ ->
                dialogInterface?.dismiss()
            }
            .create()
            .show()
    }

    private fun insetsInit() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.transactionToolbar) { view, insets ->
            view.updateMargin(top = insets.top())
            insets
        }
    }

    companion object {

        fun createArgsWithCheckId(checkId: Int): Bundle = Bundle().apply {
            putInt(TransactionConst.EXTRA_ACCOUNT_ID, checkId)
        }
    }
}
