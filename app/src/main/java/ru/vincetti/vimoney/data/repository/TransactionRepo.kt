package ru.vincetti.vimoney.data.repository

import androidx.paging.DataSource
import ru.vincetti.vimoney.data.models.TransactionListModel
import ru.vincetti.vimoney.data.models.TransactionModel
import ru.vincetti.vimoney.data.models.TransactionStatDayModel
import ru.vincetti.vimoney.data.sqlite.AppDatabase
import ru.vincetti.vimoney.data.sqlite.TransactionDao
import ru.vincetti.vimoney.ui.history.filter.Filter
import ru.vincetti.vimoney.utils.DatesFormat
import java.time.LocalDate

@Suppress("TooManyFunctions")
class TransactionRepo(db: AppDatabase) {

    private val transactionDao: TransactionDao = db.transactionDao()

    suspend fun addTransaction(transaction: TransactionModel): Long {
        return transactionDao.insertTransaction(transaction)
    }

    suspend fun addTransaction(transactions: List<TransactionModel>) {
        transactionDao.insertTransaction(transactions)
    }

    suspend fun loadTransaction(id: Int) = transactionDao.loadTransactionById(id)

    suspend fun updateTransaction(transactionModel: TransactionModel) {
        transactionDao.updateTransaction(transactionModel)
    }

    suspend fun deleteTransaction(id: Int) {
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

    suspend fun loadTransactionStatMonth(localDate: LocalDate): List<TransactionStatDayModel> {
        val month: String = DatesFormat.getMonth00(localDate)
        val year: String = DatesFormat.getYear0000(localDate)

        return transactionDao.loadTransactionStatByMonth(month, year)
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

    suspend fun loadAllTransactions() = transactionDao.loadAllTransactions()

    suspend fun deleteAllTransactions() {
        transactionDao.deleteAllTransactions()
    }
}
