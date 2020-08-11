package ru.vincetti.vimoney.ui.splash

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.vincetti.vimoney.R
import ru.vincetti.vimoney.data.JsonDownloader
import ru.vincetti.vimoney.data.models.*
import ru.vincetti.vimoney.data.models.json.*
import ru.vincetti.vimoney.data.sqlite.AppDatabase
import ru.vincetti.vimoney.utils.accountBalanceUpdateById
import ru.vincetti.vimoney.utils.isNetworkAvailable
import java.util.*

class SplashViewModel(
    val app: Application
) : AndroidViewModel(app) {

    private var _networkError = MutableLiveData<Boolean>()
    val networkError: LiveData<Boolean>
        get() = _networkError

    private var _need2Navigate2Home = MutableLiveData<Boolean>()
    val need2Navigate2Home: LiveData<Boolean>
        get() = _need2Navigate2Home

    private var _need2Navigate2Self = MutableLiveData<Boolean>()
    val need2Navigate2Self: LiveData<Boolean>
        get() = _need2Navigate2Self

    private val jsonDownloader by lazy {
        Retrofit.Builder()
            .baseUrl("https://vincetti.ru/vimoney/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(JsonDownloader::class.java)
    }

    private var mDb: AppDatabase = AppDatabase.getInstance(app)

    init {
        _networkError.value = false
        _need2Navigate2Home.value = false
        _need2Navigate2Self.value = false
        checkDb()
    }

    /** Check not empty DB. */
    private fun checkDb() {
        viewModelScope.launch {
            val config = mDb.configDao().loadConfigByKey(AppDatabase.CONFIG_KEY_NAME_DATE_EDIT)
            if (config == null) {
                if (!_networkError.value!!) {
                    loadJsonFromServer()
                }
            } else {
                _need2Navigate2Home.value = true
            }
        }
    }

    /** No network. */
    fun setNetwork(context: Context) {
        if (!isNetworkAvailable(context)) _networkError.value = true
    }

    /** Reload fragment from scratch. */
    fun resetNetworkStatus() {
        _networkError.value = false
        _need2Navigate2Self.value = true
    }

    private fun loadJsonFromServer() {
        viewModelScope.launch {
            try {
                val configModel = jsonDownloader.loadPreferences("Ru")
                configDbUpdate(configModel)
                _need2Navigate2Home.value = true
            } catch (e: Exception) {
                Log.d("TAG", " load from json error ${e.message}")
                _networkError.value = true
            }
        }
    }

    private suspend fun configDbUpdate(response: ConfigFile) {
        configDbDateInsert(response.dateEdit)
        transactionsImport(response.transactions)
        currencyImport(response.currency)
        accountsUpdate(response.accounts)
        categoriesUpdate(response.categories)
    }

    private suspend fun configDbDateInsert(timeMillisLong: Long) {
        val newConfig = ConfigModel(
            keyName = AppDatabase.CONFIG_KEY_NAME_DATE_EDIT,
            value = timeMillisLong.toString()
        )
        mDb.configDao().insertConfig(newConfig)
    }

    private suspend fun transactionsImport(transactionItems: List<TransactionsItem>) {
        for (tr in transactionItems) {
            mDb.transactionDao().insertTransaction(
                TransactionModel(
                    Date(tr.date),
                    tr.accountId,
                    app.getString(R.string.transaction_import_sample_desc),
                    tr.type,
                    tr.sum.toFloat()
                )
            )
        }
    }

    private suspend fun currencyImport(currencyItems: List<CurrencyItem>) {
        for (cur in currencyItems) {
            mDb.currentDao().insertCurrency(
                CurrencyModel(cur.code, cur.name, cur.symbol)
            )
        }
    }

    private suspend fun accountsUpdate(accountsItems: List<AccountsItem>) {
        for (acc in accountsItems) {
            accountUpdate(
                acc.id,
                acc.type,
                acc.title,
                acc.balance
            )
        }
    }

    private suspend fun accountUpdate(accId: Int, type: String, title: String, balance: Int) {
        val newAcc = AccountModel(accId, title, type, balance, 810)
        mDb.accountDao().insertAccount(newAcc)
        accountBalanceUpdateById(mDb.transactionDao(), mDb.accountDao(), accId)
    }

    private suspend fun categoriesUpdate(categoriesItems: List<CategoriesItem>) {
        for (cat in categoriesItems) {
            categoryUpdate(
                cat.categoryId,
                cat.name,
                cat.symbol
            )
        }
    }

    private suspend fun categoryUpdate(catId: Int, name: String, symbol: String) {
        val newCat = CategoryModel(catId, name, symbol)
        mDb.categoryDao().insertCategory(newCat)
    }
}
