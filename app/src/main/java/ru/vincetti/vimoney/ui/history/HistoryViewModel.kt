package ru.vincetti.vimoney.ui.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.vincetti.vimoney.data.models.TransactionListModel
import ru.vincetti.vimoney.data.sqlite.TransactionDao

class HistoryViewModel(
        trDao: TransactionDao,
        count: Int,
        checkID: Int?
) : ViewModel() {

    val transList: LiveData<List<TransactionListModel>> =
            if (checkID != null) {
                trDao.loadCheckTransactionsCountFull(checkID, count)
            } else {
                trDao.loadAllTransactionsCountFull(count)
            }

    companion object {
        const val DEFAULT_TRANSACTIONS = 10
    }
}

class HistoryViewModelFactory(
        private val trDao: TransactionDao,
        private val count: Int,
        private val checkID: Int?
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HistoryViewModel::class.java)) {
            return HistoryViewModel(trDao, count, checkID) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
