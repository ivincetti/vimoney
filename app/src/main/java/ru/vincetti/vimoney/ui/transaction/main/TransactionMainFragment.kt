package ru.vincetti.vimoney.ui.transaction.main

import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.ViewPager
import ru.vincetti.vimoney.MainViewModel
import ru.vincetti.vimoney.MainViewModelFactory
import ru.vincetti.vimoney.R
import ru.vincetti.vimoney.data.adapters.TabsFragmentPagerAdapter
import ru.vincetti.vimoney.data.models.TransactionModel
import ru.vincetti.vimoney.data.sqlite.AppDatabase
import ru.vincetti.vimoney.databinding.FragmentTransactionMainBinding
import ru.vincetti.vimoney.ui.transaction.TransactionConst

class TransactionMainFragment : Fragment() {

    private lateinit var binding: FragmentTransactionMainBinding
    private lateinit var viewModel: TransactionMainViewModel
    private lateinit var mainViewModel: MainViewModel
    private lateinit var vPager: ViewPager

    private lateinit var fragmentBundle: Bundle

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTransactionMainBinding.inflate(inflater)
        val application = requireNotNull(activity).application
        val mDb = AppDatabase.getInstance(application)
        val viewModelFactory = TransactionMainViewModelFactory(mDb.transactionDao(), mDb.accountDao())
        viewModel = ViewModelProvider(this, viewModelFactory)
                .get(TransactionMainViewModel::class.java)
        val mainViewModelFactory = MainViewModelFactory(mDb.accountDao())
        mainViewModel = ViewModelProvider(requireActivity(), mainViewModelFactory)
                .get(MainViewModel::class.java)

        fragmentBundle = Bundle()
        arguments?.let { bundle ->
            val extraTransactionId = bundle.getInt(TransactionConst.EXTRA_TRANS_ID)
            val extraAccId = bundle.getInt(TransactionConst.EXTRA_ACCOUNT_ID)
            if (extraTransactionId > 0) {
                binding.transactionNavigationDeleteBtn.visibility = View.VISIBLE
                viewModel.loadTransaction(extraTransactionId)
            } else if (extraAccId > 0) viewModel.setAccount(extraAccId)
        }

        vPager = binding.viewPager
        vPager.adapter = TabsFragmentPagerAdapter(childFragmentManager, fragmentBundle)
        vPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) { //do nothing
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

    override fun onResume() {
        super.onResume()
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            onBackPressed()
        }
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
        viewModel.transaction.observe(viewLifecycleOwner, Observer {
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
            TransactionModel.TRANSACTION_TYPE_INCOME -> {
                vPager.setCurrentItem(TransactionModel.TRANSACTION_TYPE_INCOME_TAB, true)
                setActivityTitle(TransactionModel.TRANSACTION_TYPE_INCOME_TAB)
                viewModel.saveAction = TransactionModel.TRANSACTION_TYPE_INCOME
            }
            TransactionModel.TRANSACTION_TYPE_TRANSFER -> {
                vPager.setCurrentItem(TransactionModel.TRANSACTION_TYPE_TRANSFER_TAB, true)
                setActivityTitle(TransactionModel.TRANSACTION_TYPE_TRANSFER_TAB)
                viewModel.saveAction = TransactionModel.TRANSACTION_TYPE_TRANSFER
            }
            else -> {
                vPager.setCurrentItem(TransactionModel.TRANSACTION_TYPE_SPENT_TAB, true)
                setActivityTitle(TransactionModel.TRANSACTION_TYPE_SPENT_TAB)
                viewModel.saveAction = TransactionModel.TRANSACTION_TYPE_SPENT
            }
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
                .setNegativeButton(R.string.transaction_delete_alert_negative) { dialogInterface, _ ->
                    dialogInterface?.dismiss()
                }
                .setPositiveButton(R.string.transaction_delete_alert_positive) { _, _ ->
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
                .setNegativeButton(R.string.transaction_add_alert_negative) { _, _ ->
                    findNavController().navigateUp()
                }
                .setPositiveButton(R.string.transaction_add_alert_positive) { dialogInterface, _ ->
                    dialogInterface?.dismiss()
                }
        builder.create().show()
    }

    private fun onBackPressed() {
        showUnsavedDialog()
    }
}
