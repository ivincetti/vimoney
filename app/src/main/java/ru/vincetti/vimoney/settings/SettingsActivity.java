package ru.vincetti.vimoney.settings;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;

import ru.vincetti.vimoney.R;
import ru.vincetti.vimoney.settings.json.jsonFile;

public class SettingsActivity extends AppCompatActivity {

    public static void start(Context context) {
        context.startActivity(new Intent(context, SettingsActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        LifecycleOwner lf = this;

        findViewById(R.id.setting_navigation_back_btn).setOnClickListener(view -> finish());
        findViewById(R.id.btn_check_settings).setOnClickListener(v -> jsonFile.export(lf));
    }
}
