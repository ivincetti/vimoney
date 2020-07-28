package ru.vincetti.vimoney.ui.home

import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.vincetti.vimoney.data.models.AccountListModel
import ru.vincetti.vimoney.data.sqlite.AccountDao
import ru.vincetti.vimoney.data.sqlite.TransactionDao
import ru.vincetti.vimoney.utils.accountBalanceUpdateAll
import ru.vincetti.vimoney.utils.userBalanceUpdate
import java.text.SimpleDateFormat
import java.util.*

class HomeViewModel(
        private val accDao: AccountDao,
        private val trDao: TransactionDao
) : ViewModel() {

    val expenseSum = MutableLiveData<Int>()
    val incomeSum = MutableLiveData<Int>()
    var date: String = SimpleDateFormat("MM").format(Date())
    var year: String = SimpleDateFormat("yyyy").format(Date())

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
            incomeSum.value = trDao.loadSumTransactionIncomeMonth(date, year)
            expenseSum.value = trDao.loadSumTransactionExpenseMonth(date, year)
            getNotArchiveAccountsFull()
            updateBalance()
        }
    }

    fun updateAllAccounts() {
        _homeButtonEnabled.value = false
        viewModelScope.launch {
            accountBalanceUpdateAll(trDao, accDao)
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
        private val accDao: AccountDao,
        private val trDao: TransactionDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(accDao, trDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
