package ru.vincetti.modules.files

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.vincetti.modules.core.models.Account
import ru.vincetti.modules.core.models.Category
import ru.vincetti.modules.core.models.Transaction
import java.io.*
import java.util.*
import javax.inject.Inject

class JsonFile private constructor(
    private val context: Context,
    private val dispatcher: CoroutineDispatcher
) {

    @Inject
    constructor(@ApplicationContext context: Context) : this(context, Dispatchers.IO)

    companion object {
        private const val FILE_NAME_TRANSACTIONS = "transactions.json"
        private const val FILE_NAME_ACCOUNTS = "accounts.json"
        private const val FILE_NAME_CATEGORIES = "categories.json"
    }

    suspend fun save(
        data: Triple<List<Transaction>, List<Account>, List<Category>>,
    ) {
        withContext(dispatcher) {
            val gson = GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create()

            val transactionsJson = gson.toJson(data.first)
            json2FileSave(transactionsJson, FILE_NAME_TRANSACTIONS)

            val accountJson: String = gson.toJson(data.second)
            json2FileSave(accountJson, FILE_NAME_ACCOUNTS)

            val catJson = gson.toJson(data.third)
            json2FileSave(catJson, FILE_NAME_CATEGORIES)
        }
    }

    private fun json2FileSave(stringJson: String, filename: String) {
        val file = File(context.getExternalFilesDir(null), filename)
        val fos = FileOutputStream(file)
        fos.write(stringJson.toByteArray())
        fos.close()
    }

    suspend fun getData(): Triple<List<Transaction>, List<Account>, List<Category>> {
        val gson = GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create()

        var transactionsJsonBuilder = StringBuilder()
        var accountsJsonBuilder = StringBuilder()
        var categoriesJsonBuilder = StringBuilder()

        withContext(dispatcher) {
            try {
                transactionsJsonBuilder = readFromFile(FILE_NAME_TRANSACTIONS)
                accountsJsonBuilder = readFromFile(FILE_NAME_ACCOUNTS)
                categoriesJsonBuilder = readFromFile(FILE_NAME_CATEGORIES)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        return Triple(
            importTransactions(gson, transactionsJsonBuilder),
            importAccounts(gson, accountsJsonBuilder),
            importCategories(gson, categoriesJsonBuilder)
        )
    }

    private fun importCategories(
        gson: Gson,
        categoriesJsonBuilder: StringBuilder,
    ): List<Category> {
        val listType2 = object : TypeToken<ArrayList<Category>>() {}.type
        return gson.fromJson(categoriesJsonBuilder.toString(), listType2)
    }

    private fun importAccounts(
        gson: Gson,
        accountsJsonBuilder: StringBuilder,
    ): List<Account> {
        val listType1 = object : TypeToken<ArrayList<Account>>() {}.type
        return gson.fromJson(accountsJsonBuilder.toString(), listType1)
    }

    private fun importTransactions(
        gson: Gson,
        transactionsJsonBuilder: StringBuilder,
    ): List<Transaction> {
        val listType = object : TypeToken<ArrayList<Transaction>>() {}.type
        return gson.fromJson(transactionsJsonBuilder.toString(), listType)
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
