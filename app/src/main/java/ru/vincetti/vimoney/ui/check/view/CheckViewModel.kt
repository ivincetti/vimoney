package ru.vincetti.vimoney.ui.check.view

import androidx.lifecycle.*
import kotlinx.coroutines.launch
import ru.vincetti.vimoney.data.models.AccountListModel
import ru.vincetti.vimoney.data.repository.AccountRepo
import ru.vincetti.vimoney.ui.check.DEFAULT_CHECK_ID

class CheckViewModel(
    private val accountRepo: AccountRepo,
    private val accountId: Int
) : ViewModel() {

    val account: LiveData<AccountListModel> = accountRepo.loadForListById(accountId)

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
                accountRepo.unArchiveById(accountId)
            }
        }
    }

    fun delete() {
        if (accountId != DEFAULT_CHECK_ID) {
            viewModelScope.launch {
                accountRepo.archiveById(accountId)
            }
        }
    }

    fun update() {
        _updateButtonEnable.value = false
        viewModelScope.launch {
            accountRepo.balanceUpdateById(accountId)
            _updateButtonEnable.value = true
        }
    }
}

class CheckViewModelFactory(
    private val accountRepo: AccountRepo,
    private val id: Int
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CheckViewModel::class.java)) {
            return CheckViewModel(accountRepo, id) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
