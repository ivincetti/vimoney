package ru.vincetti.vimoney.ui.history

import androidx.lifecycle.*
import androidx.paging.PagedList
import androidx.paging.toLiveData
import ru.vincetti.vimoney.data.models.TransactionListModel
import ru.vincetti.vimoney.data.repository.TransactionRepo
import ru.vincetti.vimoney.data.sqlite.AppDatabase
import ru.vincetti.vimoney.ui.history.filter.Filter

class HistoryViewModel(
    db: AppDatabase,
    initFilter: Filter
) : ViewModel() {

    private val repo: TransactionRepo = TransactionRepo(db)

    private var filter = MutableLiveData<Filter>()
        .apply { value = initFilter }

    private var _transList = filter.switchMap {
        repo.loadFilterTransactions(it).toLiveData(pageSize = HistoryFragment.DEFAULT_CHECK_COUNT)
    }
    val transList: LiveData<PagedList<TransactionListModel>>
        get() = _transList

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
