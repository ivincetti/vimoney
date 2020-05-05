package ru.vincetti.vimoney.ui.home

import androidx.lifecycle.*
import kotlinx.coroutines.launch
import ru.vincetti.vimoney.data.models.AccountListModel
import ru.vincetti.vimoney.data.sqlite.AccountDao
import ru.vincetti.vimoney.data.sqlite.TransactionDao
import java.text.SimpleDateFormat
import java.util.*

class HomeViewModel(accDao: AccountDao, trDao: TransactionDao) : ViewModel() {
    val accounts: LiveData<List<AccountListModel>> = accDao.loadNotArchiveAccountsFull()
    var date: String = SimpleDateFormat("MM").format(Date())
    var year: String = SimpleDateFormat("yyyy").format(Date())

    val incomeSum = MutableLiveData<Int>()
    val expenseSum = MutableLiveData<Int>()

    init {
        viewModelScope.launch {
            incomeSum.value = trDao.loadSumTransactionIncomeMonth(date, year)
            expenseSum.value = trDao.loadSumTransactionExpenseMonth(date, year)
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
