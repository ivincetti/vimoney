package ru.vincetti.vimoney.settings.json

import android.content.Context
import android.text.TextUtils
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.vincetti.vimoney.data.models.AccountModel
import ru.vincetti.vimoney.data.models.TransactionModel
import ru.vincetti.vimoney.data.sqlite.AppDatabase
import java.io.*
import java.util.*

object JsonFile {

    private const val FILE_NAME_TRANSACTIONS = "transactions.json"
    private const val FILE_NAME_ACCOUNTS = "accounts.json"

    suspend fun save(context: Context) {
        withContext(Dispatchers.IO) {
            val gson = GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create()
            val mDb = AppDatabase.getInstance(context)
            val accounts = mDb.accountDao().loadAllAccounts()
            accounts?.let {
                val accountJson: String = gson.toJson(it)
                val file = File(context.getExternalFilesDir(null), FILE_NAME_ACCOUNTS)
                val fosAccount = FileOutputStream(file)
                fosAccount.write(accountJson.toByteArray())
                fosAccount.close()
            }
            val transactions = mDb.transactionDao().loadAllTransactions()
            transactions?.let {
                val transactionsJson = gson.toJson(it)
                val file = File(context.getExternalFilesDir(null), FILE_NAME_TRANSACTIONS)
                val fos = FileOutputStream(file)
                fos.write(transactionsJson.toByteArray())
                fos.close()
            }
        }
    }

    suspend fun load(context: Context) {
        val gson = GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create()
        val db = AppDatabase.getInstance(context)

        withContext(Dispatchers.IO) {
            var fisTransactions: FileInputStream? = null
            var fisAccounts: FileInputStream? = null
            val transactionsJsonBuilder = StringBuilder()
            val accountsJsonBuilder = StringBuilder()

            try {
                val file = File(context.getExternalFilesDir(null), FILE_NAME_TRANSACTIONS)
                fisTransactions = FileInputStream(file)
                var isr = InputStreamReader(fisTransactions)
                var br = BufferedReader(isr)
                var text: String?

                text = br.readLine()
                while (text != null) {
                    transactionsJsonBuilder.append(text).append("\n")
                    text = br.readLine()
                }

                val fileAcc = File(context.getExternalFilesDir(null), FILE_NAME_ACCOUNTS)
                fisAccounts = FileInputStream(fileAcc)
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
