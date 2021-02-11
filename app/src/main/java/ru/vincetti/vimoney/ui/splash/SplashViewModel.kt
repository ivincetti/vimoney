package ru.vincetti.vimoney.ui.splash

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.vincetti.modules.core.models.Account
import ru.vincetti.modules.core.models.Category
import ru.vincetti.modules.core.models.Currency
import ru.vincetti.modules.core.models.Transaction
import ru.vincetti.modules.core.utils.SingleLiveEvent
import ru.vincetti.modules.database.repository.*
import ru.vincetti.modules.network.ConfigLoader
import ru.vincetti.modules.network.models.*
import ru.vincetti.vimoney.utils.NetworkUtils
import ru.vincetti.vimoney.utils.SampleDescription
import java.util.*
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val transactionRepo: TransactionRepo,
    private val accountRepo: AccountRepo,
    private val configRepo: ConfigRepo,
    private val categoryRepo: CategoryRepo,
    private val currencyRepo: CurrencyRepo,
    private val configLoader: ConfigLoader,
    private val networkUtils: NetworkUtils,
    private val sampleDescription: SampleDescription
) : ViewModel() {

    private var _networkError = MutableLiveData<Boolean>()
    val networkError: LiveData<Boolean>
        get() = _networkError

    val need2Navigate2Home = SingleLiveEvent<Boolean>()

    val need2Navigate2Self = SingleLiveEvent<Boolean>()

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
                val configModel = configLoader.loadPreferences()
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
        val transactions = mutableListOf<Transaction>()
        transactionItems.map {
            transactions.add(
                Transaction(
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
                Currency(it.code, it.name, it.symbol)
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
        val newAcc = Account(accId, title, type, balance, 810)
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
        val newCat = Category(catId, name, symbol)
        categoryRepo.add(newCat)
    }
}
