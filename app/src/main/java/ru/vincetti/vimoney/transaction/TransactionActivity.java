package ru.vincetti.vimoney.transaction;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import ru.vincetti.vimoney.R;
import ru.vincetti.vimoney.data.AppExecutors;
import ru.vincetti.vimoney.data.adapters.TabsFragmentPagerAdapter;
import ru.vincetti.vimoney.data.models.TransactionModel;
import ru.vincetti.vimoney.data.sqlite.AppDatabase;
import ru.vincetti.vimoney.utils.LogicMath;

public class TransactionActivity extends AppCompatActivity {
    public final static String EXTRA_TRANS_ID = "Extra_transaction_id";
    public final static String EXTRA_ACCOUNT_ID = "Extra_account_id";

    private AppDatabase mDb;
    private int mAccID = TransactionModel.DEFAULT_ID;
    private int mTransId = TransactionModel.DEFAULT_ID;

    ViewPager vPager;
    TransactionModel mTransaction;

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

        final TransactionViewModel viewModel =
                ViewModelProviders.of(this).get(TransactionViewModel.class);
        mTransaction = new TransactionModel();

        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra(EXTRA_TRANS_ID)) {
                mTransId = intent.getIntExtra(EXTRA_TRANS_ID, TransactionModel.DEFAULT_ID);
                findViewById(R.id.transaction_navigation_delete_btn).setVisibility(View.VISIBLE);
                LiveData<TransactionModel> transLD = mDb.transactionDao().loadTransactionById(mTransId);
                transLD.observe(this, new Observer<TransactionModel>() {
                    @Override
                    public void onChanged(TransactionModel transactionModel) {
                        transLD.removeObserver(this);
                        typeLoad(transactionModel.getType());
                        mAccID = transactionModel.getAccountId();
                        mTransaction.copyFrom(transactionModel);
                    }
                });
            } else if (intent.hasExtra(EXTRA_ACCOUNT_ID)) {
                int mAccID = intent.getIntExtra(EXTRA_ACCOUNT_ID, TransactionModel.DEFAULT_ID);
                mTransaction.setAccountId(mAccID);
            }
        }
        viewModel.setTransaction(mTransaction);
    }

    private void initViews() {
        mDb = AppDatabase.getInstance(this);

        vPager = findViewById(R.id.view_pager);
        TabLayout tabLayout = findViewById(R.id.sliding_tabs);
        vPager.setAdapter(new TabsFragmentPagerAdapter(getSupportFragmentManager()));
        tabLayout.setupWithViewPager(vPager);

        findViewById(R.id.transaction_navigation_add_btn)
                .setOnClickListener(view -> typeEntered());
//        findViewById(R.id.transaction_navigation_add_btn)
//                .setOnClickListener(view -> save());
        findViewById(R.id.transaction_navigation_delete_btn)
                .setOnClickListener(view -> delete());
        findViewById(R.id.setting_navigation_back_btn).setOnClickListener(
                view -> showUnsavedDialog());
    }

    // radioButton clicked option selected
    private int typeEntered() {
        vPager.getCurrentItem();
        return TransactionModel.TRANSACTION_TYPE_INCOME;
    }

    // radioButton option load
    private void typeLoad(int type) {
        switch (type) {
            case TransactionModel.TRANSACTION_TYPE_SPENT:
                vPager.setCurrentItem(0, true);
                break;
            case TransactionModel.TRANSACTION_TYPE_INCOME:
                vPager.setCurrentItem(1, true);
                break;
        }
    }

//    // save transaction logic
//    private void save() {
//        Log.d("DEBUG", "before save " + mTransaction.toString());
//        if (mTransaction.getAccountId() != TransactionModel.DEFAULT_ID) {
//            TransactionModel tmp = new TransactionModel(
//                    mAccID,
//                    String.valueOf(txtName.getText()),
//                    mDate,
//                    new Date(),
//                    typeEntered(),
//                    Float.valueOf(txtSum.getText().toString())
//            );
//
//            if (mTransId != DEFAULT_ID) {
//                // update logic
//                tmp.setId(mTransId);
//                AppExecutors.getsInstance().diskIO().execute(
//                        () -> mDb.transactionDao().updateTransaction(tmp));
//            } else {
//                // new transaction
//                AppExecutors.getsInstance().diskIO().execute(
//                        () -> mDb.transactionDao().insertTransaction(tmp));
//            }
//            // update balance for current (accId) account
//            AppExecutors.getsInstance().diskIO().execute(
//                    () -> LogicMath.accountBalanceUpdateById(getApplicationContext(), mAccID));
//
//            finish();
//        } else {
//            Toast.makeText(this, getResources().getString(R.string.add_check_no_account_warning), Toast.LENGTH_SHORT).show();
//        }
//    }

    // delete transaction logic
    private void delete() {
        if (mTransId != TransactionModel.DEFAULT_ID) {
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
                                        () -> LogicMath.accountBalanceUpdateById(getApplicationContext(), mAccID));
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
