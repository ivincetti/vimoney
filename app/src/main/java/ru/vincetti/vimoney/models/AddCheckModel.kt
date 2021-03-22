package ru.vincetti.vimoney.models

import ru.vincetti.modules.core.models.Account
import ru.vincetti.modules.core.models.Currency
import ru.vincetti.modules.database.repository.AccountRepo
import ru.vincetti.modules.database.repository.CurrencyRepo
import javax.inject.Inject

class AddCheckModel @Inject constructor(
    private val accountRepo: AccountRepo,
    private val currencyRepo: CurrencyRepo,
) {

    suspend fun getAllCurrency(): List<Currency> = currencyRepo.loadAll()

    suspend fun loadAccountById(id: Int) = accountRepo.loadById(id)

    suspend fun addAccount(acc: Account) {
        accountRepo.add(acc)
    }

    suspend fun updateAccount(acc: Account) {
        accountRepo.update(acc)
    }

    suspend fun archiveAccountById(id: Int) {
        accountRepo.archiveById(id)
    }

    suspend fun unArchiveAccountById(id: Int) {
        accountRepo.unArchiveById(id)
    }

    suspend fun loadCurrencyByCode(code: Int): Currency? {
        return currencyRepo.loadByCode(code)
    }
}
