package ru.vincetti.vimoney.dashboard;

import android.annotation.SuppressLint;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import moxy.InjectViewState;
import moxy.MvpPresenter;
import ru.vincetti.vimoney.MyApp;
import ru.vincetti.vimoney.data.models.TransactionStatDayModel;
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
        getViewState().setMonth(new SimpleDateFormat("MMM", Locale.getDefault()).format(cal.getTime()));
        month = new SimpleDateFormat("MM", Locale.getDefault()).format(cal.getTime());
        showProgress();
        getStat();
        getExpense();
        getIncome();
    }

    /**
     * получение данных графика
     */
    @SuppressLint("CheckResult")
    private void getStat() {
        appDatabase.transactionDao()
                .loadTransactionStatByMonth(String.valueOf(month), "2019")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(transactionListModels -> {
                    int sum = 0;
                    List<Entry> entries = new ArrayList<>();
                    entries.add(new Entry(0, sum));
                    for (TransactionStatDayModel model : transactionListModels) {
                        sum += model.getSum();
                        entries.add(new Entry(model.getDay(), sum));
                    }
                    LineDataSet dataSet = new LineDataSet(entries, "Label");
                    dataSet.setDrawFilled(true);
                    dataSet.setDrawCircles(false);
                    dataSet.setLineWidth(1.8f);
                    dataSet.setCircleRadius(4f);
                    dataSet.setFillAlpha(100);
                    dataSet.setDrawHorizontalHighlightIndicator(false);
                    // create a data object with the data sets
                    LineData lineData = new LineData(dataSet);
                    hideProgress();
                    getViewState().loadChart(lineData);
                });

    }

    /**
     * получение данных расходов
     */
    @SuppressLint("CheckResult")
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
    @SuppressLint("CheckResult")
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
