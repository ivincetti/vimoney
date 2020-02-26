package ru.vincetti.vimoney.ui.transaction.main

import androidx.lifecycle.*
import kotlinx.coroutines.launch
import ru.vincetti.vimoney.data.models.TransactionModel
import ru.vincetti.vimoney.data.sqlite.AccountDao
import ru.vincetti.vimoney.data.sqlite.TransactionDao
import ru.vincetti.vimoney.utils.accountBalanceUpdateById
import java.util.*

class TransactionMainViewModel(
        private val transactionDao: TransactionDao,
        private val accDao: AccountDao
) : ViewModel() {

    private var _transaction = MutableLiveData<TransactionModel>()
    val transaction
        get() = _transaction

    private val _nestedTransaction = MutableLiveData<TransactionModel>()
    val nestedTransaction
        get() = _nestedTransaction

    private val _needAccount = MutableLiveData<Boolean>()
    val needAccount
        get() = _needAccount

    private val _needSum = MutableLiveData<Boolean>()
    val needSum
        get() = _needSum

    private val _needToUpdate = MutableLiveData<Boolean>()
    val needToUpdate
        get() = _needToUpdate

    private val _needToNavigate = MutableLiveData<Boolean>()
    val needToNavigate
        get() = _needToNavigate

    private val _error = MutableLiveData<Boolean>()
    val error
        get() = _error

    val date = Transformations.map(_transaction) {
        it.date
    }

    var saveAction = TransactionModel.TRANSACTION_TYPE_SPENT
    private var oldTransactionAccountID = TransactionModel.DEFAULT_ID
    private var oldTransactionAccountToID = TransactionModel.DEFAULT_ID

    private var accNestedId = TransactionModel.DEFAULT_ID
    private var mAccID = TransactionModel.DEFAULT_ID
    private val mTransId = TransactionModel.DEFAULT_ID

    init {
        _transaction.value = TransactionModel()
        _nestedTransaction.value = TransactionModel()
        _needAccount.value = false
        _needToUpdate.value = false
        _needSum.value = false
        _error.value = false
        _needToNavigate.value = false
    }

    fun loadTransaction(id: Int) {
        viewModelScope.launch {
            val tmpTransaction = transactionDao.loadTransactionById(id)
            tmpTransaction?.let {
                _transaction.value = tmpTransaction
                if (tmpTransaction.id != TransactionModel.DEFAULT_ID) {
                    _needToUpdate.value = true
                    oldTransactionAccountID = tmpTransaction.id
                }
                if (tmpTransaction.extraKey == TransactionModel.TRANSACTION_TYPE_TRANSFER_KEY
                        && tmpTransaction.extraValue.toInt() > 0) {
                    _nestedTransaction.value = loadNestedTransaction(tmpTransaction.extraValue.toInt())
                    oldTransactionAccountToID =
                            _nestedTransaction.value?.accountId ?: TransactionModel.DEFAULT_ID
                }
            }
        }
    }

    private suspend fun loadNestedTransaction(id: Int) = transactionDao.loadTransactionById(id)

    fun setAccount(id: Int) {
        _transaction.value?.accountId = id
    }

    fun changeSumAdd(newSum: CharSequence?) {
        newSum?.let { char ->
            _transaction.value?.let {
                if (char.toString().toFloat() != it.sum) {
                    it.sum = char.toString().toFloat()
                    _nestedTransaction.value?.sum = char.toString().toFloat()
                    // TODO странный метод, но работает - надо вынести суммы строк от цифр
                    _transaction.value = it
                    _nestedTransaction.value = _nestedTransaction.value
                }
            }
        }
    }

    fun setDate(date: Date) {
        _transaction.value?.date = date
    }

    fun setAccountTo(id: Int) {
        _nestedTransaction.value?.accountId = id
    }

    fun delete() {
        viewModelScope.launch {
            _transaction.value?.let {
                if ((_transaction.value as TransactionModel).id != TransactionModel.DEFAULT_ID) {
                    if (it.extraKey == TransactionModel.TRANSACTION_TYPE_TRANSFER_KEY
                            && accNestedId > 0) {
                        // update balance for nested transfer account
                        val nestedAccId = transactionDao.getAccountTransactionById(accNestedId)
                        accountBalanceUpdateById(transactionDao, accDao, nestedAccId)
                        // delete nested
                        transactionDao.deleteTransactionById(accNestedId)
                    }
                    transactionDao.deleteTransactionById(mTransId)
                    // update balance for current (accId) account
                    accountBalanceUpdateById(transactionDao, accDao, mAccID)
                }
            }
        }
    }

    fun saveTransaction(txtName: String, txtSum: String, txtSumTo: String) {
        val tmpTransaction = _transaction.value
        val tmpToTransaction = _nestedTransaction.value
        if (tmpTransaction == null || tmpToTransaction == null) {
            _error.value = true
        } else {
            if (tmpTransaction.accountId != TransactionModel.DEFAULT_ID
                    && tmpToTransaction.accountId != TransactionModel.DEFAULT_ID) {
                if (txtSumTo == "" || txtSum == "") {
                    _needSum.value = true
                } else {
                    tmpTransaction.description = txtName
                    tmpTransaction.date = date.value!!
                    tmpTransaction.type = saveAction
                    tmpTransaction.sum = txtSum.toFloat()
                    tmpTransaction.extraKey = TransactionModel.TRANSACTION_TYPE_TRANSFER_KEY

                    tmpToTransaction.sum = txtSumTo.toFloat()
                    tmpToTransaction.accountId = tmpTransaction.accountId
                    tmpToTransaction.extraKey = TransactionModel.TRANSACTION_TYPE_TRANSFER_KEY
                    tmpToTransaction.extraValue = tmpTransaction.id.toString()
                    tmpToTransaction.date = date.value!!
                    tmpToTransaction.type = TransactionModel.TRANSACTION_TYPE_INCOME
                    tmpToTransaction.system = true

                    if (tmpTransaction.id != TransactionModel.DEFAULT_ID) {
                        trUpdate(tmpTransaction, tmpToTransaction)
                    } else trInsert(tmpTransaction, tmpToTransaction)
                    updateBalance(tmpTransaction, tmpToTransaction)
                }
            } else {
                _needAccount.value = true
            }
        }
    }

    fun saveTransaction(txtName: String, txtSum: String) {
        val tmpTransaction = _transaction.value
        tmpTransaction?.let {
            when {
                txtSum == "" -> _needSum.value = true
                it.accountId < 1 -> _needAccount.value = true
                else -> {
                    tmpTransaction.description = txtName
                    tmpTransaction.date = date.value!!
                    tmpTransaction.type = saveAction
                    tmpTransaction.sum = txtSum.toFloat()
                    if (tmpTransaction.id != TransactionModel.DEFAULT_ID) {
                        trUpdate(tmpTransaction)
                    } else trInsert(tmpTransaction)
                    updateBalance(tmpTransaction)
                }
            }
        }
    }

    private fun updateBalance(vararg trans: TransactionModel) {
        viewModelScope.launch {
            for (transaction in trans) {
                // update balance for current (accId) account
                accountBalanceUpdateById(transactionDao, accDao, transaction.accountId)
            }
            // update balance for account updated
            // TODO бред надо автоматизировать
//            if (oldTransactionAccountID != TransactionModel.DEFAULT_ID && tmpTransaction.accountId != oldTransactionAccountID) {
//                LogicMath.accountBalanceUpdateById(transactionDao, accountDao, oldTransactionAccountID)
//            }
//            if (oldTransactionAccountToID != TransactionModel.DEFAULT_ID && tmpToTransaction.accountId != oldTransactionAccountToID) {
//                LogicMath.accountBalanceUpdateById(transactionDao, accountDao, oldTransactionAccountToID)
//            }
        }
    }

    private fun trUpdate(transaction: TransactionModel, toTransaction: TransactionModel? = null) {
        viewModelScope.launch {
            toTransaction?.let {
                it.id = transaction.extraValue.toInt()
                transactionDao.updateTransaction(it)
            }
            transactionDao.updateTransaction(transaction)
        }
        _needToNavigate.value = true
    }

    private fun trInsert(transaction: TransactionModel, toTransaction: TransactionModel? = null) {
        viewModelScope.launch {
            toTransaction?.let {
                val idTo = transactionDao.insertTransaction(it)
                transaction.extraValue = idTo.toString()
            }
            transactionDao.insertTransaction(transaction)
        }
        _needToNavigate.value = true
    }
}

class TransactionMainViewModelFactory(
        val transactionDao: TransactionDao,
        private val accDao: AccountDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TransactionMainViewModel::class.java)) {
            return TransactionMainViewModel(transactionDao, accDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}