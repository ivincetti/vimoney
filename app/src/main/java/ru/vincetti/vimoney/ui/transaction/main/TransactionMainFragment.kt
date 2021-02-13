package ru.vincetti.vimoney.ui.transaction.main

import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.ViewPager
import dagger.hilt.android.AndroidEntryPoint
import ru.vincetti.modules.core.models.Transaction
import ru.vincetti.vimoney.R
import ru.vincetti.vimoney.databinding.FragmentTransactionMainBinding
import ru.vincetti.vimoney.extensions.updateMargin
import ru.vincetti.vimoney.ui.transaction.TransactionConst

@AndroidEntryPoint
class TransactionMainFragment : Fragment() {

    private val viewModel: TransactionMainViewModel by viewModels()

    private lateinit var fragmentBundle: Bundle

    private var _binding: FragmentTransactionMainBinding? = null
    private val binding
        get() = requireNotNull(_binding)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentTransactionMainBinding.inflate(layoutInflater)
        return binding.root
    }

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

        val imm = requireContext().getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
        requireActivity().currentFocus?.let {
            imm?.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initViews() {
        binding.transactionTabs.setupWithViewPager(binding.viewPager)
        binding.transactionNavigationDeleteBtn.setOnClickListener {
            showDeleteDialog()
        }
        binding.settingNavigationBackBtn.setOnClickListener {
            showUnsavedDialog()
        }
        viewModel.transaction.observe(viewLifecycleOwner) {
            it?.let { typeLoad(it.type) }
        }
    }

    fun setActivityTitle(position: Int) {
        binding.transactionNavigationTxt.text = when (position) {
            Transaction.TRANSACTION_TYPE_SPENT_TAB -> getString(R.string.add_title_home_spent)
            Transaction.TRANSACTION_TYPE_TRANSFER_TAB -> getString(R.string.add_title_home_transfer)
            else -> getString(R.string.add_title_home_income)
//            TransactionModel.TRANSACTION_TYPE_DEBT_TAB ->
//                transaction_navigation_txt.text = getString(R.string.add_title_home_debt)
        }
    }

    private fun typeLoad(type: Int) {
        when (type) {
            Transaction.TRANSACTION_TYPE_INCOME -> {
                binding.viewPager.setCurrentItem(Transaction.TRANSACTION_TYPE_INCOME_TAB, true)
                setActivityTitle(Transaction.TRANSACTION_TYPE_INCOME_TAB)
            }
            Transaction.TRANSACTION_TYPE_TRANSFER -> {
                binding.viewPager.setCurrentItem(Transaction.TRANSACTION_TYPE_TRANSFER_TAB, true)
                setActivityTitle(Transaction.TRANSACTION_TYPE_TRANSFER_TAB)
            }
            else -> {
                binding.viewPager.setCurrentItem(Transaction.TRANSACTION_TYPE_SPENT_TAB, true)
                setActivityTitle(Transaction.TRANSACTION_TYPE_SPENT_TAB)
            }
//            TransactionModel.TRANSACTION_TYPE_DEBT ->{
//                vPager.setCurrentItem(TransactionModel.TRANSACTION_TYPE_DEBT_TAB, true)
//                setActivityTitle(TransactionModel.TRANSACTION_TYPE_DEBT_TAB)
//                }
        }
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
            view.updateMargin(top = insets.systemWindowInsetTop)
            insets
        }
    }
}
