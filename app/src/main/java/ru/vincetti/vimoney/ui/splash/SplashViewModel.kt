package ru.vincetti.vimoney.ui.splash

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.vincetti.vimoney.data.JsonDownloader
import ru.vincetti.vimoney.data.models.AccountModel
import ru.vincetti.vimoney.data.models.CategoryModel
import ru.vincetti.vimoney.data.models.CurrencyModel
import ru.vincetti.vimoney.data.models.TransactionModel
import ru.vincetti.vimoney.data.models.json.*
import ru.vincetti.vimoney.data.repository.*
import ru.vincetti.vimoney.utils.NetworkUtils
import ru.vincetti.vimoney.utils.SampleDescription
import ru.vincetti.vimoney.utils.SingleLiveEvent
import java.util.*
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val transactionRepo: TransactionRepo,
    private val accountRepo: AccountRepo,
    private val configRepo: ConfigRepo,
    private val categoryRepo: CategoryRepo,
    private val currencyRepo: CurrencyRepo,
    private val networkUtils: NetworkUtils,
    private val sampleDescription: SampleDescription
) : ViewModel() {

    private var _networkError = MutableLiveData<Boolean>()
    val networkError: LiveData<Boolean>
        get() = _networkError

    val need2Navigate2Home = SingleLiveEvent<Boolean>()

    val need2Navigate2Self = SingleLiveEvent<Boolean>()

    private val jsonDownloader by lazy {
        Retrofit.Builder()
            .baseUrl("https://vincetti.ru/vimoney/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(JsonDownloader::class.java)
    }

    init {
        _networkError.value = false
        checkDb()
    }

    private fun checkDb() {
        viewModelScope.launch {
            val config = configRepo.loadByKey()
            if (config == null) {
                if (networkUtils.isAvailable()) {
                    loadJsonFromServer()
                } else {
                    _networkError.value = true
                }
            } else {
                need2Navigate2Home.value = true
            }
        }
    }

    fun resetNetworkStatus() {
        _networkError.value = false
        need2Navigate2Self.value = true
    }

    private fun loadJsonFromServer() {
        viewModelScope.launch {
            try {
                val configModel = jsonDownloader.loadPreferences("Ru")
                configDbUpdate(configModel)
                need2Navigate2Home.value = true
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
        configRepo.add(timeMillisLong.toString())
    }

    private suspend fun transactionsImport(transactionItems: List<TransactionsItem>) {
        val transactions = mutableListOf<TransactionModel>()
        transactionItems.map {
            transactions.add(
                TransactionModel(
                    Date(it.date),
                    it.accountId,
                    sampleDescription.get(),
                    it.type,
                    it.sum.toFloat()
                )
            )
        }

        transactionRepo.add(transactions)
    }

    private suspend fun currencyImport(currencyItems: List<CurrencyItem>) {
        currencyItems.forEach {
            currencyRepo.add(
                CurrencyModel(it.code, it.name, it.symbol)
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
        accountRepo.add(newAcc)
        accountRepo.balanceUpdateById(accId)
    }

    private suspend fun categoriesUpdate(categoriesItems: List<CategoriesItem>) {
        categoriesItems.forEach {
            categoryUpdate(
                it.categoryId,
                it.name,
                it.symbol
            )
        }
    }

    private suspend fun categoryUpdate(catId: Int, name: String, symbol: String) {
        val newCat = CategoryModel(catId, name, symbol)
        categoryRepo.add(newCat)
    }
}
