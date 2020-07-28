package ru.vincetti.vimoney.settings.json

import android.content.Context
import android.text.TextUtils
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.vincetti.vimoney.data.models.AccountModel
import ru.vincetti.vimoney.data.models.CategoryModel
import ru.vincetti.vimoney.data.models.TransactionModel
import ru.vincetti.vimoney.data.sqlite.AppDatabase
import ru.vincetti.vimoney.utils.accountBalanceUpdateAll
import java.io.*
import java.util.*

object JsonFile {

    private const val FILE_NAME_TRANSACTIONS = "transactions.json"
    private const val FILE_NAME_ACCOUNTS = "accounts.json"
    private const val FILE_NAME_CATEGORIES = "categories.json"

    suspend fun save(context: Context) {
        withContext(Dispatchers.IO) {
            val gson = GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create()
            val db = AppDatabase.getInstance(context)
            db.accountDao().loadAllAccounts()?.let {
                val accountJson: String = gson.toJson(it)
                json2FileSave(context, accountJson, FILE_NAME_ACCOUNTS)
            }
            db.transactionDao().loadAllTransactions().let {
                val transactionsJson = gson.toJson(it)
                json2FileSave(context, transactionsJson, FILE_NAME_TRANSACTIONS)
            }
            db.categoryDao().loadCategories()?.let {
                val catJson = gson.toJson(it)
                json2FileSave(context, catJson, FILE_NAME_CATEGORIES)
            }
        }
    }

    private fun json2FileSave(context: Context, stringJson: String, filename: String) {
        val file = File(context.getExternalFilesDir(null), filename)
        val fos = FileOutputStream(file)
        fos.write(stringJson.toByteArray())
        fos.close()
    }

    suspend fun load(context: Context) {
        val gson = GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create()
        val db = AppDatabase.getInstance(context)

        withContext(Dispatchers.IO) {
            var transactionsJsonBuilder = StringBuilder()
            var accountsJsonBuilder = StringBuilder()
            var categoriesJsonBuilder = StringBuilder()

            try {
                transactionsJsonBuilder = readFromFile(context, FILE_NAME_TRANSACTIONS)
                accountsJsonBuilder = readFromFile(context, FILE_NAME_ACCOUNTS)
                categoriesJsonBuilder = readFromFile(context, FILE_NAME_CATEGORIES)
            } catch (e: IOException) {
                e.printStackTrace()
            }

            if (!TextUtils.isEmpty(transactionsJsonBuilder.toString())) {
                importTransactions(db, gson, transactionsJsonBuilder)
                importAccounts(db, gson, accountsJsonBuilder)
                importCategories(db, gson, categoriesJsonBuilder)
            }
            accountBalanceUpdateAll(db.transactionDao(), db.accountDao())
        }
    }

    private suspend fun importCategories(
            db: AppDatabase,
            gson: Gson, categoriesJsonBuilder:
            StringBuilder
    ) {
        db.categoryDao().deleteAllCategories()
        val listType2 = object : TypeToken<ArrayList<CategoryModel>>() {}.type
        val categories: List<CategoryModel> =
                gson.fromJson(categoriesJsonBuilder.toString(), listType2)
        for (category in categories) {
            db.categoryDao().insertCategory(category)
        }
    }

    private suspend fun importAccounts(
            db: AppDatabase,
            gson: Gson,
            accountsJsonBuilder: StringBuilder
    ) {
        db.accountDao().deleteAllAccounts()
        val listType1 = object : TypeToken<ArrayList<AccountModel>>() {}.type
        val transactions1: List<AccountModel> =
                gson.fromJson(accountsJsonBuilder.toString(), listType1)
        for (account in transactions1) {
            db.accountDao().insertAccount(account)
        }
    }

    private suspend fun importTransactions(
            db: AppDatabase,
            gson: Gson,
            transactionsJsonBuilder: StringBuilder
    ) {
        db.transactionDao().deleteAllTransactions()
        val listType = object : TypeToken<ArrayList<TransactionModel>>() {}.type
        val transactions: List<TransactionModel> =
                gson.fromJson(transactionsJsonBuilder.toString(), listType)
        for (transaction in transactions) {
            db.transactionDao().insertTransaction(transaction)
        }
    }

    private fun readFromFile(context: Context, filename: String): StringBuilder {
        val stringBuilder = StringBuilder()
        val file = File(context.getExternalFilesDir(null), filename)
        val fis = FileInputStream(file)
        val isr = InputStreamReader(fis)
        val br = BufferedReader(isr)

        var text = br.readLine()
        while (text != null) {
            stringBuilder.append(text).append("\n")
            text = br.readLine()
        }
        try {
            fis.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return stringBuilder
    }

}
