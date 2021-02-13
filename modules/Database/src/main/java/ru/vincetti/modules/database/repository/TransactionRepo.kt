package ru.vincetti.modules.database.repository

import androidx.paging.DataSource
import ru.vincetti.modules.core.models.Filter
import ru.vincetti.modules.core.models.Transaction
import ru.vincetti.modules.core.models.TransactionStatDay
import ru.vincetti.modules.core.utils.DatesFormat
import ru.vincetti.modules.database.sqlite.TransactionDao
import ru.vincetti.modules.database.sqlite.models.TransactionListModel
import ru.vincetti.modules.database.sqlite.models.TransactionModel
import java.time.LocalDate
import javax.inject.Inject

@Suppress("TooManyFunctions")
class TransactionRepo @Inject constructor(
    private val transactionDao: TransactionDao,
    private val accountRepo: AccountRepo
) {

    suspend fun add(transaction: Transaction): Long {
        val id = transactionDao.insertTransaction(TransactionModel.from(transaction))
        accountRepo.balanceUpdateById(transaction.accountId)
        return id
    }

    suspend fun add(transactions: List<Transaction>) {
        val transactionModels = transactions.map { TransactionModel.from(it) }
        transactionDao.insertTransaction(transactionModels)
        accountRepo.balanceUpdateAll()
    }

    suspend fun loadById(id: Int): Transaction? {
        return transactionDao.loadTransactionById(id)?.toTransaction()
    }

    suspend fun update(transaction: Transaction) {
        transactionDao.updateTransaction(TransactionModel.from(transaction))
        accountRepo.balanceUpdateById(transaction.accountId)
    }

    suspend fun loadIncomeExpenseMonth(localDate: LocalDate): Pair<Int, Int> {
        val month: String = DatesFormat.getMonth00(localDate)
        val year: String = DatesFormat.getYear0000(localDate)

        val income = transactionDao.loadSumTransactionIncomeMonth(month, year)
        val expense = transactionDao.loadSumTransactionExpenseMonth(month, year)
        return income to expense
    }

    suspend fun loadIncomeExpenseActual(): Pair<Int, Int> {
        return loadIncomeExpenseMonth(LocalDate.now())
    }

    suspend fun loadTransactionStatMonth(localDate: LocalDate): List<TransactionStatDay> {
        val month: String = DatesFormat.getMonth00(localDate)
        val year: String = DatesFormat.getYear0000(localDate)

        return transactionDao.loadTransactionStatByMonth(month, year).map { it.toTransactionStatDay() }
    }

    fun loadFilterTransactions(filter: Filter): DataSource.Factory<Int, TransactionListModel> {
        return if (filter.count > 0) {
            transactionDao.loadFilterTransactionsFullLimit(
                filter.accountID,
                filter.categoryID,
                filter.comment,
                filter.dateFrom,
                filter.dateTo,
                filter.sumFrom,
                filter.count
            )
        } else {
            transactionDao.loadFilterTransactionsFull(
                filter.accountID,
                filter.categoryID,
                filter.comment,
                filter.dateFrom,
                filter.dateTo,
                filter.sumFrom
            )
        }
    }

    suspend fun loadAll(): List<Transaction> {
        return transactionDao.loadAllTransactions().map { it.toTransaction() }
    }

    suspend fun delete(transaction: Transaction) {
        transactionDao.deleteTransaction(TransactionModel.from(transaction))
        accountRepo.balanceUpdateById(transaction.accountId)
    }

    suspend fun deleteAll() {
        transactionDao.deleteAllTransactions()
    }
}
