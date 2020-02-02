package ru.vincetti.vimoney.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.vincetti.vimoney.data.models.AccountListModel
import ru.vincetti.vimoney.data.sqlite.AccountDao
import ru.vincetti.vimoney.data.sqlite.TransactionDao
import java.text.SimpleDateFormat
import java.util.*

class HomeViewModel(val accDao: AccountDao, trDao: TransactionDao, app: Application) : AndroidViewModel(app) {
    val accounts: LiveData<List<AccountListModel>> = accDao.loadNotArchiveAccountsFull()
    var date: String = SimpleDateFormat("MM").format(Date())

    val lSum1: LiveData<Int> = trDao.loadSumTransactionIncomeMonth(date, "2019")
    val lSum2: LiveData<Int> = trDao.loadSumTransactionExpenseMonth(date, "2019")
}

class HomeViewModelFactory(
        private val accDao: AccountDao,
        private val trDao: TransactionDao,
        private val app: Application
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(accDao, trDao, app) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
