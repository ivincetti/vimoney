package ru.vincetti.vimoney.ui.check.view

import androidx.lifecycle.*
import kotlinx.coroutines.launch
import ru.vincetti.vimoney.data.models.AccountListModel
import ru.vincetti.vimoney.data.sqlite.AccountDao
import ru.vincetti.vimoney.ui.check.DEFAULT_CHECK_ID

class CheckViewModel(private val dao: AccountDao, private val checkID: Int) : ViewModel() {

    companion object {
        const val DEFAULT_CHECK_COUNT = 20
    }

    val model: LiveData<AccountListModel> = dao.loadAccountByIdFull(checkID)

    private var _need2UpdateViewModel = MutableLiveData<Boolean>()
    val need2UpdateViewModel
        get() = _need2UpdateViewModel

    init {
        _need2UpdateViewModel.value = false
    }

    /** Restore from archive account logic. */
    fun restore() {
        if (checkID != DEFAULT_CHECK_ID) {
            viewModelScope.launch {
                dao.fromArchiveAccountById(checkID)
                _need2UpdateViewModel.value = true
            }
        }
    }

    /** Archive account logic. */
    fun delete() {
        if (checkID != DEFAULT_CHECK_ID) {
            // delete query
            viewModelScope.launch {
                dao.archiveAccountById(checkID)
                _need2UpdateViewModel.value = true
            }
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