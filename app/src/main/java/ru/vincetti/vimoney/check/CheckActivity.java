package ru.vincetti.vimoney.check;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import ru.vincetti.vimoney.R;

public class CheckActivity extends AppCompatActivity {

    public static void start(Context context){
        context.startActivity(new Intent(context, CheckActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_settings);

        findViewById(R.id.setting_navigation_back_btn)
                .setOnClickListener(view -> finish());
        findViewById(R.id.check_nav_edit_btn)
                .setOnClickListener(view -> AddCheckActivity.start(this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.check_nav_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.check_nav_edit_btn){
            ChecksSettingsActivity.start(this);
        }
        return true;
    }
}
