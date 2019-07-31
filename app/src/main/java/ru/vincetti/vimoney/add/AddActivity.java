package ru.vincetti.vimoney.add;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import ru.vincetti.vimoney.R;

public class AddActivity extends AppCompatActivity {

    public static void start(Context context) {
        Intent starter = new Intent(context, AddActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        findViewById(R.id.setting_navigation_back_btn).setOnClickListener(view -> finish());
    }
}
