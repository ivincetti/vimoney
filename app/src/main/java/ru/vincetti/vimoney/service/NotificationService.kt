package ru.vincetti.vimoney.service

import android.app.IntentService
import android.content.Intent
import ru.vincetti.vimoney.utils.showNotification
import ru.vincetti.vimoney.utils.showSaveErrorNotification
import ru.vincetti.vimoney.utils.showSaveNotification

class NotificationService : IntentService("NotificationService") {

    companion object {
        const val NOTIFICATION_ACTION = "Notification-service"
        const val NOTIFICATION_SAVE_ACTION = "Notification-save-action"
        const val NOTIFICATION_SAVE_ERROR_ACTION = "Notification-save-error-action"
    }

    override fun onHandleIntent(intent: Intent?) {
        intent?.let {
            when (it.action) {
                NOTIFICATION_ACTION -> showNotification(this)
                NOTIFICATION_SAVE_ACTION -> showSaveNotification(this)
                else -> showSaveErrorNotification(this)
            }
        }
    }
}
