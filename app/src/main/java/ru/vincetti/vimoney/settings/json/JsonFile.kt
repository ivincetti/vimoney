package ru.vincetti.vimoney.settings.json

import android.content.Context
import android.text.TextUtils
import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.vincetti.vimoney.data.models.AccountModel
import ru.vincetti.vimoney.data.models.CategoryModel
import ru.vincetti.vimoney.data.models.TransactionModel
import ru.vincetti.vimoney.data.repository.AccountRepo
import ru.vincetti.vimoney.data.repository.CategoryRepo
import ru.vincetti.vimoney.data.repository.TransactionRepo
import java.io.*
import java.util.*
import javax.inject.Inject

class JsonFile @Inject constructor(
    @ApplicationContext private val context: Context,
    private val categoryRepo: CategoryRepo,
    private val transactionRepo: TransactionRepo,
    private val accountRepo: AccountRepo
) {

    companion object {
        private const val FILE_NAME_TRANSACTIONS = "transactions.json"
        private const val FILE_NAME_ACCOUNTS = "accounts.json"
        private const val FILE_NAME_CATEGORIES = "categories.json"
    }

    suspend fun save() {
        withContext(Dispatchers.IO) {
            val gson = GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create()
            accountRepo.loadAll()?.let {
                val accountJson: String = gson.toJson(it)
                json2FileSave(accountJson, FILE_NAME_ACCOUNTS)
            }
            transactionRepo.loadAll().let {
                val transactionsJson = gson.toJson(it)
                json2FileSave(transactionsJson, FILE_NAME_TRANSACTIONS)
            }
            categoryRepo.loadAll()?.let {
                val catJson = gson.toJson(it)
                json2FileSave(catJson, FILE_NAME_CATEGORIES)
            }
        }
    }

    private fun json2FileSave(stringJson: String, filename: String) {
        val file = File(context.getExternalFilesDir(null), filename)
        val fos = FileOutputStream(file)
        fos.write(stringJson.toByteArray())
        fos.close()
    }

    suspend fun load() {
        val gson = GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create()

        withContext(Dispatchers.IO) {
            var transactionsJsonBuilder = StringBuilder()
            var accountsJsonBuilder = StringBuilder()
            var categoriesJsonBuilder = StringBuilder()

            try {
                transactionsJsonBuilder = readFromFile(FILE_NAME_TRANSACTIONS)
                accountsJsonBuilder = readFromFile(FILE_NAME_ACCOUNTS)
                categoriesJsonBuilder = readFromFile(FILE_NAME_CATEGORIES)
            } catch (e: IOException) {
                e.printStackTrace()
            }

            if (!TextUtils.isEmpty(transactionsJsonBuilder.toString())) {
                importTransactions(gson, transactionsJsonBuilder)
                importAccounts(gson, accountsJsonBuilder)
                importCategories(gson, categoriesJsonBuilder)
            }
            accountRepo.balanceUpdateAll()
        }
    }

    private suspend fun importCategories(
        gson: Gson,
        categoriesJsonBuilder: StringBuilder
    ) {
        categoryRepo.deleteAll()
        val listType2 = object : TypeToken<ArrayList<CategoryModel>>() {}.type
        val categories: List<CategoryModel> =
            gson.fromJson(categoriesJsonBuilder.toString(), listType2)
        categories.forEach {
            categoryRepo.add(it)
        }
    }

    private suspend fun importAccounts(
        gson: Gson,
        accountsJsonBuilder: StringBuilder
    ) {
        accountRepo.deleteAll()
        val listType1 = object : TypeToken<ArrayList<AccountModel>>() {}.type
        val accounts: List<AccountModel> =
            gson.fromJson(accountsJsonBuilder.toString(), listType1)
        accounts.forEach {
            accountRepo.add(it)
        }
    }

    private suspend fun importTransactions(
        gson: Gson,
        transactionsJsonBuilder: StringBuilder
    ) {
        transactionRepo.deleteAll()
        val listType = object : TypeToken<ArrayList<TransactionModel>>() {}.type
        val transactions: List<TransactionModel> =
            gson.fromJson(transactionsJsonBuilder.toString(), listType)
        transactionRepo.add(transactions)
    }

    private fun readFromFile(filename: String): StringBuilder {
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
            Log.d("TAG", "read from file error ${e.message}")
        }

        return stringBuilder
    }
}
