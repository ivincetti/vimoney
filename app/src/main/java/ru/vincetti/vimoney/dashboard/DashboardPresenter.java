package ru.vincetti.vimoney.dashboard;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import moxy.InjectViewState;
import moxy.MvpPresenter;
import ru.vincetti.vimoney.MyApp;
import ru.vincetti.vimoney.data.models.TransactionModel;
import ru.vincetti.vimoney.data.sqlite.AppDatabase;

@InjectViewState
class DashboardPresenter extends MvpPresenter<DashboardView> {

    AppDatabase appDatabase;

    DashboardPresenter() {
        appDatabase = AppDatabase.getInstance(MyApp.getAppContext());
        getData();
    }

    void showProgress() {
        getViewState().showProgress();
    }

    void hideProgress() {
        getViewState().hideProgress();
    }

    void changeText(String text) {
        getViewState().changeText(text);
    }

    void getData() {
        showProgress();
        appDatabase.transactionDao()
                .loadTransactionStatByMonth("10", "2019")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(transactionListModels -> {
                    StringBuilder st = new StringBuilder();
                    for(TransactionModel model: transactionListModels){
                        st.append(model.getDate() + " " + model.getSum() + "\n");
                    }
                    changeText(st.toString());
                    hideProgress();
                });
    }
}
