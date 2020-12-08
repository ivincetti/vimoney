package ru.vincetti.vimoney.data.repository

import ru.vincetti.vimoney.data.models.CurrencyModel
import ru.vincetti.vimoney.data.sqlite.CurrentDao
import javax.inject.Inject

class CurrencyRepo @Inject constructor(
    private val currentDao: CurrentDao
) {

    suspend fun loadByCode(code: Int): CurrencyModel? {
        return currentDao.loadCurrencyByCode(code)
    }

    suspend fun loadAll(): List<CurrencyModel>? {
        return currentDao.loadAllCurrency()
    }

    suspend fun add(cur: CurrencyModel) {
        currentDao.insertCurrency(cur)
    }

    suspend fun update(cur: CurrencyModel) {
        currentDao.updateCurrency(cur)
    }

    suspend fun delete(cur: CurrencyModel) {
        currentDao.deleteCurrency(cur)
    }
}
