package ru.vincetti.vimoney.home;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import ru.vincetti.vimoney.R;
import ru.vincetti.vimoney.add.AddActivity;
import ru.vincetti.vimoney.fragments.DashboardFragment;
import ru.vincetti.vimoney.fragments.HistoryFragment;
import ru.vincetti.vimoney.fragments.HomeFragment;

public class HomeActivity extends AppCompatActivity {
    private static String LOG_TAG = "MAIN DEBUG";
    private static String CHANNEL_ID = "15";

    private BottomAppBar btBar;
    private FloatingActionButton fab;

    private HomeFragment homeFragment;
    private HistoryFragment historyFragment;
    private DashboardFragment dashboardFragment;

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

        btBar = findViewById(R.id.bottom_app_bar);
        setSupportActionBar(btBar);

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> AddActivity.start(getApplicationContext()));

        homeFragment = new HomeFragment();
        historyFragment = new HistoryFragment();
        dashboardFragment = new DashboardFragment();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_container, homeFragment)
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
            case R.id.navigation_bar_home:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_container, homeFragment)
                        .commit();
                break;
            case R.id.navigation_bar_history:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_container, historyFragment)
                        .commit();
                break;
            case R.id.navigation_bar_dashboard:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_container, dashboardFragment)
                        .commit();
                break;
        }
        return true;
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
