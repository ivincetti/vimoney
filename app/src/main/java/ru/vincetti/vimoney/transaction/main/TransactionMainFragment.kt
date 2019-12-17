package ru.vincetti.vimoney.transaction.main

import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.ViewPager
import ru.vincetti.vimoney.R
import ru.vincetti.vimoney.data.adapters.TabsFragmentPagerAdapter
import ru.vincetti.vimoney.data.models.TransactionModel
import ru.vincetti.vimoney.data.sqlite.AppDatabase
import ru.vincetti.vimoney.databinding.FragmentTransactionMainBinding
import ru.vincetti.vimoney.transaction.TransactionViewModel
import ru.vincetti.vimoney.transaction.TransactionViewModelFactory

class TransactionMainFragment : Fragment() {

    companion object {
        const val EXTRA_TRANS_ID = "Extra_transaction_id"
        const val EXTRA_ACCOUNT_ID = "Extra_account_id"
    }


    private lateinit var binding: FragmentTransactionMainBinding
    private lateinit var viewModel: TransactionMainViewModel
    private lateinit var trViewModel: TransactionViewModel
    private lateinit var vPager: ViewPager

    private lateinit var fragmentBundle: Bundle

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentTransactionMainBinding.inflate(inflater)
        val application = requireNotNull(activity).application
        val mDb = AppDatabase.getInstance(application)
        val viewModelFactory = TransactionMainViewModelFactory(mDb.transactionDao(), mDb.accountDao(), application)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(TransactionMainViewModel::class.java)
        val transactionViewModelFactory = TransactionViewModelFactory(mDb.transactionDao(), mDb.accountDao())
        trViewModel = ViewModelProviders.of(this, transactionViewModelFactory).get(TransactionViewModel::class.java)

        fragmentBundle = Bundle()

        arguments?.let { bundle ->
            if (bundle.getInt(EXTRA_TRANS_ID) != null
                    && bundle.getInt(EXTRA_TRANS_ID) != TransactionModel.DEFAULT_ID) {
                val mTransId = bundle.getInt(EXTRA_TRANS_ID)
                fragmentBundle.putInt(EXTRA_TRANS_ID, mTransId)
                binding.transactionNavigationDeleteBtn.visibility = View.VISIBLE
                viewModel.loadTransaction(mTransId)
            } else {
                bundle.getInt(EXTRA_ACCOUNT_ID)?.let {
                    viewModel.setAccount(it)
                }
            }
        }

        vPager = binding.viewPager
        vPager.adapter = TabsFragmentPagerAdapter(childFragmentManager, fragmentBundle)
        vPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
                //do nothing
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                //do nothing
            }

            override fun onPageSelected(position: Int) {
                setActivityTitle(position)
            }
        })

        initViews()

        return binding.root
    }

    override fun onPause() {
        super.onPause()

        val imm = (requireContext().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager)
        imm.hideSoftInputFromWindow(activity!!.currentFocus!!.windowToken, 0)
    }


    private fun initViews() {
        binding.slidingTabs.setupWithViewPager(vPager)

        binding.transactionNavigationDeleteBtn.setOnClickListener {
            showDeleteDialog()
        }
        binding.settingNavigationBackBtn.setOnClickListener {
            showUnsavedDialog()
        }
        viewModel.transaction.observe(this, Observer {
            it?.let {
                typeLoad(it.type)
            }
        })
    }

    fun setActivityTitle(position: Int) {
        when (position) {
            TransactionModel.TRANSACTION_TYPE_SPENT_TAB ->
                binding.transactionNavigationTxt.text = getString(R.string.add_title_home_spent)
            TransactionModel.TRANSACTION_TYPE_INCOME_TAB ->
                binding.transactionNavigationTxt.text = getString(R.string.add_title_home_income)
            TransactionModel.TRANSACTION_TYPE_TRANSFER_TAB ->
                binding.transactionNavigationTxt.text = getString(R.string.add_title_home_transfer)
//            TransactionModel.TRANSACTION_TYPE_DEBT_TAB ->
//                binding.transactionNavigationTxt.text = getString(R.string.add_title_home_debt)
        }
    }

    // radioButton option load
    private fun typeLoad(type: Int) {
        when (type) {
            else -> {
                vPager.setCurrentItem(TransactionModel.TRANSACTION_TYPE_SPENT_TAB, true)
                setActivityTitle(TransactionModel.TRANSACTION_TYPE_SPENT_TAB)
            }
//            TransactionModel.TRANSACTION_TYPE_SPENT -> {
//                vPager.setCurrentItem(TransactionModel.TRANSACTION_TYPE_SPENT_TAB, true)
//                setActivityTitle(TransactionModel.TRANSACTION_TYPE_SPENT_TAB)
//            }
//            TransactionModel.TRANSACTION_TYPE_INCOME -> {
//                vPager.setCurrentItem(TransactionModel.TRANSACTION_TYPE_INCOME_TAB, true)
//                setActivityTitle(TransactionModel.TRANSACTION_TYPE_INCOME_TAB)
//            }
//            TransactionModel.TRANSACTION_TYPE_TRANSFER -> {
//                vPager.setCurrentItem(TransactionModel.TRANSACTION_TYPE_TRANSFER_TAB, true)
//                setActivityTitle(TransactionModel.TRANSACTION_TYPE_TRANSFER_TAB)
//            }
//            TransactionModel.TRANSACTION_TYPE_DEBT ->{
//                vPager.setCurrentItem(TransactionModel.TRANSACTION_TYPE_DEBT_TAB, true)
//                setActivityTitle(TransactionModel.TRANSACTION_TYPE_DEBT_TAB)
//                }
        }
    }

    // delete transaction logic
    private fun showDeleteDialog() {
        val builder = AlertDialog.Builder(requireContext())
                .setMessage(R.string.transaction_delete_alert_question)
                .setNegativeButton(R.string.transaction_delete_alert_negative)
                { dialogInterface, _ ->
                    dialogInterface?.dismiss()
                }
                .setPositiveButton(R.string.transaction_delete_alert_positive)
                { _, _ ->
                    // delete query
                    viewModel.delete()
                    findNavController().navigateUp()
                }
        builder.create().show()
    }

    // not saved transaction cancel dialog
    private fun showUnsavedDialog() {
        val builder = AlertDialog.Builder(requireContext())
                .setMessage(R.string.transaction_add_alert_question)
                .setNegativeButton(R.string.transaction_add_alert_negative)
                { _, _ ->
                    findNavController().navigateUp()
                }
                .setPositiveButton(R.string.transaction_add_alert_positive)
                { dialogInterface, _ ->
                    dialogInterface?.dismiss()
                }
        builder.create().show()
    }

    //TODO onBack
//    fun onBackPressed()
//    {
//        showUnsavedDialog();
//    }
}
