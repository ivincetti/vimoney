package ru.vincetti.vimoney.ui.home

import android.os.Bundle
import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.marginBottom
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
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
import ru.vincetti.vimoney.ui.history.filter.Filter
import ru.vincetti.vimoney.utils.DatesFormat
import java.time.LocalDate
import java.util.*

class HomeFragment : Fragment(R.layout.fragment_home) {

    private val viewModel: HomeViewModel by viewModels { viewModelFactory }

    private lateinit var viewModelFactory: HomeViewModelFactory
    private lateinit var mAdapter: CardsListRVAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val application = requireActivity().application
        val db = AppDatabase.getInstance(application)
        viewModelFactory = HomeViewModelFactory(db)

        mAdapter = CardsListRVAdapter {
            val bundle = Bundle()
            bundle.putInt(EXTRA_CHECK_ID, it)
            findNavController().navigate(R.id.action_homeFragment_to_checkFragment, bundle)
        }

        fragment_home_content.home_cards_recycle_view.adapter = mAdapter

        viewInit()
        observersInit()
        insetsInit()
        showTransactionsHistory()
    }

    private fun viewInit() {
        fragment_home_content.home_month.text = DatesFormat
            .getMonthName(LocalDate.now())
            .capitalize(Locale.getDefault())

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

    private fun observersInit() {
        viewModel.userBalance.observe(viewLifecycleOwner) {
            home_user_balance.text = it.toString()
        }
        viewModel.accounts.observe(viewLifecycleOwner) {
            mAdapter.setList(it)
        }
        viewModel.homeButtonEnabled.observe(viewLifecycleOwner) {
            home_menu_update.isEnabled = it
        }
        viewModel.incomeSum.observe(viewLifecycleOwner) {
            it?.let { fragment_home_content.home_stat_income_txt.text = it.toString() }
        }
        viewModel.expenseSum.observe(viewLifecycleOwner) {
            it?.let { fragment_home_content.home_stat_expense_txt.text = it.toString() }
        }
    }

    private fun insetsInit() {
        val fabMargin = home_fab.marginBottom
        ViewCompat.setOnApplyWindowInsetsListener(home_fab) { view, insets ->
            view.updateMargin(bottom = (insets.systemWindowInsetBottom + fabMargin))
            insets
        }
        ViewCompat.setOnApplyWindowInsetsListener(top_toolbar) { view, insets ->
            view.updateMargin(top = insets.systemWindowInsetTop)
            insets
        }
        ViewCompat.setOnApplyWindowInsetsListener(main_history_container) { view, insets ->
            view.updatePadding(bottom = insets.systemWindowInsetBottom)
            insets
        }
    }

    private fun showTransactionsHistory() {
        val historyFragment = HistoryFragment()

        val args = Filter().apply {
            count = HistoryFragment.DEFAULT_CHECK_COUNT
        }.createBundle()

        historyFragment.arguments = args
        childFragmentManager.beginTransaction()
            .replace(R.id.main_history_container, historyFragment)
            .commit()
    }
}
