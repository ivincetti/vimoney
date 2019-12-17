package ru.vincetti.vimoney.ui.check.view

import androidx.lifecycle.*
import kotlinx.coroutines.launch
import ru.vincetti.vimoney.data.models.AccountListModel
import ru.vincetti.vimoney.data.sqlite.AccountDao
import ru.vincetti.vimoney.transaction.TransactionViewModel
import ru.vincetti.vimoney.ui.dashboard.DashboardViewModel
import ru.vincetti.vimoney.utils.TransactionViewModelUtils
import ru.vincetti.vimoney.utils.UpdateViewModel

class CheckViewModel(private val dao: AccountDao, private val checkID: Int) : UpdateViewModel() {

    companion object {
        const val EXTRA_CHECK_ID = "checkID"
        const val DEFAULT_CHECK_ID = -1
        const val DEFAULT_CHECK_COUNT = 20
    }

    val model: LiveData<AccountListModel> = dao.loadAccountByIdFull(checkID)

    private var _need2UpdateViewModel = MutableLiveData<Boolean>()
    val need2UpdateViewModel
        get() = _need2UpdateViewModel

    init {
        _need2UpdateViewModel.value = false
    }

    // restore from archive account logic
    fun restore() {
        if (checkID != DEFAULT_CHECK_ID) {
            viewModelScope.launch {
                dao.fromArchiveAccountById(checkID)
                _need2UpdateViewModel.value = true
            }
        }
    }

    // archive account logic
    fun delete() {
        if (checkID != DEFAULT_CHECK_ID) {
            // delete query
            viewModelScope.launch {
                dao.archiveAccountById(checkID)
                _need2UpdateViewModel.value = true
            }
        }
    }

    override fun updateTransactionsViewModel(t: TransactionViewModel)
    {
        viewModelScope.launch {
            TransactionViewModelUtils.viewModelUpdate(dao, t)
        }
    }
}


class CheckViewModelFactory(private val dao: AccountDao, private val id: Int) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CheckViewModel::class.java)) {
            return CheckViewModel(dao, id) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}