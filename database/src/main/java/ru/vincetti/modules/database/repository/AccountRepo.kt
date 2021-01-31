package ru.vincetti.modules.database.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.map
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.vincetti.modules.core.models.Account
import ru.vincetti.modules.core.models.AccountList
import ru.vincetti.modules.database.sqlite.AccountDao
import ru.vincetti.modules.database.sqlite.models.AccountModel
import javax.inject.Inject

@Suppress("TooManyFunctions")
class AccountRepo @Inject constructor(
    private val accountDao: AccountDao,
    private val transactionRepo: TransactionRepo
) {

    suspend fun loadById(id: Int): Account? {
        return accountDao.loadAccountById(id)?.toAccount()
    }

    fun loadForListById(id: Int): LiveData<AccountList> {
        return accountDao.loadAccountByIdFull(id).map { it.toAccountList() }
    }

    suspend fun archiveById(id: Int) {
        accountDao.archiveAccountById(id)
    }

    suspend fun unArchiveById(id: Int) {
        accountDao.fromArchiveAccountById(id)
    }

    fun loadMain(): LiveData<List<AccountList>> {
        return Transformations.map(accountDao.loadMainAccountsFull()) {
            it.map { it.toAccountList() }
        }
    }

    suspend fun loadNotArchive(): List<AccountList>? {
        return accountDao.loadNotArchiveAccounts()?.map { it.toAccountList() }
    }

    suspend fun loadAll(): List<Account>? {
        return accountDao.loadAllAccounts()?.map { it.toAccount() }
    }

    fun loadAllFull(): LiveData<List<AccountList>> {
        return Transformations.map(accountDao.loadAllAccountsFull()) {
            it.map { it.toAccountList() }
        }
    }

    suspend fun balanceUpdate(): Int {
        var balance = 0
        loadAll()?.let { list ->
            list.filter { it.needAllBalance && !it.isArchive }.forEach { balance += it.sum }
        }
        return balance
    }

    suspend fun balanceUpdateById(accId: Int) {
        val sum = transactionRepo.loadCheckSum(accId)
        updateSumById(accId, sum)
    }

    suspend fun balanceUpdateAll() {
        withContext(Dispatchers.IO) {
            loadAll()?.forEach {
                val sum = transactionRepo.loadCheckSum(it.id)
                updateSumById(it.id, sum)
            }
        }
    }

    suspend fun add(acc: Account) {
        accountDao.insertAccount(AccountModel.from(acc))
    }

    suspend fun add(acc: List<Account>) {
        accountDao.insertAccount(
            acc.map {
                AccountModel.from(it)
            }
        )
    }

    suspend fun update(acc: Account) {
        accountDao.updateAccount(AccountModel.from(acc))
    }

    suspend fun deleteAll() {
        accountDao.deleteAllAccounts()
    }

    private suspend fun updateSumById(accId: Int, sum: Float) {
        accountDao.updateSumByAccId(accId, sum)
    }
}
