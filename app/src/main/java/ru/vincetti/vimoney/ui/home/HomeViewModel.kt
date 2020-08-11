package ru.vincetti.vimoney.ui.home

import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.vincetti.vimoney.data.models.AccountListModel
import ru.vincetti.vimoney.data.repository.TransactionRepo
import ru.vincetti.vimoney.data.sqlite.AccountDao
import ru.vincetti.vimoney.data.sqlite.AppDatabase
import ru.vincetti.vimoney.utils.accountBalanceUpdateAll
import ru.vincetti.vimoney.utils.userBalanceUpdate

class HomeViewModel(private val db: AppDatabase) : ViewModel() {

    private val accDao: AccountDao = db.accountDao()
    private val transRepo: TransactionRepo = TransactionRepo(db)

    val expenseSum = MutableLiveData<Int>()
    val incomeSum = MutableLiveData<Int>()

    private var _homeButtonEnabled = MutableLiveData<Boolean>()
    val homeButtonEnabled: LiveData<Boolean>
        get() = _homeButtonEnabled

    private var _userBalance = MutableLiveData<Int>()
    val userBalance: LiveData<Int>
        get() = _userBalance

    private var _accounts = MutableLiveData<List<AccountListModel>>()
    val accounts: LiveData<List<AccountListModel>>
        get() = _accounts

    init {
        _homeButtonEnabled.value = true
        _userBalance.value = 0
        viewModelScope.launch {
            val incomeExpense = transRepo.loadIncomeExpenseActual()
            incomeSum.value = incomeExpense.first
            expenseSum.value = incomeExpense.second

            getNotArchiveAccountsFull()
            updateBalance()
        }
    }

    fun updateAllAccounts() {
        _homeButtonEnabled.value = false
        viewModelScope.launch {
            accountBalanceUpdateAll(db)
            _userBalance.value = userBalanceUpdate(accDao)
            _homeButtonEnabled.value = true
        }
    }

    private suspend fun getNotArchiveAccountsFull() {
        withContext(Dispatchers.IO) {
            _accounts.postValue(accDao.loadNotArchiveAccountsFull())
        }
    }

    private suspend fun updateBalance() {
        withContext(Dispatchers.IO) {
            val newBalance = userBalanceUpdate(accDao)
            _userBalance.postValue(newBalance)
        }
    }
}

class HomeViewModelFactory(
        private val db: AppDatabase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(db) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
