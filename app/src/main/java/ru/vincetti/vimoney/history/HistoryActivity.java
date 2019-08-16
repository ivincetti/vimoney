package ru.vincetti.vimoney.history;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import ru.vincetti.vimoney.R;

public class HistoryActivity extends AppCompatActivity {
    private static final int TRANSACTIONS_COUNT = 25;

    public static void start(Context context) {
        context.startActivity(new Intent(context, HistoryActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        viewInit();

        showTransactionsHistory();
    }

    private void viewInit() {
        findViewById(R.id.setting_navigation_back_btn).setOnClickListener(view -> finish());
    }

    // Show historyFragment
    private void showTransactionsHistory(){
        HistoryFragment historyFragment = new HistoryFragment();

        Bundle args = new Bundle();
        args.putInt(HistoryFragment.BUNDLE_TRANS_COUNT_NAME, TRANSACTIONS_COUNT);
        historyFragment.setArguments(args);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.history_main_container, historyFragment)
                .commit();
    }
}
