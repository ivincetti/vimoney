package ru.vincetti.vimoney.ui.history

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.switchMap
import androidx.paging.toLiveData
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import ru.vincetti.modules.database.repository.TransactionRepo
import ru.vincetti.modules.core.models.Filter
import ru.vincetti.modules.core.utils.SingleLiveEvent

class HistoryViewModel @AssistedInject constructor(
    private val repo: TransactionRepo,
    @Assisted initFilter: Filter
) : ViewModel() {

    private var filter = MutableLiveData(initFilter)

    val needNavigate2Transaction = SingleLiveEvent<Int>()

    val transList = filter.switchMap {
        repo.loadFilterTransactions(it).toLiveData(pageSize = 20)
    }

    fun filter(newFilter: Filter) {
        filter.value = newFilter
    }

    fun clickOnElement(id: Int) {
        needNavigate2Transaction.value = id
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
