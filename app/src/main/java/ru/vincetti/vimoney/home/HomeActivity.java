package ru.vincetti.vimoney.home;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ru.vincetti.vimoney.R;
import ru.vincetti.vimoney.check.CheckActivity;
import ru.vincetti.vimoney.dashboard.DashboardActivity;
import ru.vincetti.vimoney.data.adapters.CardsListRVAdapter;
import ru.vincetti.vimoney.data.models.AccountModel;
import ru.vincetti.vimoney.data.sqlite.AppDatabase;
import ru.vincetti.vimoney.history.HistoryActivity;
import ru.vincetti.vimoney.history.HistoryFragment;
import ru.vincetti.vimoney.notifications.NotificationsActivity;
import ru.vincetti.vimoney.settings.SettingsActivity;
import ru.vincetti.vimoney.transaction.TransactionActivity;

public class HomeActivity extends AppCompatActivity {
    private final static String CHANNEL_ID = "15";
    private final static int TR_MAIN_COUNT = 10;

    private AppDatabase mDb;
    CardsListRVAdapter mAdapter;

    private TextView mBalanceText;

    private HistoryFragment historyFragment;

    public static void start(Context context) {
        Intent intent = new Intent(context, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        createNotificationChannel();
        Toolbar toolbar = findViewById(R.id.top_toolbar);
        setSupportActionBar(toolbar);
        viewInit();

        mDb = AppDatabase.getInstance(this);
        // список карт/счетов
        mAdapter = new CardsListRVAdapter(itemId -> CheckActivity.start(getBaseContext(), itemId));
        RecyclerView cardsListView = findViewById(R.id.home_cards_recycle_view);
        //cardsListView.setHasFixedSize(true);
        LinearLayoutManager cardsLayoutManager = new LinearLayoutManager(this,
                RecyclerView.HORIZONTAL, false);
        cardsListView.setLayoutManager(cardsLayoutManager);
        cardsListView.setAdapter(mAdapter);
        accountsLoadFromDB();
        showTransactionsHistory();
    }

    public void viewInit() {
        findViewById(R.id.home_fab)
                .setOnClickListener(view -> TransactionActivity.start(this));
        findViewById(R.id.home_transactions_link)
                .setOnClickListener(view -> HistoryActivity.start(this));
        findViewById(R.id.home_user_stat_link)
                .setOnClickListener(view -> DashboardActivity.start(this));
    }

    private void showTransactionsHistory() {
        historyFragment = new HistoryFragment();

        Bundle args = new Bundle();
        args.putInt(HistoryFragment.BUNDLETAG_TRANS_COUNT_NAME, TR_MAIN_COUNT);
        historyFragment.setArguments(args);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_history_container, historyFragment)
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bottom_nav_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navigation_bar_notification:
                NotificationsActivity.start(this);
                break;
            case R.id.navigation_bar_settings:
                SettingsActivity.start(this);
                break;
        }
        return true;
    }

    private void accountsLoadFromDB() {
        HomeViewModel viewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        viewModel.getAccounts().observe(this, accounts -> {
            userBalanceChange(accounts);
            mAdapter.setList(accounts);
        });

    }

    private int userBalanceChange(List<AccountModel> accList) {
        mBalanceText = findViewById(R.id.home_user_balance);
        int bal = 0;
        for (AccountModel o : accList) {
            bal += o.getSum();
        }
        mBalanceText.setText(String.valueOf(bal));
        return bal;
    }

    // Notification channel register
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel =
                    new NotificationChannel(CHANNEL_ID,
                            getString(R.string.channel_name),
                            NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(getString(R.string.channel_description));
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
