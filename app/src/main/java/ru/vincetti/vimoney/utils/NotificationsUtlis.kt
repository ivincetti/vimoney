package ru.vincetti.vimoney.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import ru.vincetti.vimoney.MainActivity
import ru.vincetti.vimoney.R

const val NOTIFICATION_CHANNEL_ID = "15"
const val NOTIFICATION_CHANNEL_NAME = "Notification_Chanel"
const val NOTIFICATION_ID = 1_231_231

/** Notification channel register. */
fun createNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_DEFAULT
        )
        channel.description = context.getString(R.string.channel_description)
        /** Register the channel with the system. */
        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.createNotificationChannel(channel)
    }
}

/** Show notification. */
fun showNotification(
    context: Context,
    title: String,
    body: String
) {
    val nManager = NotificationManagerCompat.from(context)

    val intent = Intent(context, MainActivity::class.java)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK and Intent.FLAG_ACTIVITY_CLEAR_TASK
    val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)

    val builder = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_notifications_dark)
        .setContentTitle(title)
        .setContentText(body)
        .setAutoCancel(true)
        .setContentIntent(pendingIntent)

    nManager.notify(NOTIFICATION_ID, builder.build())
}

fun showNotification(context: Context) {
    showNotification(
        context,
        context.getString(R.string.notification_sample_title_text),
        context.getString(R.string.notification_sample_body_text)
    )
}

fun showSaveNotification(context: Context) {
    showNotification(
        context,
        context.getString(R.string.notification_save_title_text),
        context.getString(R.string.notification_save_body_text)
    )
}

fun showSaveErrorNotification(context: Context) {
    showNotification(
        context,
        context.getString(R.string.notification_save_error_title_text),
        context.getString(R.string.notification_save_body_text)
    )
}
