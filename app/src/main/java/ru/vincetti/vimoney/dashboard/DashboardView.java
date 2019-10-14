package ru.vincetti.vimoney.dashboard;

import com.github.mikephil.charting.data.LineData;

import moxy.MvpView;
import moxy.viewstate.strategy.AddToEndStrategy;
import moxy.viewstate.strategy.StateStrategyType;

@StateStrategyType(AddToEndStrategy.class)
public interface DashboardView extends MvpView {
    void loadChart(LineData lineData);

    void loadStatExpense(String expense);

    void loadStatIncome(String income);

    void showProgress();

    void hideProgress();
}
