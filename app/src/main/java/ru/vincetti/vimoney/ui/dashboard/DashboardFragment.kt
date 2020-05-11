package ru.vincetti.vimoney.ui.dashboard

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_dashboard.*
import kotlinx.android.synthetic.main.fragment_dashboard_content.*
import kotlinx.android.synthetic.main.fragment_dashboard_content.view.*
import kotlinx.android.synthetic.main.stat_income_expense.view.*
import ru.vincetti.vimoney.R
import ru.vincetti.vimoney.data.sqlite.AppDatabase

class DashboardFragment : Fragment(R.layout.fragment_dashboard) {

    private val viewModel: DashboardViewModel by viewModels { viewModelFactory }

    private lateinit var viewModelFactory: DashboardViewModelFactory

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val application = requireNotNull(activity).application
        val db = AppDatabase.getInstance(application)
        viewModelFactory = DashboardViewModelFactory(db.transactionDao())

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
        dash_content.dashboard_year_next.setOnClickListener {
            viewModel.setYearNext()
        }
        dash_content.dashboard_year_previous.setOnClickListener {
            viewModel.setYearPrev()
        }
        viewModel.yearString.observe(viewLifecycleOwner, Observer {
            dash_content.dashboard_year.text = it
        })
        viewModel.dataSet.observe(viewLifecycleOwner, Observer {
            dashboard_lineChart.animate(it)
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
        dashboard_lineChart.gradientFillColors =
                intArrayOf(
                        Color.parseColor("#81FFFFFF"),
                        Color.TRANSPARENT
                )
        dashboard_lineChart.animation.duration = 400L
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
