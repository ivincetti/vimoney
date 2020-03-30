package ru.vincetti.vimoney.ui.home

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home_content.view.*
import kotlinx.android.synthetic.main.stat_income_exspence.view.*
import ru.vincetti.vimoney.R
import ru.vincetti.vimoney.data.adapters.CardsListRVAdapter
import ru.vincetti.vimoney.data.sqlite.AppDatabase
import ru.vincetti.vimoney.ui.check.EXTRA_CHECK_ID
import ru.vincetti.vimoney.ui.history.HistoryFragment
import ru.vincetti.vimoney.utils.userBalanceChange
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment(R.layout.fragment_home) {

    private val viewModel: HomeViewModel by viewModels { viewModelFactory }

    private lateinit var viewModelFactory: HomeViewModelFactory
    private var date = Date()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as? AppCompatActivity)?.setSupportActionBar(top_toolbar)

        val application = requireActivity().application
        val db = AppDatabase.getInstance(application)
        viewModelFactory = HomeViewModelFactory(db.accountDao(), db.transactionDao())

        /** Список карт/счетов. */
        val mAdapter = CardsListRVAdapter {
            val bundle = Bundle()
            bundle.putInt(EXTRA_CHECK_ID, it)
            findNavController().navigate(R.id.action_homeFragment_to_checkFragment, bundle)
        }

        val llManager = LinearLayoutManager(
                requireActivity(),
                LinearLayoutManager.HORIZONTAL,
                false)
        val recycler = fragment_home_content.home_cards_recycle_view
        recycler.layoutManager = llManager
        recycler.adapter = mAdapter

        viewModel.accounts.observe(viewLifecycleOwner, Observer {
            home_user_balance.text = userBalanceChange(it).toString()
            mAdapter.setList(it)
        })
        viewModel.incomeSum.observe(viewLifecycleOwner, Observer {
            it?.let {
                fragment_home_content.home_stat_income_txt.text = it.toString()
            }
        })
        viewModel.expenseSum.observe(viewLifecycleOwner, Observer {
            it?.let {
                fragment_home_content.home_stat_expense_txt.text = it.toString()
            }
        })

        viewInit()
        showTransactionsHistory()
    }

    private fun viewInit() {
        fragment_home_content.home_month.text = SimpleDateFormat("MMM").format(date)

        home_fab.setOnClickListener {
            findNavController().navigate(R.id.action_global_transactionMainFragment)
        }
        fragment_home_content.home_accounts_link.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_checksListFragment)
        }
        fragment_home_content.home_transactions_link.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_allHistoryFragment)
        }
        fragment_home_content.home_stat_link.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_dashboardFragment)
        }
        home_menu_notification.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_notificationFragment)
        }
        home_menu_settings.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_settingsFragment)
        }
    }

    /** Show historyFragment. */
    private fun showTransactionsHistory() {
        childFragmentManager.beginTransaction()
                .replace(R.id.main_history_container, HistoryFragment())
                .commit()
    }
}