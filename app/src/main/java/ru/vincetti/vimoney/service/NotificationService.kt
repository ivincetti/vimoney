package ru.vincetti.vimoney.service

import android.app.IntentService
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

class NotificationService : IntentService("NotificationService") {

    companion object {
        const val NOTIFICATION_ACTION = "Notification-service"
        const val NOTIFICATION_SAVE_ACTION = "Notification-save-action"
        const val NOTIFICATION_SAVE_ERROR_ACTION = "Notification-save-error-action"
        const val NOTIFICATION_CHANNEL_ID = "Notification_Chanel"
        const val NOTIFICATION_ID = 1231231

        private fun showNotification(context: Context) {
            showNotification(context,
                    context.getString(R.string.notification_sample_title_text),
                    context.getString(R.string.notification_sample_body_text)
            )
        }

        private fun showSaveNotification(context: Context) {
            showNotification(context,
                    context.getString(R.string.notification_save_title_text),
                    context.getString(R.string.notification_save_body_text)
            )
        }

        private fun showSaveErrorNotification(context: Context) {
            showNotification(context,
                    context.getString(R.string.notification_save_error_title_text),
                    context.getString(R.string.notification_save_body_text)
            )
        }

        private fun showNotification(context: Context, title: String, body: String) {
            val nManager = NotificationManagerCompat.from(context)

            // createNotificationChannel only for O= Android Versions
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val notificationChannel = NotificationChannel(NOTIFICATION_CHANNEL_ID,
                        "DEFAULT", NotificationManager.IMPORTANCE_DEFAULT)
                nManager.createNotificationChannel(notificationChannel)
            }

            // open MainActivity onClick
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
    }

    override fun onHandleIntent(intent: Intent?) {
        intent?.let {
            when (it.action) {
                NOTIFICATION_ACTION -> showNotification(this)
                NOTIFICATION_SAVE_ACTION -> showSaveNotification(this)
                NOTIFICATION_SAVE_ERROR_ACTION -> showSaveErrorNotification(this)
                else -> {

                }
            }
        }
    }
}
