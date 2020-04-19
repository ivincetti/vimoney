package ru.vincetti.vimoney.ui.transaction.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
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

    private val _date = MutableLiveData<Date>()
    val date
        get() = _date

    private val _accountId = MutableLiveData<Int>()
    val accountId
        get() = _accountId

    private val _accountIdTo = MutableLiveData<Int>()
    val accountIdTo
        get() = _accountIdTo

    private val _sum = MutableLiveData<Float>()
    val sum
        get() = _sum

    private val _description = MutableLiveData<String>()
    val description
        get() = _description

    private var oldTransactionAccountID = TransactionModel.DEFAULT_ID
    private var oldTransactionAccountToID = TransactionModel.DEFAULT_ID

    private var nestedId = TransactionModel.DEFAULT_ID
    private var mTransId = TransactionModel.DEFAULT_ID

    init {
        _transaction.value = TransactionModel()
        _nestedTransaction.value = TransactionModel()
        _accountId.value = TransactionModel.DEFAULT_ID
        _accountIdTo.value = TransactionModel.DEFAULT_ID
        _needAccount.value = false
        _needToUpdate.value = false
        _needSum.value = false
        _error.value = false
        _needToNavigate.value = false
        _date.value = Date()
    }

    fun loadTransaction(id: Int) {
        viewModelScope.launch {
            val tmpTransaction = transactionDao.loadTransactionById(id)
            tmpTransaction?.let {
                _transaction.value = tmpTransaction
                if (tmpTransaction.id != TransactionModel.DEFAULT_ID) {
                    _needToUpdate.value = true
                    _accountId.value = tmpTransaction.accountId
                    _sum.value = tmpTransaction.sum
                    _description.value = tmpTransaction.description
                    _date.value = tmpTransaction.date
                    mTransId = tmpTransaction.id
                    oldTransactionAccountID = tmpTransaction.id
                }
                if (tmpTransaction.extraKey == TransactionModel.TRANSACTION_TYPE_TRANSFER_KEY
                        && tmpTransaction.extraValue.toInt() > 0) {
                    nestedId = tmpTransaction.extraValue.toInt()
                    _nestedTransaction.value = loadNestedTransaction(nestedId)
                    oldTransactionAccountToID =
                            _nestedTransaction.value?.accountId ?: TransactionModel.DEFAULT_ID
                }
            }
        }
    }

    private suspend fun loadNestedTransaction(id: Int) = transactionDao.loadTransactionById(id)

    fun setAccount(id: Int) {
        _accountId.value = id
    }

    fun setAccountTo(id: Int) {
        _accountIdTo.value = id
    }

    fun setSum(newSum: String) {
        if (newSum.isNotEmpty()) {
            val sumF = newSum.toFloat()
            if (sumF > 0) {
                _sum.value = sumF
                _nestedTransaction.value?.sum = sumF
            }
        }
    }

    fun setDescription(newDescription: String) {
        _description.value = newDescription
    }

    fun setDate(newDate: Date) {
        _date.value = newDate
    }

    fun delete() {
        viewModelScope.launch {
            _transaction.value?.let {
                if (it.id != TransactionModel.DEFAULT_ID) {
                    if (it.extraKey == TransactionModel.TRANSACTION_TYPE_TRANSFER_KEY
                            && nestedId > 0) {
                        // delete nested
                        transactionDao.deleteTransactionById(nestedId)
                        accountBalanceUpdateById(transactionDao, accDao, _accountIdTo.value!!)
                    }
                    transactionDao.deleteTransactionById(mTransId)
                    accountBalanceUpdateById(transactionDao, accDao, _accountId.value!!)
                    _needToNavigate.value = true
                }
            }
        }
    }

    fun saveTransaction(actionType: Int, txtName: String, txtSum: String) {
        val tmpTransaction = _transaction.value
        tmpTransaction?.let {
            when {
                txtSum == "" -> _needSum.value = true
                accountId.value!! < 1 -> _needAccount.value = true
                else -> {
                    tmpTransaction.description = txtName
                    tmpTransaction.date = date.value!!
                    tmpTransaction.type = actionType
                    tmpTransaction.accountId = _accountId.value!!
                    tmpTransaction.sum = txtSum.toFloat()
                    if (tmpTransaction.id != TransactionModel.DEFAULT_ID) {
                        trUpdate(tmpTransaction)
                    } else {
                        trInsert(tmpTransaction)
                    }
                    updateBalance(tmpTransaction)
                }
            }
        }
    }

    fun saveTransactionTo(actionType: Int, txtName: String, txtSum: String, txtSumTo: String) {
        val tmpTransaction = _transaction.value
        val tmpToTransaction = _nestedTransaction.value
        if (tmpTransaction == null || tmpToTransaction == null) {
            _error.value = true
        } else {
            if (_accountId.value != TransactionModel.DEFAULT_ID
                    && _accountIdTo.value != TransactionModel.DEFAULT_ID) {
                if (txtSumTo == "" || txtSum == "") {
                    _needSum.value = true
                } else {
                    tmpTransaction.description = txtName
                    tmpTransaction.date = date.value!!
                    tmpTransaction.accountId = _accountId.value!!
                    tmpTransaction.type = actionType
                    tmpTransaction.sum = txtSum.toFloat()
                    tmpTransaction.extraKey = TransactionModel.TRANSACTION_TYPE_TRANSFER_KEY

                    tmpToTransaction.sum = txtSumTo.toFloat()
                    tmpToTransaction.accountId = _accountIdTo.value!!
                    tmpToTransaction.extraKey = TransactionModel.TRANSACTION_TYPE_TRANSFER_KEY
                    tmpToTransaction.extraValue = tmpTransaction.id.toString()
                    tmpToTransaction.date = date.value!!
                    tmpToTransaction.type = TransactionModel.TRANSACTION_TYPE_INCOME
                    tmpToTransaction.system = true

                    if (tmpTransaction.id != TransactionModel.DEFAULT_ID) {
                        trUpdate(tmpTransaction, tmpToTransaction)
                    } else {
                        trInsert(tmpTransaction, tmpToTransaction)
                    }
                    updateBalance(tmpTransaction, tmpToTransaction)
                }
            } else {
                _needAccount.value = true
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

    private fun trInsert(transaction: TransactionModel, toTransaction: TransactionModel? = null) {
        viewModelScope.launch {
            toTransaction?.let {
                val idTo = transactionDao.insertTransaction(it)
                transaction.extraValue = idTo.toString()
            }
            transactionDao.insertTransaction(transaction)
            _needToNavigate.value = true
        }
    }

    private fun trUpdate(transaction: TransactionModel, toTransaction: TransactionModel? = null) {
        viewModelScope.launch {
            toTransaction?.let {
                it.id = transaction.extraValue.toInt()
                transactionDao.updateTransaction(it)
            }
            transactionDao.updateTransaction(transaction)
            _needToNavigate.value = true
        }
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