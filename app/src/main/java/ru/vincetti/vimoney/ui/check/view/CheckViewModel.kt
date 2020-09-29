package ru.vincetti.vimoney.ui.check.view

import androidx.lifecycle.*
import kotlinx.coroutines.launch
import ru.vincetti.vimoney.data.models.AccountListModel
import ru.vincetti.vimoney.data.sqlite.AppDatabase
import ru.vincetti.vimoney.ui.check.DEFAULT_CHECK_ID
import ru.vincetti.vimoney.utils.accountBalanceUpdateById

class CheckViewModel(
    private val database: AppDatabase,
    private val accountId: Int
) : ViewModel() {

    private val accountDao = database.accountDao()

    val account: LiveData<AccountListModel> = accountDao.loadAccountByIdFull(accountId)

    val isArchive: LiveData<Boolean> = account.map {
        it.isArchive
    }

    val isNeedOnMain: LiveData<Boolean> = account.map {
        it.needOnMain
    }

    private var _updateButtonEnable = MutableLiveData<Boolean>()
    val updateButtonEnable: LiveData<Boolean>
        get() = _updateButtonEnable

    init {
        _updateButtonEnable.value = true
    }

    fun restore() {
        if (accountId != DEFAULT_CHECK_ID) {
            viewModelScope.launch {
                accountDao.fromArchiveAccountById(accountId)
            }
        }
    }

    fun delete() {
        if (accountId != DEFAULT_CHECK_ID) {
            viewModelScope.launch {
                accountDao.archiveAccountById(accountId)
            }
        }
    }

    fun update() {
        _updateButtonEnable.value = false
        viewModelScope.launch {
            accountBalanceUpdateById(database, accountId)
            _updateButtonEnable.value = true
        }
    }
}

class CheckViewModelFactory(
    private val database: AppDatabase,
    private val id: Int
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CheckViewModel::class.java)) {
            return CheckViewModel(database, id) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
