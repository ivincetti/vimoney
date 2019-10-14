package ru.vincetti.vimoney.dashboard;

import moxy.MvpView;
import moxy.viewstate.strategy.AddToEndStrategy;
import moxy.viewstate.strategy.StateStrategyType;

@StateStrategyType(AddToEndStrategy.class)
public interface DashboardView extends MvpView {
    void changeText(String text);
}
