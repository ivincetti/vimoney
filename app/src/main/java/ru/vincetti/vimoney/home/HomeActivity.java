package ru.vincetti.vimoney.home;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ru.vincetti.vimoney.R;
import ru.vincetti.vimoney.check.CheckActivity;
import ru.vincetti.vimoney.dashboard.DashboardActivity;
import ru.vincetti.vimoney.data.adapters.CardsListViewAdapter;
import ru.vincetti.vimoney.data.sqlite.DbHelper;
import ru.vincetti.vimoney.data.sqlite.VimonContract;
import ru.vincetti.vimoney.history.HistoryActivity;
import ru.vincetti.vimoney.history.HistoryFragment;
import ru.vincetti.vimoney.models.Account;
import ru.vincetti.vimoney.notifications.NotificationsActivity;
import ru.vincetti.vimoney.settings.SettingsActivity;
import ru.vincetti.vimoney.transaction.TransactionActivity;

public class HomeActivity extends AppCompatActivity {
    private static String LOG_TAG = "MAIN DEBUG";
    private static String BUNDLETAG = "ru.vincetti.vimoney.transhistory";
    private static String CHANNEL_ID = "15";
    private static int TR_MAIN_COUNT = 10;

    private SQLiteDatabase db;
    private ArrayList<Account> accList;

    private Toolbar toolbar;
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

        toolbar = findViewById(R.id.top_toolbar);
        setSupportActionBar(toolbar);

        viewInit();

        accList = new ArrayList<>();

        db = new DbHelper(this).getReadableDatabase();
        accountsLoadFromDB();
        userBalanceChange();

        showTransactionsHistory();

        // список карт/счетов
        CardsListViewAdapter adapter = new CardsListViewAdapter(accList, position -> {
            Log.d("DEBUG", "Cards click");
            CheckActivity.start(getBaseContext());
        });
        RecyclerView cardsListView = findViewById(R.id.home_cards_recycle_view);
        cardsListView.setHasFixedSize(true);
        LinearLayoutManager cardsLayoutManager = new LinearLayoutManager(this,
                RecyclerView.HORIZONTAL, false);
        cardsListView.setLayoutManager(cardsLayoutManager);
        cardsListView.setAdapter(adapter);
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
        args.putInt(BUNDLETAG, TR_MAIN_COUNT);
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
        try (Cursor accCurs = db.query(VimonContract.AccountsEntry.TABLE_NAME,
                null, null, null,
                null, null, null)) {
            if (accCurs.getCount() > 0) {
                while (accCurs.moveToNext()) {
                    accList.add(new Account(
                            accCurs.getString(
                                    accCurs.getColumnIndex(VimonContract.AccountsEntry.COLUMN_TITLE)
                            ),
                            accCurs.getString(
                                    accCurs.getColumnIndex(VimonContract.AccountsEntry.COLUMN_TYPE)
                            ),
                            accCurs.getInt(
                                    accCurs.getColumnIndex(VimonContract.AccountsEntry.COLUMN_BALANCE)
                            )
                    ));
                }
            }
        }
    }

    private int userBalanceChange() {
        mBalanceText = findViewById(R.id.home_user_balance);
        int bal = 0;
        for (Account o : accList) {
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
