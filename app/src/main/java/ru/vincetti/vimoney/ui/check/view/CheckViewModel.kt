package ru.vincetti.vimoney.ui.check.view

import androidx.lifecycle.*
import kotlinx.coroutines.launch
import ru.vincetti.vimoney.data.models.AccountListModel
import ru.vincetti.vimoney.data.sqlite.AccountDao
import ru.vincetti.vimoney.data.sqlite.TransactionDao
import ru.vincetti.vimoney.ui.check.DEFAULT_CHECK_ID
import ru.vincetti.vimoney.utils.accountBalanceUpdateById

class CheckViewModel(
        private val transactionDao: TransactionDao,
        private val accountDao: AccountDao,
        private val accountId: Int
) : ViewModel() {

    companion object {
        const val DEFAULT_CHECK_COUNT = 20
    }

    val model: LiveData<AccountListModel> = accountDao.loadAccountByIdFull(accountId)

    private var _updateButtonEnable = MutableLiveData<Boolean>()
    val updateButtonEnable
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
            accountBalanceUpdateById(transactionDao, accountDao, accountId)
            _updateButtonEnable.value = true
        }
    }
}

class CheckViewModelFactory(
        private val transactionDao: TransactionDao,
        private val accountDao: AccountDao,
        private val id: Int
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CheckViewModel::class.java)) {
            return CheckViewModel(transactionDao, accountDao, id) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}