package ru.vincetti.vimoney.add;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import ru.vincetti.vimoney.R;

public class AddCheckActivity extends AppCompatActivity {

    public static void start(Context context) {
        Intent starter = new Intent(context, AddCheckActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_check);

        findViewById(R.id.setting_navigation_back_btn).setOnClickListener(view -> finish());
    }
}
