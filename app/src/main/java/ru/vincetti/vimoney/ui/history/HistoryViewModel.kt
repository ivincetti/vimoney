package ru.vincetti.vimoney.ui.history

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.switchMap
import androidx.paging.toLiveData
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import ru.vincetti.vimoney.data.repository.TransactionRepo
import ru.vincetti.vimoney.ui.history.filter.Filter

class HistoryViewModel @AssistedInject constructor(
    private val repo: TransactionRepo,
    @Assisted initFilter: Filter
) : ViewModel() {

    private var filter = MutableLiveData(initFilter)

    val transList = filter.switchMap {
        repo.loadFilterTransactions(it).toLiveData(pageSize = 20)
    }

    fun filter(newFilter: Filter) {
        filter.value = newFilter
    }

    @dagger.assisted.AssistedFactory
    interface AssistedFactory {
        fun create(initFilter: Filter): HistoryViewModel
    }

    companion object {

        fun provideFactory(
            assistedFactory: AssistedFactory,
            initFilter: Filter
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return assistedFactory.create(initFilter) as T
            }
        }
    }
}
