package ru.vincetti.vimoney.settings.json

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.text.TextUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.vincetti.vimoney.data.models.AccountModel
import ru.vincetti.vimoney.data.models.TransactionModel
import ru.vincetti.vimoney.data.sqlite.AppDatabase
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStreamReader
import java.util.*

object JsonFile {

    private const val FILE_NAME_TRANSACTIONS = "transactions.json"
    private const val FILE_NAME_ACCOUNTS = "accounts.json"

    suspend fun save(context: Context) {
        withContext(Dispatchers.IO) {
            val gson = Gson()
            val mDb = AppDatabase.getInstance(context)
            val accounts = mDb.accountDao().loadAllAccounts()
            accounts?.let {
                val accountJson: String = gson.toJson(it)
                val fosAccount = context.openFileOutput(FILE_NAME_ACCOUNTS, MODE_PRIVATE)
                fosAccount?.write(accountJson.toByteArray())
            }
            val transactions = mDb.transactionDao().loadAllTransactions()
            transactions?.let {
                val transactionsJson = gson.toJson(it)
                val fos = context.openFileOutput(FILE_NAME_TRANSACTIONS, MODE_PRIVATE)
                fos?.write(transactionsJson.toByteArray())
            }
        }
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
