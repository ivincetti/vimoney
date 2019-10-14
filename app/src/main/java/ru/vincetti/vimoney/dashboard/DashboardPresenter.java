package ru.vincetti.vimoney.dashboard;

import android.text.format.DateFormat;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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

    private AppDatabase appDatabase;
    private Calendar cal;
    private String month;

    DashboardPresenter() {
        appDatabase = AppDatabase.getInstance(MyApp.getAppContext());

        cal = Calendar.getInstance();
        cal.setTime(new Date());

        getData();
    }

    void showProgress() {
        getViewState().showProgress();
    }

    void hideProgress() {
        getViewState().hideProgress();
    }

    void getData() {
        getViewState().setMonth(new SimpleDateFormat("MMM").format(cal.getTime()));
        month = new SimpleDateFormat("MM").format(cal.getTime());
        showProgress();
        getStat();
        getExpense();
        getIncome();
    }

    /**
     * получение данных графика
     */
    private void getStat() {
        appDatabase.transactionDao()
                .loadTransactionStatByMonth(String.valueOf(month), "2019")
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

    /**
     * получение данных расходов
     */
    private void getExpense() {
        appDatabase.transactionDao()
                .loadSumTransactionExpenseMonthRx(String.valueOf(month), "2019")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        integer -> getViewState().loadStatExpense(String.valueOf(integer)),
                        throwable -> getViewState().loadStatExpense("0")
                );
    }

    /**
     * получение данных доходов
     */
    private void getIncome() {
        appDatabase.transactionDao()
                .loadSumTransactionIncomeMonthRx(String.valueOf(month), "2019")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        integer -> getViewState().loadStatIncome(String.valueOf(integer)),
                        throwable -> getViewState().loadStatIncome("0")
                );

    }


    public void setMonthPrev() {
        cal.add(Calendar.MONTH, -1);
        getData();
    }

    public void setMonthNext() {
        cal.add(Calendar.MONTH, 1);
        getData();
    }
}
