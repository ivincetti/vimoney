package ru.vincetti.vimoney.service;

import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import ru.vincetti.vimoney.R;
import ru.vincetti.vimoney.home.HomeActivity;

public class NotificationService extends IntentService {
    public final static String NOTIFICATION_ACTION = "Notification-service";
    public final static String NOTIFICATION_CHANNEL_ID = "Notification_Chanel";
    public final static int NOTIFICATION_ID = 1231231;

    public NotificationService() {
        super("NotificationService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (NOTIFICATION_ACTION.equals(intent.getAction())) {
            showNotification(this);
        }
    }


    private static void showNotification(Context context) {
        NotificationManager nManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // createNotificationChannel only for O= Android Versions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                    "DEFAULT", NotificationManager.IMPORTANCE_DEFAULT);
            nManager.createNotificationChannel(notificationChannel);
        }

        // open HomeActivity onClick
        Intent intent = new Intent(context, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,
                0, intent, 0);

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_notifications_dark)
                        .setContentTitle(context.getString(R.string.notification_sample_title_text))
                        .setContentText(context.getString(R.string.notification_sample_body_text))
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent);

        nManager.notify(NOTIFICATION_ID, builder.build());
    }
}
