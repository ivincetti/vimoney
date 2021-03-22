package ru.vincetti.modules.database.repository

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.vincetti.modules.core.models.Filter
import ru.vincetti.modules.core.models.Transaction
import ru.vincetti.modules.core.models.TransactionStatDay
import ru.vincetti.modules.core.utils.DatesFormat
import ru.vincetti.modules.database.sqlite.TransactionDao
import ru.vincetti.modules.database.sqlite.models.TransactionListModel
import ru.vincetti.modules.database.sqlite.models.TransactionModel
import java.time.LocalDate
import javax.inject.Inject

class TransactionRepo private constructor(
    private val transactionDao: TransactionDao,
    private val dispatcher: CoroutineDispatcher
) {

    @Inject
    constructor(transactionDao: TransactionDao) : this(transactionDao, Dispatchers.IO)

    suspend fun add(transaction: Transaction): Long = withContext(dispatcher) {
        transactionDao.insertTransaction(TransactionModel.from(transaction))
    }

    suspend fun add(transactions: List<Transaction>) = withContext(dispatcher) {
        val transactionModels = transactions.map { TransactionModel.from(it) }
        transactionDao.insertTransaction(transactionModels)
    }

    suspend fun update(transaction: Transaction) = withContext(dispatcher) {
        transactionDao.updateTransaction(TransactionModel.from(transaction))
    }

    suspend fun loadById(id: Int): Transaction? = withContext(dispatcher) {
        transactionDao.loadTransactionById(id)?.toTransaction()
    }

    suspend fun loadBalanceByCheckId(id: Int): Float = withContext(dispatcher) {
        transactionDao.loadSumByCheckId(id)
    }

    suspend fun loadIncomeExpenseMonth(localDate: LocalDate): Pair<Int, Int> = withContext(dispatcher) {
        val month: String = DatesFormat.getMonth00(localDate)
        val year: String = DatesFormat.getYear0000(localDate)

        val income = transactionDao.loadSumTransactionIncomeMonth(month, year)
        val expense = transactionDao.loadSumTransactionExpenseMonth(month, year)
        income to expense
    }

    fun loadIncomeMonthActualLive(): LiveData<Int> {
        return loadIncomeMonthLive(LocalDate.now())
    }

    fun loadExpenseMonthActualLive(): LiveData<Int> {
        return loadExpenseMonthLive(LocalDate.now())
    }

    suspend fun loadIncomeExpenseActual(): Pair<Int, Int> = withContext(dispatcher) {
        loadIncomeExpenseMonth(LocalDate.now())
    }

    suspend fun loadTransactionStatMonth(localDate: LocalDate): List<TransactionStatDay> = withContext(dispatcher) {
        val month: String = DatesFormat.getMonth00(localDate)
        val year: String = DatesFormat.getYear0000(localDate)

        transactionDao.loadTransactionStatByMonth(month, year).map { it.toTransactionStatDay() }
    }

    suspend fun loadAll(): List<Transaction> = withContext(dispatcher) {
        transactionDao.loadAllTransactions().map { it.toTransaction() }
    }

    suspend fun delete(transaction: Transaction) = withContext(dispatcher) {
        transactionDao.deleteTransaction(TransactionModel.from(transaction))
    }

    suspend fun deleteAll() = withContext(dispatcher) {
        transactionDao.deleteAllTransactions()
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

    private fun loadIncomeMonthLive(localDate: LocalDate): LiveData<Int> {
        val month: String = DatesFormat.getMonth00(localDate)
        val year: String = DatesFormat.getYear0000(localDate)

        return transactionDao.loadSumTransactionIncomeMonthLive(month, year)
    }

    private fun loadExpenseMonthLive(localDate: LocalDate): LiveData<Int> {
        val month: String = DatesFormat.getMonth00(localDate)
        val year: String = DatesFormat.getYear0000(localDate)

        return transactionDao.loadSumTransactionExpenseMonthLive(month, year)
    }
}
