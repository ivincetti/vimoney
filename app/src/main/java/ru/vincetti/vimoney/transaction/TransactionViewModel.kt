package ru.vincetti.vimoney.transaction

import androidx.lifecycle.*
import kotlinx.coroutines.launch
import ru.vincetti.vimoney.data.models.TransactionModel
import ru.vincetti.vimoney.data.sqlite.AccountDao
import ru.vincetti.vimoney.data.sqlite.TransactionDao
import ru.vincetti.vimoney.utils.LogicMath
import java.util.*

class TransactionViewModel(
        val transactionDao: TransactionDao,
        val accountDao: AccountDao
) : ViewModel() {
    private val curSymbolsId = MutableLiveData<HashMap<Int, String>>()
    private val accountNames = MutableLiveData<HashMap<Int, String>>()

    private val _accountNotArchiveNames = MutableLiveData<HashMap<Int, String>>()
    val accountNotArchiveNames: LiveData<HashMap<Int, String>>
        get() = _accountNotArchiveNames

    var saveAction = TransactionModel.TRANSACTION_TYPE_SPENT
    private var oldTransactionAccountID = TransactionModel.DEFAULT_ID
    private var oldTransactionAccountToID = TransactionModel.DEFAULT_ID

    private val _transaction = MutableLiveData<TransactionModel>()
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

    private val _error = MutableLiveData<Boolean>()
    val error
        get() = _error

    val date = Transformations.map(_transaction) {
        it.date
    }

    init {
        _transaction.value = TransactionModel()
        _nestedTransaction.value = TransactionModel()
        _needAccount.value = false
        _needToUpdate.value = false
        _needSum.value = false
        _error.value = false
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
                    oldTransactionAccountToID = _nestedTransaction.value?.accountId
                            ?: TransactionModel.DEFAULT_ID
                }
            }
        }
    }

    private suspend fun loadNestedTransaction(id: Int) = transactionDao.loadTransactionById(id)

    fun loadFromCurSymbols(id: Int) = curSymbolsId.value?.get(id)
    fun loadFromAccountNames(id: Int) = accountNames.value?.get(id)
    fun loadFromAccountNotArchiveNames(id: Int) = accountNotArchiveNames.value?.get(id)

    fun changeSumAdd(s: CharSequence?) {
        s?.let {
            _transaction.value?.sum = it.toString().toFloat()
            _nestedTransaction.value?.sum = it.toString().toFloat()
        }
    }

    fun setDate(date: Date) {
        _transaction.value?.let {
            it.date = date
        }
    }

    fun setAccount(id: Int) {
        _transaction.value?.let {
            it.accountId = id
        }
    }

    fun setAccountTo(id: Int) {
        _nestedTransaction.value?.let {
            it.accountId = id
        }
    }

    fun saveTransaction(txtName: String,
                        txtSum: String,
                        txtSumTo: String) {
        val tmpTransaction = _transaction.value
        val tmpToTransaction = _nestedTransaction.value
        if (tmpTransaction == null || tmpToTransaction == null) {
            _error.value = true
        } else {
            if (tmpTransaction.accountId != TransactionModel.DEFAULT_ID && tmpToTransaction.accountId != TransactionModel.DEFAULT_ID) {
                if (txtSumTo == "" || txtSum == "") {
                    _needSum.value = true
                } else {
                    tmpTransaction.description = txtName
                    tmpTransaction.date = date.value
                    tmpTransaction.type = saveAction
                    tmpTransaction.sum = txtSum.toFloat()
                    tmpTransaction.extraKey = TransactionModel.TRANSACTION_TYPE_TRANSFER_KEY

                    tmpToTransaction.sum = txtSumTo.toFloat()
                    tmpToTransaction.accountId = tmpTransaction.accountId
                    tmpToTransaction.extraKey = TransactionModel.TRANSACTION_TYPE_TRANSFER_KEY
                    tmpToTransaction.extraValue = tmpTransaction.id.toString()
                    tmpToTransaction.date = date.value
                    tmpToTransaction.type = TransactionModel.TRANSACTION_TYPE_INCOME
                    tmpToTransaction.isSystem = true

                    if (tmpTransaction.id != TransactionModel.DEFAULT_ID) {
                        transferUpdate()
                    } else {
                        // new transaction
                        transferInsert()
                    }
                    updateBalance(tmpTransaction, tmpToTransaction)
                }
            } else {
                _needAccount.value = true
            }
        }
    }

    fun saveTransaction(txtName: String,
                        txtSum: String) {
        val tmpTransaction = _transaction.value
        if (tmpTransaction == null) {
            _error.value = true
        } else {
            if (tmpTransaction.accountId != TransactionModel.DEFAULT_ID) {
                if (txtSum == "") {
                    _needSum.value = true
                } else {
                    tmpTransaction.description = txtName
                    tmpTransaction.date = date.value
                    tmpTransaction.type = saveAction
                    tmpTransaction.sum = txtSum.toFloat()

                    if (tmpTransaction.id != TransactionModel.DEFAULT_ID) {
                        transferUpdate()
                    } else {
                        // new transaction
                        transferInsert()
                    }
                    updateBalance(tmpTransaction)
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
                LogicMath.accountBalanceUpdateById(transactionDao, accountDao, transaction.accountId)
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

    private fun transferUpdate() {
        _nestedTransaction.value?.id = _transaction.value?.extraValue!!.toInt()
        viewModelScope.launch {
            transactionDao.updateTransaction(_nestedTransaction.value!!)
            transactionDao.updateTransaction(_transaction.value!!)
        }
    }

    private fun transferInsert() {
        viewModelScope.launch {
            _nestedTransaction.value?.let {
                val idTo = transactionDao.insertTransaction(it)
                _transaction.value?.extraValue = idTo.toString()
                transactionDao.insertTransaction(_transaction.value!!)
            }
        }
    }

    fun setAccountNames(hash: HashMap<Int, String>) {
        accountNames.value = hash
    }

    fun setNotArchiveAccountNames(hash: HashMap<Int, String>) {
        _accountNotArchiveNames.value = hash
    }

    fun setCurrencyIdSymbols(hash: HashMap<Int, String>) {
        curSymbolsId.value = hash
    }

}

class TransactionViewModelFactory(
        private val transactionDao: TransactionDao,
        private val accDao: AccountDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TransactionViewModel::class.java)) {
            return TransactionViewModel(transactionDao, accDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

