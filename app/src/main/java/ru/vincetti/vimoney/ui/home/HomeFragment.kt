package ru.vincetti.vimoney.ui.home

import android.os.Bundle
import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.marginBottom
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home_content.*
import kotlinx.android.synthetic.main.fragment_home_content.view.*
import kotlinx.android.synthetic.main.stat_income_expense.view.*
import ru.vincetti.vimoney.R
import ru.vincetti.vimoney.data.adapters.CardsListRVAdapter
import ru.vincetti.vimoney.data.sqlite.AppDatabase
import ru.vincetti.vimoney.extensions.updateMargin
import ru.vincetti.vimoney.ui.check.EXTRA_CHECK_ID
import ru.vincetti.vimoney.ui.history.HistoryFragment
import ru.vincetti.vimoney.utils.DatesFormat
import java.time.LocalDate

class HomeFragment : Fragment(R.layout.fragment_home) {

    private val viewModel: HomeViewModel by viewModels { viewModelFactory }

    private lateinit var viewModelFactory: HomeViewModelFactory

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val application = requireActivity().application
        val db = AppDatabase.getInstance(application)
        viewModelFactory = HomeViewModelFactory(db)

        /** Список карт/счетов. */
        val mAdapter = CardsListRVAdapter {
            val bundle = Bundle()
            bundle.putInt(EXTRA_CHECK_ID, it)
            findNavController().navigate(R.id.action_homeFragment_to_checkFragment, bundle)
        }

        val recycler = fragment_home_content.home_cards_recycle_view
        recycler.adapter = mAdapter

        viewModel.userBalance.observe(viewLifecycleOwner, Observer {
            home_user_balance.text = it.toString()
        })
        viewModel.accounts.observe(viewLifecycleOwner, Observer {
            mAdapter.setList(it)
        })
        viewModel.homeButtonEnabled.observe(viewLifecycleOwner, Observer {
            home_menu_update.isEnabled = it
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
        insetsInit()
        showTransactionsHistory()
    }

    private fun viewInit() {
        fragment_home_content.home_month.text = DatesFormat.getMonthName(LocalDate.now())

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
        home_menu_update.setOnClickListener { viewModel.updateAllAccounts() }
    }

    private fun insetsInit() {
        val fabMargin = home_fab.marginBottom
        ViewCompat.setOnApplyWindowInsetsListener(home_fab) { _, insets ->
            home_fab.updateMargin(bottom = (insets.systemWindowInsetBottom + fabMargin))
            insets
        }

        ViewCompat.setOnApplyWindowInsetsListener(top_toolbar) { _, insets ->
            top_toolbar.updateMargin(top = insets.systemWindowInsetTop)
            insets
        }

        ViewCompat.setOnApplyWindowInsetsListener(main_history_container_root) { _, insets ->
            main_history_container_root.updatePadding(bottom = insets.systemWindowInsetBottom)
            insets
        }
    }

    /** Show historyFragment. */
    private fun showTransactionsHistory() {
        childFragmentManager.beginTransaction()
                .replace(R.id.main_history_container, HistoryFragment())
                .commit()
    }
}