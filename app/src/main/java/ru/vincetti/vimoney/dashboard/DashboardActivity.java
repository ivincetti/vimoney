package ru.vincetti.vimoney.dashboard;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import ru.vincetti.vimoney.R;
import ru.vincetti.vimoney.add.AddActivity;
import ru.vincetti.vimoney.history.HistoryActivity;
import ru.vincetti.vimoney.home.HomeActivity;

public class DashboardActivity extends AppCompatActivity {
    private TextView mTextMessage;
    private BottomAppBar btBar;
    private FloatingActionButton fab;

    public static void start(Context context) {
        Intent intent = new Intent(context, DashboardActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        btBar = findViewById(R.id.bottom_app_bar);
        setSupportActionBar(btBar);

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> AddActivity.start(getApplicationContext()));

        mTextMessage = findViewById(R.id.message);
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
                HomeActivity.start(this);
                break;
            case R.id.navigation_bar_history:
                HistoryActivity.start(this);
                break;
            case R.id.navigation_bar_dashboard:
                break;
        }
        return true;
    }

}
