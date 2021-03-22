package ru.vincetti.vimoney.ui.check.view

import androidx.lifecycle.*
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.launch
import ru.vincetti.modules.core.models.AccountList
import ru.vincetti.vimoney.models.CheckModel
import ru.vincetti.vimoney.ui.check.DEFAULT_CHECK_ID

class CheckViewModel @AssistedInject constructor(
    private val model: CheckModel,
    @Assisted private val accountId: Int
) : ViewModel() {

    val account: LiveData<AccountList?> = model.loadLiveAccountById(accountId)

    val isArchive: LiveData<Boolean> = account.map { it?.isArchive ?: false }

    val isNeedOnMain: LiveData<Boolean> = account.map { it?.needOnMain ?: false }

    private var _updateButtonEnable = MutableLiveData<Boolean>()
    val updateButtonEnable: LiveData<Boolean>
        get() = _updateButtonEnable

    fun restore() {
        if (accountId != DEFAULT_CHECK_ID) {
            viewModelScope.launch {
                model.unArchiveAccountById(accountId)
            }
        }
    }

    fun delete() {
        if (accountId != DEFAULT_CHECK_ID) {
            viewModelScope.launch {
                model.archiveAccountById(accountId)
            }
        }
    }

    fun update() {
        _updateButtonEnable.value = false
        viewModelScope.launch {
            model.accountBalanceUpdateById(accountId)
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
