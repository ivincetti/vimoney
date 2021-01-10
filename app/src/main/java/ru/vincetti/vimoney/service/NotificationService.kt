package ru.vincetti.vimoney.service

import android.app.IntentService
import android.content.Intent
import ru.vincetti.vimoney.utils.NotificationsUtils

class NotificationService : IntentService("NotificationService") {

    companion object {
        const val NOTIFICATION_ACTION = "Notification-service"
        const val NOTIFICATION_SAVE_ACTION = "Notification-save-action"
        const val NOTIFICATION_SAVE_ERROR_ACTION = "Notification-save-error-action"
    }

    override fun onHandleIntent(intent: Intent?) {
        intent?.let {
            when (it.action) {
                NOTIFICATION_ACTION -> NotificationsUtils.showNotification(this)
                NOTIFICATION_SAVE_ACTION -> NotificationsUtils.showSaveNotification(this)
                else -> NotificationsUtils.showSaveErrorNotification(this)
            }
        }
    }
}
