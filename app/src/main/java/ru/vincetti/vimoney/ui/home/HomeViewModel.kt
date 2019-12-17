package ru.vincetti.vimoney.ui.home

import android.app.Application
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import ru.vincetti.vimoney.data.models.AccountListModel
import ru.vincetti.vimoney.data.sqlite.AccountDao
import ru.vincetti.vimoney.data.sqlite.TransactionDao
import ru.vincetti.vimoney.transaction.TransactionViewModel
import ru.vincetti.vimoney.utils.TransactionViewModelUtils.Companion.viewModelUpdate
import java.text.SimpleDateFormat
import java.util.*

class HomeViewModel(val accDao: AccountDao, trDao: TransactionDao, app: Application) : AndroidViewModel(app) {
    val accounts: LiveData<List<AccountListModel>> = accDao.loadNotArhiveAccountsFull()
    var date: String = SimpleDateFormat("MM").format(Date())

    val lSum1: LiveData<Int> = trDao.loadSumTransactionIncomeMonth(date, "2019")
    val lSum2: LiveData<Int> = trDao.loadSumTransactionExpenseMonth(date, "2019")

    fun updateTransactionsViewModel(transactionViewModel: TransactionViewModel)
    {
        viewModelScope.launch {
            viewModelUpdate(accDao, transactionViewModel)
        }
    }
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
