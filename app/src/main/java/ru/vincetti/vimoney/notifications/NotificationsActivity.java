package ru.vincetti.vimoney.notifications;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import ru.vincetti.vimoney.R;
import ru.vincetti.vimoney.service.NotificationService;

public class NotificationsActivity extends AppCompatActivity {

    public static void start(Context context) {
        context.startActivity(new Intent(context, NotificationsActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        findViewById(R.id.setting_navigation_back_btn).setOnClickListener(view -> finish());
        findViewById(R.id.notification_notify_btn).setOnClickListener(
                view -> startService(new Intent(this, NotificationService.class)));
    }
}
