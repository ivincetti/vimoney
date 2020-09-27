package ru.vincetti.vimoney.ui.history

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.switchMap
import androidx.paging.toLiveData
import ru.vincetti.vimoney.data.repository.TransactionRepo
import ru.vincetti.vimoney.data.sqlite.AppDatabase
import ru.vincetti.vimoney.ui.history.filter.Filter

class HistoryViewModel(
    db: AppDatabase,
    initFilter: Filter
) : ViewModel() {

    private val repo: TransactionRepo = TransactionRepo(db)

    private var filter = MutableLiveData(initFilter)

    val transList = filter.switchMap {
        repo.loadFilterTransactions(it).toLiveData(pageSize = 20)
    }

    fun filter(newFilter: Filter) {
        filter.value = newFilter
    }
}

class HistoryViewModelFactory(
    private val db: AppDatabase,
    private val filter: Filter
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HistoryViewModel::class.java)) {
            return HistoryViewModel(db, filter) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
