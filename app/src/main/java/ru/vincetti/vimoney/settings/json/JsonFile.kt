package ru.vincetti.vimoney.settings.json

import android.content.Context
import android.content.Intent
import android.text.TextUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.vincetti.vimoney.data.models.AccountModel
import ru.vincetti.vimoney.data.models.TransactionModel
import ru.vincetti.vimoney.data.sqlite.AppDatabase
import ru.vincetti.vimoney.service.FileService
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStreamReader
import java.util.*

object JsonFile {

    const val FILE_NAME_TRANSACTIONS = "transactions.json"
    const val FILE_NAME_ACCOUNTS = "accounts.json"

    fun save(context: Context) {
        context.startService(
                Intent(context, FileService::class.java).setAction(FileService.SAVE_ACTION)
        )
    }

    suspend fun load(context: Context) {
        val gson = Gson()
        val db = AppDatabase.getInstance(context)

        withContext(Dispatchers.IO) {
            var fisTransactions: FileInputStream? = null
            var fisAccounts: FileInputStream? = null
            val transactionsJsonBuilder = StringBuilder()
            val accountsJsonBuilder = StringBuilder()

            try {
                fisTransactions = context.openFileInput(FILE_NAME_TRANSACTIONS)
                var isr = InputStreamReader(fisTransactions)
                var br = BufferedReader(isr)
                var text: String?

                text = br.readLine()
                while (text != null) {
                    transactionsJsonBuilder.append(text).append("\n")
                    text = br.readLine()
                }

                fisAccounts = context.openFileInput(FILE_NAME_ACCOUNTS)
                isr = InputStreamReader(fisAccounts)
                br = BufferedReader(isr)

                text = br.readLine()
                while (text != null) {
                    accountsJsonBuilder.append(text).append("\n")
                    text = br.readLine()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                try {
                    fisTransactions?.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

                try {
                    fisAccounts?.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            if (!TextUtils.isEmpty(transactionsJsonBuilder.toString())) {
                db.transactionDao().deleteAllTransactions()
                val listType = object : TypeToken<ArrayList<TransactionModel>>() {}.type
                val transactions: List<TransactionModel> =
                        gson.fromJson(transactionsJsonBuilder.toString(), listType)
                for (transaction in transactions) {
                    db.transactionDao().insertTransaction(transaction)
                }
                db.accountDao().deleteAllAccounts()
                val listType1 = object : TypeToken<ArrayList<AccountModel>>() {}.type
                val transactions1: List<AccountModel> =
                        gson.fromJson(accountsJsonBuilder.toString(), listType1)
                for (account in transactions1) {
                    db.accountDao().insertAccount(account)
                }
            }
        }
    }
}
