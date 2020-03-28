package ru.vincetti.vimoney.ui.dashboard

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import kotlinx.android.synthetic.main.fragment_dashboard.*
import kotlinx.android.synthetic.main.fragment_dashboard_content.view.*
import kotlinx.android.synthetic.main.stat_income_exspence.view.*
import ru.vincetti.vimoney.R
import ru.vincetti.vimoney.data.sqlite.AppDatabase

class DashboardFragment : Fragment(R.layout.fragment_dashboard) {

    private val viewModel: DashboardViewModel by viewModels { viewModelFactory }

    private lateinit var viewModelFactory: DashboardViewModelFactory
    private lateinit var dashboardChart: LineChart

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val application = requireNotNull(activity).application
        val db = AppDatabase.getInstance(application)
        viewModelFactory = DashboardViewModelFactory(db.transactionDao())

        dashboardChart = dash_content.dashboard_chart
        graphInit()

        setting_navigation_back_btn.setOnClickListener {
            viewModel.homeButton()
        }

        dash_content.dashboard_month_next.setOnClickListener {
            viewModel.setMonthNext()
        }
        dash_content.dashboard_month_previous.setOnClickListener {
            viewModel.setMonthPrev()
        }
        viewModel.monthString.observe(viewLifecycleOwner, Observer {
            dash_content.dashboard_month.text = it
        })
        viewModel.dataSet.observe(viewLifecycleOwner, Observer {
            dashboardChart.data = it
            dashboardChart.invalidate()
        })
        viewModel.isShowProgress.observe(viewLifecycleOwner, Observer {
            showProgress(it)
        })
        viewModel.need2Navigate2Home.observe(viewLifecycleOwner, Observer {
            if (it) findNavController().navigate(R.id.action_dashboardFragment_to_homeFragment)
        })
        viewModel.income.observe(viewLifecycleOwner, Observer {
            dash_content.home_stat_income_txt.text = it.toString()
        })
        viewModel.expense.observe(viewLifecycleOwner, Observer {
            dash_content.home_stat_expense_txt.text = it.toString()
        })
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
            dash_content.dashboard_progress.visibility = View.VISIBLE
            dash_content.dashboard_container.visibility = View.GONE
        } else {
            dash_content.dashboard_progress.visibility = View.GONE
            dash_content.dashboard_container.visibility = View.VISIBLE
        }
    }
}