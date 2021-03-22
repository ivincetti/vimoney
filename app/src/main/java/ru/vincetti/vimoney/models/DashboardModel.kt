package ru.vincetti.vimoney.models

import ru.vincetti.modules.core.models.TransactionStatDay
import ru.vincetti.modules.database.repository.TransactionRepo
import java.time.LocalDate
import javax.inject.Inject

class DashboardModel @Inject constructor(
    private val transactionRepo: TransactionRepo
) {

    suspend fun loadIncomeExpenseMonth(localDate: LocalDate): Pair<Int, Int> {
        return transactionRepo.loadIncomeExpenseMonth(localDate)
    }

    suspend fun loadTransactionStatMonth(localDate: LocalDate): List<TransactionStatDay> {
        return transactionRepo.loadTransactionStatMonth(localDate)
    }
}
