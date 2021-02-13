package ru.vincetti.modules.database.repository

import ru.vincetti.modules.core.models.Currency
import ru.vincetti.modules.database.sqlite.CurrentDao
import ru.vincetti.modules.database.sqlite.models.CurrencyModel
import javax.inject.Inject

class CurrencyRepo @Inject constructor(
    private val currentDao: CurrentDao
) {

    suspend fun loadByCode(code: Int): Currency? {
        return currentDao.loadCurrencyByCode(code)?.toCurrency()
    }

    suspend fun loadAll(): List<Currency>? {
        return currentDao.loadAllCurrency()?.map { it.toCurrency() }
    }

    suspend fun add(cur: Currency) {
        currentDao.insertCurrency(CurrencyModel.from(cur))
    }

    suspend fun update(cur: Currency) {
        currentDao.updateCurrency(CurrencyModel.from(cur))
    }

    suspend fun delete(cur: Currency) {
        currentDao.deleteCurrency(CurrencyModel.from(cur))
    }
}
