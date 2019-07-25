package ru.vincetti.vimoney.utils;

import android.content.Context;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import ru.vincetti.vimoney.R;
import ru.vincetti.vimoney.add.AddActivity;
import ru.vincetti.vimoney.dashboard.DashboardActivity;
import ru.vincetti.vimoney.history.HistoryActivity;
import ru.vincetti.vimoney.home.HomeActivity;

public class bottomNavigationHelper {

    public static void initNavigation(final Context context, BottomNavigationView view) {
        view.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {

                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.navigation_bar_home:
                                HomeActivity.start(context);
                                return true;
                            case R.id.navigation_bar_dashboard:
                                DashboardActivity.start(context);
                                return true;
                            case R.id.navigation_bar_history:
                                HistoryActivity.start(context);
                                return true;
                            case R.id.navigation_bar_plus:
                                AddActivity.start(context);
                                return true;
                        }
                        return false;
                    }
                });
    }
}
