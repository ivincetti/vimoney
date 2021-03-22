package ru.vincetti.vimoney.models

import androidx.paging.DataSource
import ru.vincetti.modules.core.models.Filter
import ru.vincetti.modules.database.repository.TransactionRepo
import ru.vincetti.modules.database.sqlite.models.TransactionListModel
import javax.inject.Inject

class HistoryModel @Inject constructor(
    private val transactionRepo: TransactionRepo
) {

    fun loadFilterTransactions(filter: Filter): DataSource.Factory<Int, TransactionListModel> {
        return transactionRepo.loadFilterTransactions(filter)
    }
}
