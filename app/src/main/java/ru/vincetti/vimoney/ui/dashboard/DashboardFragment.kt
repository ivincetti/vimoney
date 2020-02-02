package ru.vincetti.vimoney.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import kotlinx.android.synthetic.main.fragment_dashboard_content.view.*
import kotlinx.android.synthetic.main.stat_income_exspence.view.*
import ru.vincetti.vimoney.R
import ru.vincetti.vimoney.data.sqlite.AppDatabase
import ru.vincetti.vimoney.databinding.FragmentDashboardBinding

class DashboardFragment : Fragment() {
    private lateinit var dashboardChart: LineChart
    lateinit var binding: FragmentDashboardBinding

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDashboardBinding.inflate(inflater)

        val application = requireNotNull(this.activity).application
        val db = AppDatabase.getInstance(application)
        val viewModelFactory = DashboardViewModelFactory(db.transactionDao())
        val viewModel = ViewModelProvider(this, viewModelFactory)
                .get(DashboardViewModel::class.java)

        dashboardChart = binding.dashContent.dashboard_chart
        graphInit()

        binding.settingNavigationBackBtn.setOnClickListener {
            findNavController().navigate(R.id.action_dashboardFragment_to_homeFragment)
        }

        binding.dashContent.dashboard_month_next.setOnClickListener {
            viewModel.setMonthNext()
        }

        binding.dashContent.dashboard_month_previous.setOnClickListener {
            viewModel.setMonthPrev()
        }

        viewModel.monthString.observe(this, Observer {
            binding.dashContent.dashboard_month.text = it
        })

        viewModel.dataSet.observe(this, Observer {
            dashboardChart.data = it
            dashboardChart.invalidate() // refresh
        })

        viewModel.isShowProgress.observe(this, Observer {
            it?.let {
                showProgress(it)
            }
        })

        viewModel.income.observe(this, Observer {
            binding.dashContent.home_stat_income_txt.text = it.toString()
        })

        viewModel.expense.observe(this, Observer {
            binding.dashContent.home_stat_expense_txt.text = it.toString()
        })

        return binding.root
    }

    private fun graphInit() {
        dashboardChart.legend.isEnabled = false
        dashboardChart.description.isEnabled = false
        val bottom = dashboardChart.xAxis
        bottom.position = XAxis.XAxisPosition.BOTTOM
        bottom.setDrawGridLines(false)
        val left = dashboardChart.axisLeft
        left.setDrawAxisLine(false) // no axis line
        left.setDrawGridLines(false) // no grid lines
        left.setDrawZeroLine(true) // draw a zero line
        dashboardChart.axisRight.isEnabled = false // no right axis
    }

    private fun showProgress(need2Show: Boolean) {
        if (need2Show) {
            binding.dashContent.dashboard_progress.visibility = View.VISIBLE
            binding.dashContent.dashboard_container.visibility = View.GONE
        } else {
            binding.dashContent.dashboard_progress.visibility = View.GONE
            binding.dashContent.dashboard_container.visibility = View.VISIBLE
        }
    }
}