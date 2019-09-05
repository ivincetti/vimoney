package ru.vincetti.vimoney.transaction;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

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
    private TextView titleTxt;
    private int mAccID = TransactionModel.DEFAULT_ID;
    private int mTransId = TransactionModel.DEFAULT_ID;
    private int accTransferTo = TransactionModel.DEFAULT_ID;
    private int accNestedId = TransactionModel.DEFAULT_ID;
    Bundle fragmentBundle;

    ViewPager vPager;
    TransactionModel mTransaction;

    public static void start(Context context) {
        Intent starter = new Intent(context, TransactionActivity.class);
        context.startActivity(starter);
    }

    public static void startId(Context context, int id) {
        Intent starter = new Intent(context, TransactionActivity.class);
        starter.putExtra(EXTRA_TRANS_ID, id);
        context.startActivity(starter);
    }

    public static void startCheckId(Context context, int id) {
        Intent starter = new Intent(context, TransactionActivity.class);
        starter.putExtra(EXTRA_ACCOUNT_ID, id);
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
        fragmentBundle = new Bundle();

        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra(EXTRA_TRANS_ID)) {
                mTransId = intent.getIntExtra(EXTRA_TRANS_ID, TransactionModel.DEFAULT_ID);
                fragmentBundle.putInt(EXTRA_TRANS_ID, mTransId);
                findViewById(R.id.transaction_navigation_delete_btn).setVisibility(View.VISIBLE);
                LiveData<TransactionModel> transLD = mDb.transactionDao().loadTransactionById(mTransId);
                transLD.observe(this, new Observer<TransactionModel>() {
                    @Override
                    public void onChanged(TransactionModel transactionModel) {
                        transLD.removeObserver(this);
                        typeLoad(transactionModel.getType());
                        mAccID = transactionModel.getAccountId();
                        mTransaction.copyFrom(transactionModel);
                        if (transactionModel.getExtraValue().equals(TransactionModel.TRANSACTION_TYPE_TRANSFER_KEY)) {
                            accNestedId = Integer.valueOf(transactionModel.getExtraValue());
                            accTransferTo = Integer.valueOf(transactionModel.getExtraValue());
                        }
                    }
                });
            } else if (intent.hasExtra(EXTRA_ACCOUNT_ID)) {
                int mAccID = intent.getIntExtra(EXTRA_ACCOUNT_ID, TransactionModel.DEFAULT_ID);
                mTransaction.setAccountId(mAccID);
            }
        }
        TabLayout tabLayout = findViewById(R.id.sliding_tabs);
        vPager.setAdapter(new TabsFragmentPagerAdapter(getSupportFragmentManager(), fragmentBundle));
        vPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //do nothing
            }

            @Override
            public void onPageSelected(int position) {
                setActivityTitle(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                //do nothing
            }
        });
        tabLayout.setupWithViewPager(vPager);

        viewModel.setTransaction(mTransaction);
    }

    @Override
    protected void onPause() {
        super.onPause();

        InputMethodManager imm = (InputMethodManager) this.getSystemService(
                INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
    }

    private void initViews() {
        mDb = AppDatabase.getInstance(this);
        titleTxt = findViewById(R.id.transaction_navigation_txt);
        vPager = findViewById(R.id.view_pager);

        findViewById(R.id.transaction_navigation_delete_btn)
                .setOnClickListener(view -> delete());
        findViewById(R.id.setting_navigation_back_btn).setOnClickListener(
                view -> showUnsavedDialog());
    }

    void setActivityTitle(int position) {
        switch (position) {
            case TransactionModel.TRANSACTION_TYPE_SPENT_TAB:
                titleTxt.setText(getResources().getString(R.string.add_title_home_spent));
                break;
            case TransactionModel.TRANSACTION_TYPE_INCOME_TAB:
                titleTxt.setText(getResources().getString(R.string.add_title_home_income));
                break;
            case TransactionModel.TRANSACTION_TYPE_TRANSFER:
                titleTxt.setText(getResources().getString(R.string.add_title_home_transfer));
                break;
//            case TransactionModel.TRANSACTION_TYPE_DEBT:
//                titleTxt.setText(getResources().getString(R.string.add_title_home_debt));
//                break;
        }
    }

    // radioButton option load
    private void typeLoad(int type) {
        switch (type) {
            case TransactionModel.TRANSACTION_TYPE_SPENT:
                vPager.setCurrentItem(TransactionModel.TRANSACTION_TYPE_SPENT_TAB, true);
                setActivityTitle(TransactionModel.TRANSACTION_TYPE_SPENT_TAB);
                break;
            case TransactionModel.TRANSACTION_TYPE_INCOME:
                vPager.setCurrentItem(TransactionModel.TRANSACTION_TYPE_INCOME_TAB, true);
                setActivityTitle(TransactionModel.TRANSACTION_TYPE_INCOME_TAB);
                break;
            case TransactionModel.TRANSACTION_TYPE_TRANSFER:
                vPager.setCurrentItem(TransactionModel.TRANSACTION_TYPE_TRANSFER_TAB, true);
                setActivityTitle(TransactionModel.TRANSACTION_TYPE_TRANSFER_TAB);
                break;
//            case TransactionModel.TRANSACTION_TYPE_DEBT:
//                vPager.setCurrentItem(TransactionModel.TRANSACTION_TYPE_DEBT_TAB, true);
//                setActivityTitle(TransactionModel.TRANSACTION_TYPE_DEBT_TAB);
//                break;
        }
    }

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
                                        () -> {
                                            if (mTransaction.getExtraKey().equals(TransactionModel.TRANSACTION_TYPE_TRANSFER_KEY)
                                                    && accNestedId > 0) {
                                                // delete nested
                                                mDb.transactionDao().deleteTransactionById(accNestedId);
                                                // TODO accTransferTo must be write (now id of transaction)
                                                // update balance for nested transfer account
                                                // LogicMath.accountBalanceUpdateById(getApplicationContext(), accTransferTo);
                                            }
                                            mDb.transactionDao().deleteTransactionById(mTransId);
                                            // update balance for current (accId) account
                                            LogicMath.accountBalanceUpdateById(mDb, mAccID);
                                        });
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
