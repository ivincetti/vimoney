package ru.vincetti.vimoney.data.repository

import androidx.lifecycle.LiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.vincetti.vimoney.data.models.AccountListModel
import ru.vincetti.vimoney.data.models.AccountModel
import ru.vincetti.vimoney.data.sqlite.AccountDao
import javax.inject.Inject

@Suppress("TooManyFunctions")
class AccountRepo @Inject constructor(
    private val accountDao: AccountDao
) {

    @Inject
    lateinit var transactionRepo: TransactionRepo

    suspend fun loadById(id: Int): AccountModel? {
        return accountDao.loadAccountById(id)
    }

    fun loadForListById(id: Int): LiveData<AccountListModel> {
        return accountDao.loadAccountByIdFull(id)
    }

    suspend fun archiveById(id: Int) {
        accountDao.archiveAccountById(id)
    }

    suspend fun unArchiveById(id: Int) {
        accountDao.fromArchiveAccountById(id)
    }

    fun loadMain(): LiveData<List<AccountListModel>> {
        return accountDao.loadMainAccountsFull()
    }

    suspend fun loadNotArchive(): List<AccountListModel>? {
        return accountDao.loadNotArchiveAccounts()
    }

    suspend fun loadAll(): List<AccountModel>? {
        return accountDao.loadAllAccounts()
    }

    fun loadAllFull(): LiveData<List<AccountListModel>> {
        return accountDao.loadAllAccountsFull()
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

    suspend fun add(acc: AccountModel) {
        accountDao.insertAccount(acc)
    }

    suspend fun update(acc: AccountModel) {
        accountDao.updateAccount(acc)
    }

    suspend fun deleteAll() {
        accountDao.deleteAllAccounts()
    }

    private suspend fun updateSumById(accId: Int, sum: Float) {
        accountDao.updateSumByAccId(accId, sum)
    }
}
