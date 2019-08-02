package ru.vincetti.vimoney.check;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import ru.vincetti.vimoney.R;

public class ChecksSettingsActivity extends AppCompatActivity {

    public static void start(Context context) {
        context.startActivity(new Intent(context, ChecksSettingsActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_settings);

        findViewById(R.id.setting_navigation_back_btn).setOnClickListener(view -> finish());
        findViewById(R.id.check_settings_fab)
                .setOnClickListener(view -> AddCheckActivity.start(this));
    }
}
