package ru.vincetti.vimoney.check;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import ru.vincetti.vimoney.R;

public class CheckActivity extends AppCompatActivity {
    private Toolbar toolbar;

    public static void start(Context context){
        context.startActivity(new Intent(context, CheckActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check);

        toolbar = findViewById(R.id.top_toolbar);
        setSupportActionBar(toolbar);

        findViewById(R.id.setting_navigation_back_btn)
                .setOnClickListener(view -> finish());
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
