package ru.vincetti.modules.database.repository

import androidx.paging.DataSource
import ru.vincetti.modules.core.models.Filter
import ru.vincetti.modules.core.models.Transaction
import ru.vincetti.modules.database.sqlite.models.TransactionListModel
import ru.vincetti.modules.core.models.TransactionStatDay
import ru.vincetti.modules.core.utils.DatesFormat
import ru.vincetti.modules.database.sqlite.TransactionDao
import ru.vincetti.modules.database.sqlite.models.TransactionModel
import java.time.LocalDate
import javax.inject.Inject

@Suppress("TooManyFunctions")
class TransactionRepo @Inject constructor(
    private val transactionDao: TransactionDao
) {

    suspend fun add(transaction: Transaction): Long {
        return transactionDao.insertTransaction(TransactionModel.from(transaction))
    }

    suspend fun add(transactions: List<Transaction>) {
        val transactionModels = transactions.map { TransactionModel.from(it) }
        transactionDao.insertTransaction(transactionModels)
    }

    suspend fun loadById(id: Int): Transaction? {
        return transactionDao.loadTransactionById(id)?.toTransaction()
    }

    suspend fun update(transaction: Transaction) {
        transactionDao.updateTransaction(TransactionModel.from(transaction))
    }

    suspend fun delete(id: Int) {
        transactionDao.deleteTransactionById(id)
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

    suspend fun loadCheckSum(checkID: Int) = transactionDao.loadSumByCheckId(checkID)

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

    suspend fun deleteAll() {
        transactionDao.deleteAllTransactions()
    }
}
