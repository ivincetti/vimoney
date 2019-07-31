package ru.vincetti.vimoney.history;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import ru.vincetti.vimoney.R;
import ru.vincetti.vimoney.fragments.HistoryFragment;

public class HistoryActivity extends AppCompatActivity {
    HistoryFragment historyFragment;

    public static void start(Context context) {
        context.startActivity(new Intent(context, HistoryActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        viewInit();

        historyFragment = new HistoryFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.history_main_container, historyFragment)
                .commit();
    }

    private void viewInit(){
        findViewById(R.id.setting_navigation_back_btn).setOnClickListener(view -> finish());
    }
}
