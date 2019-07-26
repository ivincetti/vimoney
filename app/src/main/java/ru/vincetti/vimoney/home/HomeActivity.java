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
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import ru.vincetti.vimoney.R;
import ru.vincetti.vimoney.add.AddActivity;
import ru.vincetti.vimoney.dashboard.DashboardActivity;
import ru.vincetti.vimoney.data.sqlite.DbHelper;
import ru.vincetti.vimoney.data.sqlite.VimonContract;
import ru.vincetti.vimoney.history.HistoryActivity;

public class HomeActivity extends AppCompatActivity {
    private static String LOG_TAG = "MAIN DEBUG";
    private static String CHANNEL_ID = "15";
    private SQLiteDatabase db;

    private BottomAppBar btBar;
    private FloatingActionButton fab;

    private TextView mUserText;
    private TextView mBalanceText;
    private int mAllBalance;

    private TextView mAcc1Type;
    private TextView mAcc1Name;
    private TextView mAcc1Balance;

    private TextView mAcc2Type;
    private TextView mAcc2Name;
    private TextView mAcc2Balance;

    private TextView mAcc3Type;
    private TextView mAcc3Name;
    private TextView mAcc3Balance;


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
        mAllBalance = 0;

        db = new DbHelper(this).getReadableDatabase();

        viewInit();
        userBalanceChange(mAllBalance);
        userLoadfromDB();

        mAllBalance = account1LoadfromDB() + account2LoadfromDB() + account3LoadfromDB();
        userBalanceChange(mAllBalance);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bottom_nav_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navigation_bar_home:
                break;
            case R.id.navigation_bar_history:
                HistoryActivity.start(this);
                break;
            case R.id.navigation_bar_dashboard:
                DashboardActivity.start(this);
                break;
        }
        return true;
    }

    // activity view initialization
    private void viewInit() {
        mUserText = findViewById(R.id.home_user_name);
        mBalanceText = findViewById(R.id.home_user_balance);

        mAcc1Type = findViewById(R.id.home_acc1_type);
        mAcc1Name = findViewById(R.id.home_acc1_name);
        mAcc1Balance = findViewById(R.id.home_acc1_balance);

        mAcc2Type = findViewById(R.id.home_acc2_type);
        mAcc2Name = findViewById(R.id.home_acc2_name);
        mAcc2Balance = findViewById(R.id.home_acc2_balance);

        mAcc3Type = findViewById(R.id.home_acc3_type);
        mAcc3Name = findViewById(R.id.home_acc3_name);
        mAcc3Balance = findViewById(R.id.home_acc3_balance);

        btBar = findViewById(R.id.bottom_app_bar);
        setSupportActionBar(btBar);

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> AddActivity.start(getApplicationContext()));
    }

    private void userLoad(String name) {
        mUserText.setText(name);
    }

    private void userBalanceChange(int newBalance) {
        mBalanceText.setText(String.valueOf(newBalance));
    }

    private void userLoadfromDB() {
        Cursor userCurs = db.query(VimonContract.UserEntry.TABLE_NAME,
                null, null, null,
                null, null, null);
        try {
            if (userCurs.getCount() > 0) {
                Log.d(LOG_TAG, "user get from DB");
                userCurs.moveToFirst();
                Log.d(LOG_TAG, "user movetoFirst from DB");
                userLoad(userCurs.getString(
                        userCurs.getColumnIndex(VimonContract.UserEntry.COLUMN_NAME)
                ));
            }
        } finally {
            userCurs.close();
        }
    }

    private void acc1Load(String name, String type, int balance) {
        mAcc1Name.setText(name);
        mAcc1Type.setText(type);
        mAcc1Balance.setText(String.valueOf(balance));
    }

    private void acc2Load(String name, String type, int balance) {
        mAcc2Name.setText(name);
        mAcc2Type.setText(type);
        mAcc2Balance.setText(String.valueOf(balance));
    }

    private void acc3Load(String name, String type, int balance) {
        mAcc3Name.setText(name);
        mAcc3Type.setText(type);
        mAcc3Balance.setText(String.valueOf(balance));
    }

    private int account1LoadfromDB() {
        int num = 1;
        int balance = 0;
        Cursor accCurs = db.query(VimonContract.AccountsEntry.TABLE_NAME,
                null, null, null,
                null, null, null);
        try {
            if (accCurs.getCount() > 0) {
                Log.d(LOG_TAG, "Accounts1 get from DB");
                accCurs.move(num);
                Log.d(LOG_TAG, "Accounts1 " + num + " from DB");
                balance = accCurs.getInt(
                        accCurs.getColumnIndex(VimonContract.AccountsEntry.COLUMN_BALANCE)
                );
                Log.d(LOG_TAG, "Accounts1 balanca " + balance);
                acc1Load(accCurs.getString(
                        accCurs.getColumnIndex(VimonContract.AccountsEntry.COLUMN_TITLE)
                        ),
                        accCurs.getString(
                                accCurs.getColumnIndex(VimonContract.AccountsEntry.COLUMN_TYPE)
                        ),
                        balance
                );
            }
        } finally {
            accCurs.close();
        }
        return balance;
    }

    private int account2LoadfromDB() {
        int num = 2;
        int balance = 0;
        Cursor accCurs = db.query(VimonContract.AccountsEntry.TABLE_NAME,
                null, null, null,
                null, null, null);
        try {
            if (accCurs.getCount() > 0) {
                Log.d(LOG_TAG, "Accounts1 get from DB");
                accCurs.move(num);
                Log.d(LOG_TAG, "Accounts1 " + num + " from DB");
                balance = accCurs.getInt(
                        accCurs.getColumnIndex(VimonContract.AccountsEntry.COLUMN_BALANCE)
                );
                Log.d(LOG_TAG, "Accounts1 balanca " + balance);
                acc2Load(accCurs.getString(
                        accCurs.getColumnIndex(VimonContract.AccountsEntry.COLUMN_TITLE)
                        ),
                        accCurs.getString(
                                accCurs.getColumnIndex(VimonContract.AccountsEntry.COLUMN_TYPE)
                        ),
                        balance
                );
            }
        } finally {
            accCurs.close();
        }
        return balance;
    }

    private int account3LoadfromDB() {
        int num = 3;
        int balance = 0;
        Cursor accCurs = db.query(VimonContract.AccountsEntry.TABLE_NAME,
                null, null, null,
                null, null, null);
        try {
            if (accCurs.getCount() > 0) {
                Log.d(LOG_TAG, "Accounts1 get from DB");
                accCurs.move(num);
                Log.d(LOG_TAG, "Accounts1 " + num + " from DB");
                balance = accCurs.getInt(
                        accCurs.getColumnIndex(VimonContract.AccountsEntry.COLUMN_BALANCE)
                );
                Log.d(LOG_TAG, "Accounts1 balanca " + balance);
                acc3Load(accCurs.getString(
                        accCurs.getColumnIndex(VimonContract.AccountsEntry.COLUMN_TITLE)
                        ),
                        accCurs.getString(
                                accCurs.getColumnIndex(VimonContract.AccountsEntry.COLUMN_TYPE)
                        ),
                        balance
                );
            }
        } finally {
            accCurs.close();
        }
        return balance;
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
