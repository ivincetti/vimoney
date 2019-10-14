package ru.vincetti.vimoney.dashboard;

import android.text.format.DateFormat;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.List;

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

    void getData() {
        showProgress();
        getStat();
        getExpense();
        getIncome();
    }

    private void getStat() {
        appDatabase.transactionDao()
                .loadTransactionStatByMonth("10", "2019")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(transactionListModels -> {
                    hideProgress();
                    int sum = 0;
                    List<Entry> entries = new ArrayList<Entry>();
                    for (TransactionModel model : transactionListModels) {
                        if (model.getType() == TransactionModel.TRANSACTION_TYPE_INCOME) {
                            sum += model.getSum();
                        } else if (model.getType() == TransactionModel.TRANSACTION_TYPE_SPENT) {
                            sum -= model.getSum();
                        }
                        entries.add(new Entry(
                                Float.valueOf(DateFormat.format("dd", model.getDate()).toString()), sum)
                        );
                    }
                    LineDataSet dataSet = new LineDataSet(entries, "Label");
                    LineData lineData = new LineData(dataSet);
                    getViewState().loadChart(lineData);
                });

    }

    private void getExpense() {
        appDatabase.transactionDao()
                .loadSumTransactionExpenseMonthRx("10", "2019")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(integer -> getViewState().loadStatExpense(String.valueOf(integer)));
    }

    private void getIncome() {
        appDatabase.transactionDao()
                .loadSumTransactionIncomeMonthRx("10", "2019")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(integer -> getViewState().loadStatIncome(String.valueOf(integer)));
    }
}
