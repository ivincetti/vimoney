package ru.vincetti.vimoney.settings;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import ru.vincetti.vimoney.R;

public class SettingsActivity extends AppCompatActivity {

    public static void start(Context context){
        context.startActivity(new Intent(context, SettingsActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        findViewById(R.id.setting_navigation_back_btn).setOnClickListener(view -> finish());

        findViewById(R.id.btn_check_settings)
                .setOnClickListener(view -> SettingsCheckActivity.start(this));
    }
}