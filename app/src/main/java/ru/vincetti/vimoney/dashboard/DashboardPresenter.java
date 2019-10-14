package ru.vincetti.vimoney.dashboard;

import moxy.InjectViewState;
import moxy.MvpPresenter;

@InjectViewState
public class DashboardPresenter extends MvpPresenter<DashboardView> {

    public DashboardPresenter() {
    }

    public void changeText(String text){
        getViewState().changeText(text);
    }
}
