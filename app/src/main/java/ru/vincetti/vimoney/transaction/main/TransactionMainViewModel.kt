package ru.vincetti.vimoney.transaction.main

import android.app.Application
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import ru.vincetti.vimoney.data.models.TransactionModel
import ru.vincetti.vimoney.data.sqlite.AccountDao
import ru.vincetti.vimoney.data.sqlite.TransactionDao
import ru.vincetti.vimoney.ui.check.add.AddCheckViewModel
import ru.vincetti.vimoney.ui.dashboard.DashboardViewModel
import ru.vincetti.vimoney.utils.LogicMath

class TransactionMainViewModel(
        private val transactionDao: TransactionDao,
        private val accDao: AccountDao,
        app: Application
) : AndroidViewModel(app) {

    private var _transaction = MutableLiveData<TransactionModel>()
    val transaction
        get() = _transaction

    private var accNestedId = TransactionModel.DEFAULT_ID
    private var mAccID = TransactionModel.DEFAULT_ID
    private val mTransId = TransactionModel.DEFAULT_ID

    init {
        _transaction.value = TransactionModel()
    }

    fun loadTransaction(id: Int) {
        viewModelScope.launch {
            val transLD = transactionDao.loadTransactionById(id)
            transLD?.let {
                _transaction.value = it
                if (it.extraKey == TransactionModel.TRANSACTION_TYPE_TRANSFER_KEY) {
                    accNestedId = Integer.valueOf(it.extraValue)
                }
            }
        }
    }

    fun setAccount(id: Int) {
        _transaction.value?.accountId = id
    }

    fun delete() {
        viewModelScope.launch {
            _transaction.value?.let {
                if ((_transaction.value as TransactionModel).id != TransactionModel.DEFAULT_ID) {
                    if (it.extraKey == TransactionModel.TRANSACTION_TYPE_TRANSFER_KEY
                            && accNestedId > 0) {
                        // update balance for nested transfer account
                        val nestedAccId = transactionDao.getAccountTransactionById(accNestedId)
                        LogicMath.accountBalanceUpdateById(transactionDao, accDao, nestedAccId)
                        // delete nested
                        transactionDao.deleteTransactionById(accNestedId)
                    }
                    transactionDao.deleteTransactionById(mTransId)
                    // update balance for current (accId) account
                    LogicMath.accountBalanceUpdateById(transactionDao, accDao, mAccID)
                }
            }
        }
    }
}

class TransactionMainViewModelFactory(
        val transactionDao: TransactionDao,
        val accDao: AccountDao,
        val app: Application
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TransactionMainViewModel::class.java)) {
            return TransactionMainViewModel(transactionDao, accDao, app) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}