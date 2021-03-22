package ru.vincetti.vimoney.workers

import android.content.Context
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkRequest
import androidx.work.Worker
import androidx.work.WorkerParameters
import ru.vincetti.vimoney.utils.NotificationsUtils

class NotificationWorker(appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {

    companion object {

        fun createWorkRequest(): WorkRequest = OneTimeWorkRequestBuilder<NotificationWorker>().build()
    }

    override fun doWork(): Result {
        NotificationsUtils.showNotification(applicationContext)

        return Result.success()
    }
}

class NotificationSaveWorker(appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {

    companion object {

        fun createWorkRequest(): WorkRequest = OneTimeWorkRequestBuilder<NotificationSaveWorker>().build()
    }

    override fun doWork(): Result {
        NotificationsUtils.showSaveNotification(applicationContext)

        return Result.success()
    }
}

class NotificationErrorWorker(appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {

    companion object {

        fun createWorkRequest(): WorkRequest = OneTimeWorkRequestBuilder<NotificationErrorWorker>().build()
    }

    override fun doWork(): Result {
        NotificationsUtils.showSaveErrorNotification(applicationContext)

        return Result.success()
    }
}
