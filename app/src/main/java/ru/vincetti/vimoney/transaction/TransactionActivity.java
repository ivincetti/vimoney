package ru.vincetti.vimoney.transaction;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import java.util.Date;

import ru.vincetti.vimoney.R;
import ru.vincetti.vimoney.data.AppExecutors;
import ru.vincetti.vimoney.data.models.TransactionModel;
import ru.vincetti.vimoney.data.sqlite.AppDatabase;
import ru.vincetti.vimoney.utils.TransactionMath;

public class TransactionActivity extends AppCompatActivity {
    private final static String EXTRA_TRANS_ID = "Extra_transaction_id";
    private final static int DEFAULT_TRANS_ID = -1;

    private int mTransId = DEFAULT_TRANS_ID;
    private AppDatabase mDb;

    TextView txtName, txtAccount, txtSum;
    CalendarView calView;
    Button btnSave;

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

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(EXTRA_TRANS_ID)) {
            mTransId = intent.getIntExtra(EXTRA_TRANS_ID, DEFAULT_TRANS_ID);
            btnSave.setText(getString(R.string.add_btn_update));

            LiveData<TransactionModel> transLD = mDb.transactionDao().loadTransactionById(mTransId);
            transLD.observe(this, new Observer<TransactionModel>() {
                @Override
                public void onChanged(TransactionModel transactionModel) {
                    transLD.removeObserver(this);
                    txtSum.setText(String.valueOf(transactionModel.getSum()));
                    txtAccount.setText(String.valueOf(transactionModel.getAccountId()));
                    txtName.setText(transactionModel.getDescription());
                }
            });
        }
    }

    private void initViews() {
        txtSum = findViewById(R.id.add_sum);
        txtAccount = findViewById(R.id.add_acc_name);
        txtName = findViewById(R.id.add_desc);
        btnSave = findViewById(R.id.add_btn);
        calView = findViewById(R.id.add_calendar);
        btnSave.setOnClickListener(view -> Save());
        findViewById(R.id.setting_navigation_back_btn).
                setOnClickListener(view -> finish());
    }

    private int typeEntered() {
        int checkedId = ((RadioGroup) findViewById(R.id.radioGroup)).getCheckedRadioButtonId();
        switch (checkedId) {
            case R.id.add_spent_button:
                return TransactionModel.TRANSACTION_TYPE_SPENT;
            case R.id.add_income_button:
                return TransactionModel.TRANSACTION_TYPE_INCOME;
        }
        return TransactionModel.TRANSACTION_TYPE_INCOME;
    }

    private void Save() {
        int accId = Integer.valueOf(String.valueOf(txtAccount.getText()));
        TransactionModel tmp = new TransactionModel(
                accId,
                String.valueOf(txtName.getText()),
                new Date(calView.getDate()),
                new Date(),
                typeEntered(),
                Float.valueOf(txtSum.getText().toString())
        );
        if (mTransId != DEFAULT_TRANS_ID) {
            tmp.setId(mTransId);
            AppExecutors.getsInstance().diskIO().execute(
                    () -> mDb.transactionDao().updateTransaction(tmp));
        } else {
            AppExecutors.getsInstance().diskIO().execute(
                    () -> mDb.transactionDao().insertTransaction(tmp));
        }
        AppExecutors.getsInstance().diskIO().execute(
                () -> TransactionMath.accountUpdate(getApplicationContext(), accId));

        finish();
    }
}
