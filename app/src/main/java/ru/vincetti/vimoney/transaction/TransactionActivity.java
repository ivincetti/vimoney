package ru.vincetti.vimoney.transaction;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import ru.vincetti.vimoney.R;
import ru.vincetti.vimoney.data.AppExecutors;
import ru.vincetti.vimoney.data.models.TransactionModel;
import ru.vincetti.vimoney.data.sqlite.AppDatabase;
import ru.vincetti.vimoney.utils.LogicMath;

public class TransactionActivity extends AppCompatActivity {
    private final static String EXTRA_TRANS_ID = "Extra_transaction_id";
    private final static int DEFAULT_TRANS_ID = -1;

    private int mTransId = DEFAULT_TRANS_ID;
    private AppDatabase mDb;

    TextView txtName, txtAccount, txtSum, txtDate;
    Button btnSave;
    RadioGroup typeRadioGroup;
    Date mDate;

    public static void start(Context context) {
        Intent starter = new Intent(context, TransactionActivity.class);
        context.startActivity(starter);
    }

    public static void start(Context context, int id) {
        Intent starter = new Intent(context, TransactionActivity.class);
        starter.putExtra(EXTRA_TRANS_ID, id);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        initViews();
        mDb = AppDatabase.getInstance(this);

        mDate = new Date();

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(EXTRA_TRANS_ID)) {
            mTransId = intent.getIntExtra(EXTRA_TRANS_ID, DEFAULT_TRANS_ID);
            btnSave.setText(getString(R.string.add_btn_update));
            findViewById(R.id.transaction_navigation_delete_btn).setVisibility(View.VISIBLE);

            LiveData<TransactionModel> transLD = mDb.transactionDao().loadTransactionById(mTransId);
            transLD.observe(this, new Observer<TransactionModel>() {
                @Override
                public void onChanged(TransactionModel transactionModel) {
                    transLD.removeObserver(this);
                    txtSum.setText(String.valueOf(transactionModel.getSum()));
                    txtAccount.setText(String.valueOf(transactionModel.getAccountId()));
                    txtName.setText(transactionModel.getDescription());
                    mDate = transactionModel.getDate();
                    txtDate.setText(DateFormat
                            .getDateInstance(DateFormat.MEDIUM).format(transactionModel.getDate()));
                    typeLoad(transactionModel.getType());
                }
            });
        }
    }

    private void initViews() {
        txtSum = findViewById(R.id.add_sum);
        txtAccount = findViewById(R.id.add_acc_name);
        txtName = findViewById(R.id.add_desc);
        txtDate = findViewById(R.id.add_date_txt);
        txtDate.setOnClickListener(view -> showDateDialog());
        btnSave = findViewById(R.id.add_btn);
        btnSave.setOnClickListener(view -> save());
        typeRadioGroup = findViewById(R.id.radioGroup);

        findViewById(R.id.setting_navigation_back_btn).
                setOnClickListener(view -> finish());
        findViewById(R.id.transaction_navigation_add_btn)
                .setOnClickListener(view -> save());
        findViewById(R.id.transaction_navigation_delete_btn)
                .setOnClickListener(view -> delete());

        findViewById(R.id.setting_navigation_back_btn).setOnClickListener(
                view -> showUnsavedDialog());
    }

    private void showDateDialog(){
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(mDate);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (datePicker, year, month, day) -> {
            GregorianCalendar tmpCal = new GregorianCalendar(year, month, day);
            mDate = tmpCal.getTime();
            txtDate.setText(DateFormat
                    .getDateInstance(DateFormat.MEDIUM).format(mDate));
        },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    // radioButton clicked option selected
    private int typeEntered() {
        int checkedId = typeRadioGroup.getCheckedRadioButtonId();
        switch (checkedId) {
            case R.id.add_spent_button:
                return TransactionModel.TRANSACTION_TYPE_SPENT;
            case R.id.add_income_button:
                return TransactionModel.TRANSACTION_TYPE_INCOME;
        }
        return TransactionModel.TRANSACTION_TYPE_INCOME;
    }

    // radioButton option load
    private void typeLoad(int type) {
        if (type == TransactionModel.TRANSACTION_TYPE_INCOME) {
            typeRadioGroup.check(R.id.add_income_button);
        } else if (type == TransactionModel.TRANSACTION_TYPE_SPENT) {
            typeRadioGroup.check(R.id.add_spent_button);
        }
    }

    // save transaction logic
    private void save() {
        int accId = Integer.valueOf(String.valueOf(txtAccount.getText()));
        TransactionModel tmp = new TransactionModel(
                accId,
                String.valueOf(txtName.getText()),
                mDate,
                new Date(),
                typeEntered(),
                Float.valueOf(txtSum.getText().toString())
        );

        if (mTransId != DEFAULT_TRANS_ID) {
            // update logic
            tmp.setId(mTransId);
            AppExecutors.getsInstance().diskIO().execute(
                    () -> mDb.transactionDao().updateTransaction(tmp));
        } else {
            // new transaction
            AppExecutors.getsInstance().diskIO().execute(
                    () -> mDb.transactionDao().insertTransaction(tmp));
        }
        // update balance for current (accId) account
        AppExecutors.getsInstance().diskIO().execute(
                () -> LogicMath.accountBalanceUpdateById(getApplicationContext(), accId));

        finish();
    }

    // delete transaction logic
    private void delete() {
        int accId = Integer.valueOf(String.valueOf(txtAccount.getText()));

        if (mTransId != DEFAULT_TRANS_ID) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this)
                    .setMessage(R.string.transaction_delete_alert_question)
                    .setNegativeButton(R.string.transaction_delete_alert_negative,
                            (dialogInterface, i) -> {
                                if (dialogInterface != null) dialogInterface.dismiss();
                            })
                    .setPositiveButton(R.string.transaction_delete_alert_positive,
                            (dialogInterface, i) -> {
                                // delete query
                                AppExecutors.getsInstance().diskIO().execute(
                                        () -> mDb.transactionDao().deleteTransactionById(mTransId));
                                // update balance for current (accId) account
                                AppExecutors.getsInstance().diskIO().execute(
                                        () -> LogicMath.accountBalanceUpdateById(getApplicationContext(), accId));
                                finish();

                            });

            builder.create().show();
        }
    }

    // not saved transaction cancel dialog
    private void showUnsavedDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setMessage(R.string.transaction_add_alert_question)
                .setNegativeButton(R.string.transaction_add_alert_negative,
                        (dialogInterface, i) -> finish())
                .setPositiveButton(R.string.transaction_add_alert_positive,
                        (dialogInterface, i) -> {
                            if (dialogInterface != null) dialogInterface.dismiss();
                        });

        builder.create().show();
    }

    @Override
    public void onBackPressed() {
        showUnsavedDialog();
    }
}
