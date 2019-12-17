package ru.vincetti.vimoney.ui.home

import android.app.Application
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_home_content.view.*
import kotlinx.android.synthetic.main.stat_income_exspence.view.*
import ru.vincetti.vimoney.R
import ru.vincetti.vimoney.data.adapters.CardsListRVAdapter
import ru.vincetti.vimoney.data.sqlite.AccountDao
import ru.vincetti.vimoney.data.sqlite.AppDatabase
import ru.vincetti.vimoney.data.sqlite.TransactionDao
import ru.vincetti.vimoney.databinding.FragmentHomeBinding
import ru.vincetti.vimoney.transaction.TransactionViewModel
import ru.vincetti.vimoney.transaction.TransactionViewModelFactory
import ru.vincetti.vimoney.transaction.main.TransactionMainViewModel
import ru.vincetti.vimoney.ui.check.view.CheckViewModel
import ru.vincetti.vimoney.ui.history.HistoryFragment
import ru.vincetti.vimoney.utils.LogicMath
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment() {

    companion object {
        const val TR_MAIN_COUNT = 10
    }

    lateinit var binding: FragmentHomeBinding
    var date = Date()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentHomeBinding.inflate(inflater)
        (activity as? AppCompatActivity)?.setSupportActionBar(binding.topToolbar)

        val application = requireNotNull(this.activity).application
        val db = AppDatabase.getInstance(application)
        val viewModelFactory = HomeViewModelFactory(db.accountDao(), db.transactionDao(), application)
        val viewModel = ViewModelProviders.of(this, viewModelFactory).get(HomeViewModel::class.java)
        val transactionViewModelFactory = TransactionViewModelFactory(db.transactionDao(),db.accountDao())
        val transactionViewModel = ViewModelProviders.of(activity!!, transactionViewModelFactory).get(TransactionViewModel::class.java)

        // список карт/счетов
        val mAdapter = CardsListRVAdapter {
            val bundle = Bundle()
            bundle.putInt(CheckViewModel.EXTRA_CHECK_ID, it)
            findNavController().navigate(R.id.action_homeFragment_to_checkFragment, bundle)
        }

        val layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        val recycler = binding.homeContent.home_cards_recycle_view
        recycler.layoutManager = layoutManager
        recycler.adapter = mAdapter

        viewModel.accounts.observe(this, Observer {
            binding.homeUserBalance.text = LogicMath.userBalanceChange(it).toString()
            mAdapter.setList(it)
        })

        viewModel.lSum1.observe(this, Observer {
            if (it != null) {
                binding.homeContent.home_stat_income_txt.text = it.toString()
            }
        })

        viewModel.lSum2.observe(this, Observer {
            if (it != null) {
                binding.homeContent.home_stat_expense_txt.text = it.toString()
            }
        })

        viewInit()
        accountsLoadFromDB(viewModel, transactionViewModel)
        showTransactionsHistory()

        return binding.root
    }

    private fun viewInit() {
        binding.homeContent.home_stat_expense_txt.text = "0"
        binding.homeContent.home_stat_income_txt.text = "0"
        binding.homeFab.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_transactionMainFragment)
        }
        binding.homeContent.home_accounts_link.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_checksListFragment)
        }
        binding.homeContent.home_transactions_link.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_allHistoryFragment)
        }
        binding.homeContent.home_stat_link.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_dashboardFragment)
        }
    }

    // Show historyFragment
    private fun showTransactionsHistory() {
        val historyFragment = HistoryFragment()
        val args = Bundle()
        // set transactions count to show
        args.putInt(HistoryFragment.BUNDLE_TRANS_COUNT_NAME, TR_MAIN_COUNT)
        historyFragment.arguments = args
        childFragmentManager.beginTransaction()
                .replace(R.id.main_history_container, historyFragment)
                .commit()
    }

    /**
     * Settings & notification Activity in beta
     */
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.bottom_nav_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            //R.id.navigation_bar_notification ->  NotificationsActivity.start(this);
            //R.id.navigation_bar_settings -> SettingsActivity.start(this);
        }
        return true
    }

    // load accounts info from ViewModel (SQLite)
    private fun accountsLoadFromDB(viewModel: HomeViewModel, transactionViewModel: TransactionViewModel) {
        // get stat
        binding.homeContent.home_month.text = SimpleDateFormat("MMM").format(date)
        // setting in viewmodel Utils hashes
        viewModel.updateTransactionsViewModel(transactionViewModel)
    }
}
