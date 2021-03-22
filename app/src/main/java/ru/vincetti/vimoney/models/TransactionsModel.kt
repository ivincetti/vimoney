package ru.vincetti.vimoney.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import ru.vincetti.modules.core.models.AccountList
import ru.vincetti.modules.core.models.Category
import ru.vincetti.modules.core.models.Transaction
import ru.vincetti.modules.database.repository.AccountRepo
import ru.vincetti.modules.database.repository.CategoryRepo
import ru.vincetti.modules.database.repository.TransactionRepo
import javax.inject.Inject

class TransactionsModel @Inject constructor(
    private val accountRepo: AccountRepo,
    private val transactionRepo: TransactionRepo,
    private val categoryRepo: CategoryRepo,
) {

    private val allAccounts: LiveData<List<AccountList>> = accountRepo.loadAllFull()

    fun loadNotArchiveAccounts(): LiveData<List<AccountList>> = allAccounts.map { list ->
        list.filter { it.isArchive.not() }
    }

    fun loadAccountById(id: Int): LiveData<AccountList?> = accountRepo.loadLiveForListById(id)

    fun loadCategoryById(id: Int): LiveData<Category?> = categoryRepo.loadByIdLive(id)

    fun loadAllCategories(): LiveData<List<Category>> = categoryRepo.loadAllLive()

    suspend fun loadTransactionById(id: Int): Transaction? = transactionRepo.loadById(id)

    suspend fun addTransaction(transaction: Transaction): Long {
        val id = transactionRepo.add(transaction)
        val newBalance = transactionRepo.loadBalanceByCheckId(transaction.accountId)
        accountRepo.updateAccountBalanceById(transaction.accountId, newBalance)
        return id
    }

    suspend fun updateTransaction(transaction: Transaction) {
        transactionRepo.update(transaction)
        val newBalance = transactionRepo.loadBalanceByCheckId(transaction.accountId)
        accountRepo.updateAccountBalanceById(transaction.accountId, newBalance)
    }

    suspend fun deleteTransaction(transaction: Transaction) {
        transactionRepo.delete(transaction)
    }
}
