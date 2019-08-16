package ru.vincetti.vimoney.check;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.LiveData;

import ru.vincetti.vimoney.R;
import ru.vincetti.vimoney.data.models.AccountModel;
import ru.vincetti.vimoney.data.sqlite.AppDatabase;
import ru.vincetti.vimoney.history.HistoryFragment;

public class CheckActivity extends AppCompatActivity {
    private final static String EXTRA_CHECK_ID = "Extra_check_id";
    private final static int DEFAULT_CHECK_ID = -1;
    private final static int DEFAULT_CHECK_COUNT = 20;

    private int mCheckId = DEFAULT_CHECK_ID;
    private AppDatabase mDb;
    private TextView checkName, checkType, checkBalance;

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

            LiveData<AccountModel> data = mDb.accountDao().loadAccountById(mCheckId);
            data.observe(this, accountModel -> loadTransaction(accountModel));
            showTransactionsHistory(mCheckId);
        }
    }

    private void initViews() {
        mCheckId = DEFAULT_CHECK_ID;
        mDb = AppDatabase.getInstance(this);
        findViewById(R.id.setting_navigation_back_btn)
                .setOnClickListener(view -> finish());

        checkName = findViewById(R.id.check_acc_name);
        checkType = findViewById(R.id.check_acc_type);
        checkBalance = findViewById(R.id.check_acc_balance);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.check_nav_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.check_nav_edit_btn) {
            ChecksSettingsActivity.start(this);
        }
        return true;
    }

    private void showTransactionsHistory(int checkId) {
        HistoryFragment historyFragment = new HistoryFragment();

        Bundle args = new Bundle();
        args.putInt(HistoryFragment.BUNDLETAG_TRANS_COUNT_NAME, DEFAULT_CHECK_COUNT);
        args.putInt(HistoryFragment.BUNDLETAG_TRANS_CHECK_ID_NAME, checkId);
        historyFragment.setArguments(args);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.check_history_container, historyFragment)
                .commit();
    }

    private void loadTransaction(AccountModel accountModel) {
        checkName.setText(accountModel.getName());
        checkType.setText(accountModel.getType());
        checkBalance.setText(String.valueOf(accountModel.getSum()));
    }
}