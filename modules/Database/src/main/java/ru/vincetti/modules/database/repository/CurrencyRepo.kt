package ru.vincetti.modules.database.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.vincetti.modules.core.models.Currency
import ru.vincetti.modules.database.sqlite.CurrentDao
import ru.vincetti.modules.database.sqlite.models.CurrencyModel
import javax.inject.Inject

class CurrencyRepo private constructor(
    private val currentDao: CurrentDao,
    private val dispatcher: CoroutineDispatcher
) {

    @Inject
    constructor(currentDao: CurrentDao) : this(currentDao, Dispatchers.IO)

    fun loadLiveSymbolByCode(code: Int): LiveData<String?> {
        return currentDao.loadLiveCurrencyByCode(code).map { it?.symbol }
    }

    suspend fun loadByCode(code: Int): Currency? = withContext(dispatcher) {
        currentDao.loadCurrencyByCode(code)?.toCurrency()
    }

    suspend fun loadAll(): List<Currency> = withContext(dispatcher) {
        currentDao.loadAllCurrency().map { it.toCurrency() }
    }

    suspend fun add(cur: Currency) = withContext(dispatcher) {
        currentDao.insertCurrency(CurrencyModel.from(cur))
    }

    suspend fun update(cur: Currency) = withContext(dispatcher) {
        currentDao.updateCurrency(CurrencyModel.from(cur))
    }

    suspend fun delete(cur: Currency) = withContext(dispatcher) {
        currentDao.deleteCurrency(CurrencyModel.from(cur))
    }
}
