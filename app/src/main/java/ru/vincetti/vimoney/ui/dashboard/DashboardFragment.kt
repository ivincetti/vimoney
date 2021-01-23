package ru.vincetti.vimoney.ui.dashboard

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.vincetti.vimoney.databinding.FragmentDashboardBinding
import ru.vincetti.vimoney.extensions.updateMargin

@AndroidEntryPoint
class DashboardFragment : Fragment() {

    private val viewModel: DashboardViewModel by viewModels()

    private var _binding: FragmentDashboardBinding? = null
    private val binding
        get() = requireNotNull(_binding)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentDashboardBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        graphInit()
        viewsInit()
        observersInit()
        insetsInit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun viewsInit() {
        binding.settingNavigationBackBtn.setOnClickListener { viewModel.backButtonClicked() }
        binding.dashContent.dashboardMonthNext.setOnClickListener { viewModel.setMonthNext() }
        binding.dashContent.dashboardMonthPrevious.setOnClickListener { viewModel.setMonthPrev() }
        binding.dashContent.dashboardYearNext.setOnClickListener { viewModel.setYearNext() }
        binding.dashContent.dashboardYearPrevious.setOnClickListener { viewModel.setYearPrev() }
    }

    private fun observersInit() {
        viewModel.monthString.observe(viewLifecycleOwner) {
            binding.dashContent.dashboardMonth.text = it
        }
        viewModel.yearString.observe(viewLifecycleOwner) {
            binding.dashContent.dashboardYear.text = it
        }
        viewModel.dataSet.observe(viewLifecycleOwner) {
            binding.dashContent.dashboardLineChart.animate(it)
        }
        viewModel.isShowProgress.observe(viewLifecycleOwner) {
            showProgress(it)
        }
        viewModel.need2Navigate2Home.observe(viewLifecycleOwner) {
            if (it) findNavController().navigateUp()
        }
        viewModel.income.observe(viewLifecycleOwner) {
            binding.dashContent.statContent.homeStatIncomeTxt.text = it.toString()
        }
        viewModel.expense.observe(viewLifecycleOwner) {
            binding.dashContent.statContent.homeStatExpenseTxt.text = it.toString()
        }
    }

    @SuppressLint("Range")
    private fun graphInit() {
        binding.dashContent.dashboardLineChart.apply {
            gradientFillColors = intArrayOf(
                Color.parseColor("#81FFFFFF"),
                Color.TRANSPARENT
            )
            animation.duration = 400L
        }
    }

    private fun showProgress(need2Show: Boolean) {
        if (need2Show) {
            binding.dashContent.dashboardProgress.visibility = View.VISIBLE
            binding.dashContent.dashboardContainer.visibility = View.GONE
        } else {
            binding.dashContent.dashboardProgress.visibility = View.GONE
            binding.dashContent.dashboardContainer.visibility = View.VISIBLE
        }
    }

    private fun insetsInit() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.dashboardToolbar) { view, insets ->
            view.updateMargin(top = insets.systemWindowInsetTop)
            insets
        }
    }
}
