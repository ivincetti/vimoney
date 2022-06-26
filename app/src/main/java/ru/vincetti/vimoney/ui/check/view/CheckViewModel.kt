package ru.vincetti.vimoney.ui.check.view

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.vincetti.modules.core.models.AccountList
import ru.vincetti.modules.core.utils.SingleLiveEvent
import ru.vincetti.modules.database.repository.AccountRepo
import ru.vincetti.vimoney.ui.check.AccountConst
import javax.inject.Inject

@HiltViewModel
class CheckViewModel @Inject constructor(
    state: SavedStateHandle,
    private val accountRepo: AccountRepo,
) : ViewModel() {

    private val accountId = requireNotNull(state.get<Int>("checkID"))

    val account: LiveData<AccountList> = accountRepo.loadForListById(accountId)

    val isArchive: LiveData<Boolean> = account.map { it.isArchive }
    val isNeedOnMain: LiveData<Boolean> = account.map { it.needOnMain }

    private var _updateButtonEnable = MutableLiveData<Boolean>()
    val updateButtonEnable: LiveData<Boolean>
        get() = _updateButtonEnable

    private val _navigate2Edit = SingleLiveEvent<Int>()
    val navigate2Edit: LiveData<Int>
        get() = _navigate2Edit

    private val _navigate2Add = SingleLiveEvent<Int>()
    val navigate2Add: LiveData<Int>
        get() = _navigate2Add

    init {
        _updateButtonEnable.value = true
    }

    fun restore() {
        if (accountId != AccountConst.DEFAULT_CHECK_ID) {
            viewModelScope.launch {
                accountRepo.unArchiveById(accountId)
            }
        }
    }

    fun delete() {
        if (accountId != AccountConst.DEFAULT_CHECK_ID) {
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

    fun onEditClicked() {
        _navigate2Edit.value = accountId
    }

    fun onAddClicked() {
        _navigate2Add.value = accountId
    }
}
