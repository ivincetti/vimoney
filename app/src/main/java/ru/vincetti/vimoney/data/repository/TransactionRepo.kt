package ru.vincetti.vimoney.data.repository

import ru.vincetti.vimoney.data.models.TransactionStatDayModel
import ru.vincetti.vimoney.data.sqlite.AppDatabase
import ru.vincetti.vimoney.data.sqlite.TransactionDao
import ru.vincetti.vimoney.utils.DatesFormat
import java.time.LocalDate

class TransactionRepo(db: AppDatabase) {

    private val dao: TransactionDao = db.transactionDao()

    suspend fun loadIncomeExpenseMonth(localDate: LocalDate): Pair<Int, Int> {
        val month: String = DatesFormat.getMonth00(localDate)
        val year: String = DatesFormat.getYear0000(localDate)

        val income = dao.loadSumTransactionIncomeMonth(month, year)
        val expense = dao.loadSumTransactionExpenseMonth(month, year)
        return income to expense
    }

    suspend fun loadIncomeExpenseActual(): Pair<Int, Int> {
        return loadIncomeExpenseMonth(LocalDate.now())
    }

    suspend fun loadTransactionStatMonth(localDate: LocalDate): List<TransactionStatDayModel> {
        val month: String = DatesFormat.getMonth00(localDate)
        val year: String = DatesFormat.getYear0000(localDate)

        return dao.loadTransactionStatByMonth(month, year)
    }

    suspend fun loadCheckSum(checkID: Int) = dao.loadSumByCheckId(checkID)
}
