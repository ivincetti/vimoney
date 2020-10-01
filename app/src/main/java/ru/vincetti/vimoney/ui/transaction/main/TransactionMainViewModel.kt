package ru.vincetti.vimoney.ui.transaction.main

import androidx.lifecycle.*
import kotlinx.coroutines.launch
import ru.vincetti.vimoney.data.models.TransactionModel
import ru.vincetti.vimoney.data.repository.TransactionRepo
import ru.vincetti.vimoney.data.sqlite.AppDatabase
import ru.vincetti.vimoney.utils.accountBalanceUpdateById
import java.util.*

class TransactionMainViewModel(
    private val database: AppDatabase
) : ViewModel() {

    private val transactionRepo = TransactionRepo(database)
    private val accountDao = database.accountDao()
    private val currentDao = database.currentDao()
    private val categoryDao = database.categoryDao()

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

    private val _date = MutableLiveData<Date>()
    val date
        get() = _date

    private val _accountId = MutableLiveData<Int>()
    val account = _accountId.switchMap {
        liveData {
            emit(accountDao.loadAccountById(it))
        }
    }
    val currency = account.switchMap {
        liveData {
            it?.let {
                emit(currentDao.loadCurrencyByCode(it.currency))
            } ?: run {
                emit(null)
            }
        }
    }

    private val _accountIdTo = MutableLiveData<Int>()
    val accountTo = _accountIdTo.switchMap {
        liveData {
            emit(accountDao.loadAccountById(it))
        }
    }
    val currencyTo = accountTo.switchMap {
        liveData {
            it?.let {
                emit(currentDao.loadCurrencyByCode(it.currency))
            } ?: run {
                emit(null)
            }
        }
    }

    private val _sum = MutableLiveData<Float>()
    val sum
        get() = _sum

    private val _categoryId = MutableLiveData<Int>()
    val category = _categoryId.switchMap {
        liveData {
            emit(categoryDao.loadCategoryById(it))
        }
    }

    val categoriesList = categoryDao.loadAllCategories()
    val accountNotArchiveNames = accountDao.loadNotArchiveAccounts()

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
        _categoryId.value = TransactionModel.DEFAULT_CATEGORY
        _needAccount.value = false
        _needToUpdate.value = false
        _needSum.value = false
        _needToNavigate.value = false
        _date.value = Date()
    }

    fun loadTransaction(id: Int) {
        viewModelScope.launch {
            val tmpTransaction = transactionRepo.loadTransaction(id)
            tmpTransaction?.let {
                _transaction.value = tmpTransaction
                if (tmpTransaction.id != TransactionModel.DEFAULT_ID) {
                    _needToUpdate.value = true
                    _accountId.value = tmpTransaction.accountId
                    _categoryId.value = tmpTransaction.categoryId
                    _sum.value = tmpTransaction.sum
                    _description.value = tmpTransaction.description
                    _date.value = tmpTransaction.date
                    mTransId = tmpTransaction.id
                    oldTransactionAccountID = tmpTransaction.id
                }
                if (
                    tmpTransaction.extraKey == TransactionModel.TRANSACTION_TYPE_TRANSFER_KEY
                    && tmpTransaction.extraValue.toInt() > 0
                ) {
                    nestedId = tmpTransaction.extraValue.toInt()
                    loadNestedTransaction(nestedId)?.let {
                        _nestedTransaction.value = it
                        _accountIdTo.value = it.accountId
                        oldTransactionAccountToID = it.accountId
                    }
                }
            }
        }
    }

    private suspend fun loadNestedTransaction(id: Int) = transactionRepo.loadTransaction(id)

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

    fun setCategoryID(categoryID: Int) {
        _categoryId.value = categoryID
    }

    fun delete() {
        viewModelScope.launch {
            _transaction.value?.let {
                if (it.id != TransactionModel.DEFAULT_ID) {
                    if (
                        it.extraKey == TransactionModel.TRANSACTION_TYPE_TRANSFER_KEY
                        && nestedId > 0
                    ) {
                        transactionRepo.deleteTransaction(nestedId)
                        accountBalanceUpdateById(database, _accountIdTo.value!!)
                    }
                    transactionRepo.deleteTransaction(mTransId)
                    accountBalanceUpdateById(database, _accountId.value!!)
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
                _accountId.value!! < 1 -> _needAccount.value = true
                else -> {
                    tmpTransaction.description = txtName
                    tmpTransaction.date = date.value!!
                    tmpTransaction.type = actionType
                    tmpTransaction.accountId = _accountId.value!!
                    tmpTransaction.sum = txtSum.toFloat()
                    tmpTransaction.categoryId = _categoryId.value!!
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
        _transaction.value?.let { tmpTransaction ->
            _nestedTransaction.value?.let { tmpToTransaction ->
                if (
                    _accountId.value != TransactionModel.DEFAULT_ID
                    && _accountIdTo.value != TransactionModel.DEFAULT_ID
                ) {
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
    }

    fun navigatedBack(){
        _needToNavigate.value = false
    }

    private fun updateBalance(vararg trans: TransactionModel) {
        viewModelScope.launch {
            for (transaction in trans) {
                accountBalanceUpdateById(database, transaction.accountId)
            }
        }
    }

    private fun trInsert(transaction: TransactionModel, toTransaction: TransactionModel? = null) {
        viewModelScope.launch {
            toTransaction?.let {
                val idTo: Long = transactionRepo.addTransaction(it)
                transaction.extraValue = idTo.toString()
            }
            transactionRepo.addTransaction(transaction)
            _needToNavigate.value = true
        }
    }

    private fun trUpdate(transaction: TransactionModel, toTransaction: TransactionModel? = null) {
        viewModelScope.launch {
            toTransaction?.let {
                it.id = transaction.extraValue.toInt()
                transactionRepo.updateTransaction(it)
            }
            transactionRepo.updateTransaction(transaction)
            _needToNavigate.value = true
        }
    }
}

class TransactionMainViewModelFactory(
    private val database: AppDatabase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TransactionMainViewModel::class.java)) {
            return TransactionMainViewModel(database) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
