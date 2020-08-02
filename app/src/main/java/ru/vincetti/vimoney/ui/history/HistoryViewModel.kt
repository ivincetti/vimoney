package ru.vincetti.vimoney.ui.history

import androidx.lifecycle.*
import ru.vincetti.vimoney.data.models.TransactionListModel
import ru.vincetti.vimoney.data.repository.TransactionRepo
import ru.vincetti.vimoney.data.sqlite.AppDatabase
import ru.vincetti.vimoney.ui.history.filter.Filter

class HistoryViewModel(
    db: AppDatabase,
    filter: Filter
) : ViewModel() {

    private val repo: TransactionRepo = TransactionRepo(db)

    private var _filter = MutableLiveData<Filter>().apply { value = filter }
    val filter: LiveData<Filter>
        get() = _filter

    private var _transList = _filter.switchMap {
        repo.loadFilterTransactions(it)
    }
    val transList: LiveData<List<TransactionListModel>>
        get() = _transList

    fun filter(newFilter: Filter) {
        _filter.value = newFilter
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
