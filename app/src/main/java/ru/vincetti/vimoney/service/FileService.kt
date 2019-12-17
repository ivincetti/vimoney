package ru.vincetti.vimoney.service

import android.app.IntentService
import android.content.Intent
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import ru.vincetti.vimoney.App
import ru.vincetti.vimoney.data.sqlite.AppDatabase
import ru.vincetti.vimoney.settings.json.JsonFile

class FileService : IntentService("FileService") {

    // использование своего job
    private val myJob = Job()
    private val myScope = CoroutineScope(Dispatchers.IO + myJob)

    companion object {
        const val SAVE_ACTION = "save-file-service"
    }

    override fun onHandleIntent(intent: Intent?) {
        if (SAVE_ACTION == intent?.action) export()
    }

    private fun export() {
        val gson = Gson()
        val context = App.getAppContext()

        val mDb = AppDatabase.getInstance(context!!)
        myScope.launch {
            val accounts = mDb.accountDao().loadAllAccounts()
            accounts?.let {
                val accountJson: String = gson.toJson(it)
                val fosAccount = App.context?.openFileOutput(JsonFile.FILE_NAME_ACCOUNTS, MODE_PRIVATE)
                fosAccount?.write(accountJson.toByteArray())
            }
            val transactions = mDb.transactionDao().loadAllTransactions()
            transactions?.let {
                val transactionsJson = gson.toJson(it)
                val fos = App.context?.openFileOutput(JsonFile.FILE_NAME_TRANSACTIONS, MODE_PRIVATE)
                fos?.write(transactionsJson.toByteArray())
            }
            App.getAppContext()?.startService(
                    Intent(App.context, NotificationService::class.java)
                            .setAction(NotificationService.NOTIFICATION_SAVE_ACTION))
        }
    }
}
