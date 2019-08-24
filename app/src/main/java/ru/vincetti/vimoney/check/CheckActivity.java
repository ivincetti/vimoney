package ru.vincetti.vimoney.check;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.LiveData;

import ru.vincetti.vimoney.R;
import ru.vincetti.vimoney.data.models.AccountListModel;
import ru.vincetti.vimoney.data.sqlite.AppDatabase;
import ru.vincetti.vimoney.history.HistoryFragment;
import ru.vincetti.vimoney.transaction.TransactionActivity;

public class CheckActivity extends AppCompatActivity {
    private final static String EXTRA_CHECK_ID = "Extra_check_id";
    private final static int DEFAULT_CHECK_ID = -1;
    private final static int DEFAULT_CHECK_COUNT = 20;

    private int mCheckId = DEFAULT_CHECK_ID;
    private AppDatabase mDb;
    private TextView checkName, checkType, checkBalance, isArchive, checkSymbol;

    public static void start(Context context, int id) {
        Intent intent = new Intent(context, CheckActivity.class);
        intent.putExtra(EXTRA_CHECK_ID, id);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check);
        Toolbar toolbar = findViewById(R.id.top_toolbar);
        setSupportActionBar(toolbar);
        initViews();

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(EXTRA_CHECK_ID)) {
            mCheckId = intent.getIntExtra(EXTRA_CHECK_ID, DEFAULT_CHECK_ID);

            LiveData<AccountListModel> data = mDb.accountDao().loadAccountByIdFull(mCheckId);
            data.observe(this, accountModel -> loadAccount(accountModel));
            showTransactionsHistory(mCheckId);
        }
    }

    private void initViews() {
        mDb = AppDatabase.getInstance(this);
        findViewById(R.id.setting_navigation_back_btn)
                .setOnClickListener(view -> finish());

        checkName = findViewById(R.id.check_acc_name);
        checkType = findViewById(R.id.check_acc_type);
        checkBalance = findViewById(R.id.check_acc_balance);
        checkSymbol = findViewById(R.id.check_acc_symbol);
        isArchive = findViewById(R.id.check_acc_archive);

        findViewById(R.id.check_fab).setOnClickListener(view -> {
            Intent starter = new Intent(getBaseContext(), TransactionActivity.class);
            starter.putExtra(TransactionActivity.EXTRA_ACCOUNT_ID, mCheckId);
            startActivity(starter);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.check_nav_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.check_nav_edit_btn) {
            AddCheckActivity.start(this, mCheckId);
        }
        return true;
    }

    // show transaction for this account
    private void showTransactionsHistory(int checkId) {
        HistoryFragment historyFragment = new HistoryFragment();

        Bundle args = new Bundle();
        args.putInt(HistoryFragment.BUNDLE_TRANS_COUNT_NAME, DEFAULT_CHECK_COUNT);
        args.putInt(HistoryFragment.BUNDLE_TRANS_CHECK_ID_NAME, checkId);
        historyFragment.setArguments(args);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.check_history_container, historyFragment)
                .commit();
    }

    // account data to UI
    private void loadAccount(AccountListModel accountModel) {
        checkName.setText(accountModel.getName());
        checkType.setText(accountModel.getType());
        checkBalance.setText(String.valueOf(accountModel.getSum()));
        if (accountModel.isArhive()) {
            isArchive.setVisibility(View.VISIBLE);
            isArchive.setText(R.string.check_arсhive_txt);
        } else {
            isArchive.setVisibility(View.INVISIBLE);
        }
        checkSymbol.setText(accountModel.getSymbol());
    }
}
