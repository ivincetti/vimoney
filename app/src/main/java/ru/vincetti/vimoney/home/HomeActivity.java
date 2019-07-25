package ru.vincetti.vimoney.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import ru.vincetti.vimoney.R;
import ru.vincetti.vimoney.add.AddActivity;
import ru.vincetti.vimoney.utils.bottomNavigationHelper;

public class HomeActivity extends AppCompatActivity {
    private TextView mTextMessage;

    public static void start(Context context) {
        Intent intent = new Intent(context, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mTextMessage = findViewById(R.id.message);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        bottomNavigationHelper.initNavigation(this, navView);
        navView.getMenu().findItem(R.id.navigation_bar_home).setChecked(true);
    }

}
