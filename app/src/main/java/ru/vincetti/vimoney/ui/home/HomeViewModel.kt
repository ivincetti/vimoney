package ru.vincetti.vimoney.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.vincetti.vimoney.data.models.AccountListModel
import ru.vincetti.vimoney.data.sqlite.AccountDao
import ru.vincetti.vimoney.data.sqlite.TransactionDao
import java.text.SimpleDateFormat
import java.util.*

class HomeViewModel(accDao: AccountDao, trDao: TransactionDao) : ViewModel() {
    val accounts: LiveData<List<AccountListModel>> = accDao.loadNotArchiveAccountsFull()
    var date: String = SimpleDateFormat("MM").format(Date())
    var year: String = SimpleDateFormat("YYYY").format(Date())

    val incomeSum: LiveData<Int> = trDao.loadSumTransactionIncomeMonth(date, year)
    val expenseSum: LiveData<Int> = trDao.loadSumTransactionExpenseMonth(date, year)
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
