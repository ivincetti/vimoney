package ru.vincetti.vimoney.ui.history

import androidx.lifecycle.*
import androidx.paging.PagedList
import androidx.paging.toLiveData
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import ru.vincetti.modules.core.models.Filter
import ru.vincetti.modules.core.utils.SingleLiveEvent
import ru.vincetti.modules.database.sqlite.models.TransactionListModel
import ru.vincetti.vimoney.models.HistoryModel

class HistoryViewModel @AssistedInject constructor(
    private val historyModel: HistoryModel,
    @Assisted initFilter: Filter
) : ViewModel() {

    private var filter = MutableLiveData(initFilter)

    private val _needNavigate2Transaction = SingleLiveEvent<Int>()
    val needNavigate2Transaction: LiveData<Int>
        get() = _needNavigate2Transaction

    val transactions: LiveData<PagedList<TransactionListModel>> = filter.switchMap {
        historyModel.loadFilterTransactions(it).toLiveData(pageSize = 20)
    }

    fun filter(newFilter: Filter) {
        filter.value = newFilter
    }

    fun clickOnElement(id: Int) {
        _needNavigate2Transaction.value = id
    }

    @AssistedFactory
    interface HistoryViewModelFactory {
        fun create(initFilter: Filter): HistoryViewModel
    }

    companion object {

        fun provideFactory(
            assistedFactory: HistoryViewModelFactory,
            initFilter: Filter
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {

            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create(initFilter) as T
            }
        }
    }
}
