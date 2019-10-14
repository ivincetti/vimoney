package ru.vincetti.vimoney.dashboard;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.LineData;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import moxy.MvpAppCompatActivity;
import moxy.presenter.InjectPresenter;
import ru.vincetti.vimoney.R;

public class DashboardActivity extends MvpAppCompatActivity implements DashboardView {

    @InjectPresenter
    DashboardPresenter mPresenter;

    @BindView(R.id.dashboard_chart)
    LineChart dashboardChart;

    @BindView(R.id.dashboard_container)
    View container;

    @BindView(R.id.dashboard_progress)
    ProgressBar progressBar;

    @BindView(R.id.home_stat_income_txt)
    TextView incomeTxt;

    @BindView(R.id.home_stat_expense_txt)
    TextView expenseTxt;

    public static void start(Context context) {
        context.startActivity(new Intent(context, DashboardActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        ButterKnife.bind(this);

        dashboardChart.getLegend().setEnabled(false);
        dashboardChart.getDescription().setEnabled(false);
        XAxis bottom = dashboardChart.getXAxis();
        bottom.setPosition(XAxis.XAxisPosition.BOTTOM);
        bottom.setDrawGridLines(false);
//        bottom.setGranularity(1f);
        // data has AxisDependency.LEFT
        YAxis left = dashboardChart.getAxisLeft();
//        left.setDrawLabels(false); // no axis labels
        left.setDrawAxisLine(false); // no axis line
        left.setDrawGridLines(false); // no grid lines
        left.setDrawZeroLine(true); // draw a zero line
        dashboardChart.getAxisRight().setEnabled(false); // no right axis
    }

    @OnClick(R.id.setting_navigation_back_btn)
    void back() {
        finish();
    }

    @Override
    public void loadChart(LineData lineData) {
        dashboardChart.setData(lineData);
        dashboardChart.invalidate(); // refresh
    }

    @Override
    public void loadStatIncome(String income){
        incomeTxt.setText(income);
    }

    @Override
    public void loadStatExpense(String expense){
        expenseTxt.setText(expense);
    }

    @Override
    public void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
        container.setVisibility(View.GONE);
    }

    @Override
    public void hideProgress() {
        progressBar.setVisibility(View.GONE);
        container.setVisibility(View.VISIBLE);
    }
}
