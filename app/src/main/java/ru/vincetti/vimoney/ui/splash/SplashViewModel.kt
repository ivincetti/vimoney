package ru.vincetti.vimoney.ui.splash

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.vincetti.vimoney.R
import ru.vincetti.vimoney.data.JsonDownloader
import ru.vincetti.vimoney.data.models.AccountModel
import ru.vincetti.vimoney.data.models.ConfigModel
import ru.vincetti.vimoney.data.models.CurrencyModel
import ru.vincetti.vimoney.data.models.TransactionModel
import ru.vincetti.vimoney.data.models.json.AccountsItem
import ru.vincetti.vimoney.data.models.json.ConfigFile
import ru.vincetti.vimoney.data.models.json.CurrencyItem
import ru.vincetti.vimoney.data.models.json.TransactionsItem
import ru.vincetti.vimoney.data.sqlite.AppDatabase
import ru.vincetti.vimoney.utils.LogicMath
import java.util.*

class SplashViewModel(val app: Application) : AndroidViewModel(app) {

    private var _networkError = MutableLiveData<Boolean>()
    val networkError
        get() = _networkError

    private var _need2Navigate2Home = MutableLiveData<Boolean>()
    val need2Navigate2Home
        get() = _need2Navigate2Home

    private var jsonDownloader = Retrofit.Builder()
            .baseUrl("https://vincetti.ru/vimoney/")
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(JsonDownloader::class.java)
    private var mDb: AppDatabase = AppDatabase.getInstance(app)

    init {
        _networkError.value = false
        _need2Navigate2Home.value = false
        init()
    }

    private fun init() {
        viewModelScope.launch {
            val config = mDb.configDao().loadConfigByKey(AppDatabase.CONFIG_KEY_NAME_DATE_EDIT)
            if (config == null) {
                if (!_networkError.value!!) {
                    loadJson()
                }
            } else {
                _need2Navigate2Home.value = true
            }
        }
    }

    /** No network. */
    fun setNoNetwork(){
        _networkError.value = true
    }

    private fun loadJson() {
        viewModelScope.launch {
            try {
                val configModel = jsonDownloader.loadPreferences("Ru")
                configDbUpdate(configModel)
                _need2Navigate2Home.value = true
            } catch (e: Exception) {
                _networkError.value = true
            }

        }
    }

    private suspend fun configDbUpdate(response: ConfigFile) {
        configDbDateInsert(response.dateEdit)
        // user info to base
        userUpdate(response.user.name)
        transactionsImport(response.transactions)
        currencyImport(response.currency)
        accountsUpdate(response.accounts)
    }

    private suspend fun configDbDateInsert(timeMillisLong: Long) {
        val newConfig = ConfigModel(
                keyName = AppDatabase.CONFIG_KEY_NAME_DATE_EDIT,
                value = timeMillisLong.toString())
        mDb.configDao().insertConfig(newConfig)
    }

    private suspend fun userUpdate(user: String) {
        val mUser = mDb.configDao().loadConfigByKey(AppDatabase.CONFIG_KEY_NAME_USER_NAME)
        val newUser = ConfigModel(
                keyName = AppDatabase.CONFIG_KEY_NAME_USER_NAME,
                value = user)
        if (mUser == null) {
            mDb.configDao().insertConfig(newUser)
        } else {
            newUser.id = mUser.id
            mDb.configDao().updateConfig(newUser)
        }
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
                    CurrencyModel(cur.code, cur.name, cur.symbol))
        }
    }

    private suspend fun accountsUpdate(accountsItems: List<AccountsItem>) {
        for (acc in accountsItems) {
            accountUpdate(
                    acc.id,
                    acc.type,
                    acc.title,
                    acc.instrument,
                    acc.balance)
        }
    }

    private suspend fun accountUpdate(accId: Int, type: String, title: String, ins: Int, balance: Int) {
        val newAcc = AccountModel(accId, title, type, balance, 810)
        mDb.accountDao().insertAccount(newAcc)
        LogicMath.accountBalanceUpdateById(mDb.transactionDao(), mDb.accountDao(), accId)
    }
}