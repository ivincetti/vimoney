package ru.vincetti.modules.database.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.vincetti.modules.core.models.Account
import ru.vincetti.modules.core.models.AccountList
import ru.vincetti.modules.database.sqlite.AccountDao
import ru.vincetti.modules.database.sqlite.models.AccountModel
import javax.inject.Inject

@Suppress("TooManyFunctions")
class AccountRepo private constructor(
    private val accountDao: AccountDao,
    private val dispatcher: CoroutineDispatcher
) {

    @Inject
    constructor(accountDao: AccountDao) : this(accountDao, Dispatchers.IO)

    fun loadLiveForListById(id: Int): LiveData<AccountList?> {
        return accountDao.loadListByIdLive(id).map { it?.toAccountList() }
    }

    suspend fun loadForListById(id: Int): AccountList? {
        return accountDao.loadListById(id)?.toAccountList()
    }

    fun loadAllFull(): LiveData<List<AccountList>> {
        return accountDao.loadAllFullLive().map { list ->
            list.map { it.toAccountList() }
        }
    }

    suspend fun loadById(id: Int): Account? = withContext(dispatcher) {
        accountDao.loadById(id)?.toAccount()
    }

    suspend fun loadAll(): List<Account> = withContext(dispatcher) {
        accountDao.loadAll().map { it.toAccount() }
    }

    suspend fun archiveById(id: Int) = withContext(dispatcher) {
        accountDao.archiveById(id)
    }

    suspend fun unArchiveById(id: Int) = withContext(dispatcher) {
        accountDao.fromArchiveById(id)
    }

    suspend fun add(acc: Account) = withContext(dispatcher) {
        accountDao.insert(AccountModel.from(acc))
    }

    suspend fun add(acc: List<Account>) = withContext(dispatcher) {
        accountDao.insert(
            acc.map { AccountModel.from(it) }
        )
    }

    suspend fun update(acc: Account) = withContext(dispatcher) {
        accountDao.update(AccountModel.from(acc))
    }

    suspend fun deleteAll() = withContext(dispatcher) {
        accountDao.deleteAll()
    }

    suspend fun updateAccountBalanceById(accId: Int, sum: Float) = withContext(dispatcher) {
        accountDao.updateSumByAccId(accId, sum)
    }
}
