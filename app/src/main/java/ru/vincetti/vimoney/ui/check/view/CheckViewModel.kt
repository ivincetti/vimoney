package ru.vincetti.vimoney.ui.check.view

import androidx.lifecycle.*
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.launch
import ru.vincetti.modules.core.models.AccountList
import ru.vincetti.modules.database.repository.AccountRepo
import ru.vincetti.vimoney.ui.check.DEFAULT_CHECK_ID

class CheckViewModel @AssistedInject constructor(
    private val accountRepo: AccountRepo,
    @Assisted private val accountId: Int
) : ViewModel() {

    val account: LiveData<AccountList> = accountRepo.loadForListById(accountId)

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

    @dagger.assisted.AssistedFactory
    interface AssistedFactory {
        fun create(accountId: Int): CheckViewModel
    }

    companion object {

        fun provideFactory(
            assistedFactory: AssistedFactory,
            accountId: Int
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return assistedFactory.create(accountId) as T
            }
        }
    }
}
