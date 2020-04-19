package ru.vincetti.vimoney.ui.transaction.main

import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.ViewPager
import kotlinx.android.synthetic.main.fragment_transaction_main.*
import ru.vincetti.vimoney.MainViewModel
import ru.vincetti.vimoney.MainViewModelFactory
import ru.vincetti.vimoney.R
import ru.vincetti.vimoney.data.adapters.TabsFragmentPagerAdapter
import ru.vincetti.vimoney.data.models.TransactionModel
import ru.vincetti.vimoney.data.sqlite.AppDatabase
import ru.vincetti.vimoney.ui.transaction.TransactionConst

class TransactionMainFragment : Fragment(R.layout.fragment_transaction_main) {

    private val viewModel: TransactionMainViewModel by viewModels { viewModelFactory }
    private val mainViewModel: MainViewModel by activityViewModels { mainViewModelFactory }

    private lateinit var viewModelFactory: TransactionMainViewModelFactory
    private lateinit var mainViewModelFactory: MainViewModelFactory
    private lateinit var fragmentBundle: Bundle

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val application = requireNotNull(activity).application
        val mDb = AppDatabase.getInstance(application)
        viewModelFactory = TransactionMainViewModelFactory(mDb.transactionDao(), mDb.accountDao())
        mainViewModelFactory = MainViewModelFactory(mDb.accountDao())

        fragmentBundle = Bundle()
        arguments?.let { bundle ->
            val extraTransactionId = bundle.getInt(TransactionConst.EXTRA_TRANS_ID)
            val extraAccId = bundle.getInt(TransactionConst.EXTRA_ACCOUNT_ID)
            if (extraTransactionId > 0) {
                transaction_navigation_delete_btn.visibility = View.VISIBLE
                viewModel.loadTransaction(extraTransactionId)
            } else if (extraAccId > 0) viewModel.setAccount(extraAccId)
        }

        view_pager.adapter = TabsFragmentPagerAdapter(childFragmentManager, fragmentBundle)
        view_pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(p: Int, pOffset: Float, pOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                setActivityTitle(position)
            }
        })
        initViews()
    }

    override fun onResume() {
        super.onResume()
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            showUnsavedDialog()
        }
    }

    override fun onPause() {
        super.onPause()

        val imm = (requireContext().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager)
        imm.hideSoftInputFromWindow(requireActivity().currentFocus!!.windowToken, 0)
    }


    private fun initViews() {
        sliding_tabs.setupWithViewPager(view_pager)
        transaction_navigation_delete_btn.setOnClickListener {
            showDeleteDialog()
        }
        setting_navigation_back_btn.setOnClickListener {
            showUnsavedDialog()
        }
        viewModel.transaction.observe(viewLifecycleOwner, Observer {
            it?.let {
                typeLoad(it.type)
            }
        })
    }

    fun setActivityTitle(position: Int) {
        when (position) {
            TransactionModel.TRANSACTION_TYPE_SPENT_TAB ->
                transaction_navigation_txt.text = getString(R.string.add_title_home_spent)
            TransactionModel.TRANSACTION_TYPE_INCOME_TAB ->
                transaction_navigation_txt.text = getString(R.string.add_title_home_income)
            TransactionModel.TRANSACTION_TYPE_TRANSFER_TAB ->
                transaction_navigation_txt.text = getString(R.string.add_title_home_transfer)
//            TransactionModel.TRANSACTION_TYPE_DEBT_TAB ->
//                transaction_navigation_txt.text = getString(R.string.add_title_home_debt)
        }
    }

    /** RadioButton option load. */
    private fun typeLoad(type: Int) {
        when (type) {
            TransactionModel.TRANSACTION_TYPE_INCOME -> {
                view_pager.setCurrentItem(TransactionModel.TRANSACTION_TYPE_INCOME_TAB, true)
                setActivityTitle(TransactionModel.TRANSACTION_TYPE_INCOME_TAB)
            }
            TransactionModel.TRANSACTION_TYPE_TRANSFER -> {
                view_pager.setCurrentItem(TransactionModel.TRANSACTION_TYPE_TRANSFER_TAB, true)
                setActivityTitle(TransactionModel.TRANSACTION_TYPE_TRANSFER_TAB)
            }
            else -> {
                view_pager.setCurrentItem(TransactionModel.TRANSACTION_TYPE_SPENT_TAB, true)
                setActivityTitle(TransactionModel.TRANSACTION_TYPE_SPENT_TAB)
            }
//            TransactionModel.TRANSACTION_TYPE_DEBT ->{
//                vPager.setCurrentItem(TransactionModel.TRANSACTION_TYPE_DEBT_TAB, true)
//                setActivityTitle(TransactionModel.TRANSACTION_TYPE_DEBT_TAB)
//                }
        }
    }

    /** Delete transaction logic. */
    private fun showDeleteDialog() {
        val builder = AlertDialog.Builder(requireContext())
                .setMessage(R.string.transaction_delete_alert_question)
                .setNegativeButton(R.string.transaction_delete_alert_negative) { dialogInterface, _ ->
                    dialogInterface?.dismiss()
                }
                .setPositiveButton(R.string.transaction_delete_alert_positive) { _, _ ->
                    viewModel.delete()
                }
        builder.create().show()
    }

    /** Not saved transaction cancel dialog. */
    private fun showUnsavedDialog() {
        val builder = AlertDialog.Builder(requireContext())
                .setMessage(R.string.transaction_add_alert_question)
                .setNegativeButton(R.string.transaction_add_alert_negative) { _, _ ->
                    findNavController().navigateUp()
                }
                .setPositiveButton(R.string.transaction_add_alert_positive) { dialogInterface, _ ->
                    dialogInterface?.dismiss()
                }
        builder.create().show()
    }
}
