package ru.vincetti.vimoney.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.core.view.marginBottom
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.vincetti.modules.core.models.Filter
import ru.vincetti.modules.core.utils.DatesFormat
import ru.vincetti.vimoney.BuildConfig
import ru.vincetti.vimoney.R
import ru.vincetti.vimoney.databinding.FragmentHomeBinding
import ru.vincetti.vimoney.extensions.updateMargin
import ru.vincetti.vimoney.ui.history.HistoryFragment
import java.time.LocalDate
import java.util.*

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private val viewModel: HomeViewModel by viewModels()

    private lateinit var adapter: HomeCardsAdapter

    private var _binding: FragmentHomeBinding? = null
    private val binding
        get() = requireNotNull(_binding)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = HomeCardsAdapter(
            object : HomeCardViewHolder.Actions {
                override fun onCardClicked(id: Int) {
                    viewModel.clickOnCheck(id)
                }
            }
        )

        binding.fragmentHomeContent.homeCardsRecycleView.adapter = adapter

        viewInit()
        observersInit()
        insetsInit()
        showTransactionsHistory()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun viewInit() {
        if (BuildConfig.DEBUG) {
            binding.homeMenuNotification.apply {
                visibility = View.VISIBLE
                setOnClickListener {
                    findNavController().navigate(R.id.action_homeFragment_to_notificationFragment)
                }
            }
        }
        binding.homeFab.setOnClickListener {
            findNavController().navigate(R.id.action_global_transactionMainFragment)
        }
        binding.homeMenuSettings.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_settingsFragment)
        }
        binding.homeMenuUpdate.setOnClickListener { viewModel.updateAllAccounts() }

        binding.fragmentHomeContent.homeMonth.text = DatesFormat
            .getMonthName(LocalDate.now())
            .capitalize(Locale.getDefault())
        binding.fragmentHomeContent.homeAccountsLink.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_checksListFragment)
        }
        binding.fragmentHomeContent.homeTransactionsLink.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_allHistoryFragment)
        }
        binding.fragmentHomeContent.homeStatLink.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_dashboardFragment)
        }
    }

    private fun observersInit() {
        viewModel.userBalance.observe(viewLifecycleOwner) {
            binding.homeUserBalance.text = it.toString()
        }
        viewModel.accounts.observe(viewLifecycleOwner) {
            adapter.setList(it)
        }
        viewModel.homeButtonEnabled.observe(viewLifecycleOwner) {
            binding.homeMenuUpdate.isEnabled = it
        }
        viewModel.incomeSum.observe(viewLifecycleOwner) {
            it?.let { binding.fragmentHomeContent.homeStatContainer.homeStatIncomeTxt.text = it.toString() }
        }
        viewModel.expenseSum.observe(viewLifecycleOwner) {
            it?.let { binding.fragmentHomeContent.homeStatContainer.homeStatExpenseTxt.text = it.toString() }
        }
        viewModel.needNavigate2Check.observe(viewLifecycleOwner) {
            go2Check(it)
        }
    }

    private fun insetsInit() {
        val fabMargin = binding.homeFab.marginBottom
        ViewCompat.setOnApplyWindowInsetsListener(binding.homeFab) { view, insets ->
            view.updateMargin(bottom = (insets.systemWindowInsetBottom + fabMargin))
            insets
        }
        ViewCompat.setOnApplyWindowInsetsListener(binding.topToolbar) { view, insets ->
            view.updateMargin(top = insets.systemWindowInsetTop)
            insets
        }
        ViewCompat.setOnApplyWindowInsetsListener(binding.fragmentHomeContent.mainHistoryContainer) { view, insets ->
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

    private fun go2Check(id: Int) {
        val bundle = bundleOf("checkID" to id)
        findNavController().navigate(R.id.action_homeFragment_to_checkFragment, bundle)
    }
}
