package ru.vincetti.vimoney.ui.transaction.main

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.vincetti.modules.core.models.Transaction
import ru.vincetti.modules.database.repository.AccountRepo
import ru.vincetti.modules.database.repository.CategoryRepo
import ru.vincetti.modules.database.repository.CurrencyRepo
import ru.vincetti.modules.database.repository.TransactionRepo
import java.util.*
import javax.inject.Inject

@HiltViewModel
@Suppress("TooManyFunctions")
class TransactionMainViewModel @Inject constructor(
    private val transactionRepo: TransactionRepo,
    private val accountRepo: AccountRepo,
    private val categoryRepo: CategoryRepo,
    private val currencyRepo: CurrencyRepo
) : ViewModel() {

    private var _transaction = MutableLiveData<Transaction>()
    val transaction
        get() = _transaction

    private val _nestedTransaction = MutableLiveData<Transaction>()
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
            emit(accountRepo.loadById(it))
        }
    }
    val currency = account.switchMap {
        liveData {
            it?.let {
                emit(currencyRepo.loadByCode(it.currency))
            } ?: run {
                emit(null)
            }
        }
    }

    private val _accountIdTo = MutableLiveData<Int>()
    val accountTo = _accountIdTo.switchMap {
        liveData {
            emit(accountRepo.loadById(it))
        }
    }
    val currencyTo = accountTo.switchMap {
        liveData {
            it?.let {
                emit(currencyRepo.loadByCode(it.currency))
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
            emit(categoryRepo.loadById(it))
        }
    }

    val categoriesList = liveData {
        emit(categoryRepo.loadAll())
    }
    val accountNotArchiveNames = liveData {
        emit(accountRepo.loadNotArchive())
    }

    private val _description = MutableLiveData<String>()
    val description
        get() = _description

    private var oldTransactionAccountID = Transaction.DEFAULT_ID
    private var oldTransactionAccountToID = Transaction.DEFAULT_ID

    private var nestedId = Transaction.DEFAULT_ID
    private var mTransId = Transaction.DEFAULT_ID

    init {
        _transaction.value = Transaction()
        _nestedTransaction.value = Transaction()
        _accountId.value = Transaction.DEFAULT_ID
        _accountIdTo.value = Transaction.DEFAULT_ID
        _categoryId.value = Transaction.DEFAULT_CATEGORY
        _needAccount.value = false
        _needToUpdate.value = false
        _needSum.value = false
        _needToNavigate.value = false
        _date.value = Date()
    }

    fun loadTransaction(id: Int) {
        viewModelScope.launch {
            val tmpTransaction = transactionRepo.loadById(id)
            tmpTransaction?.let {
                _transaction.value = tmpTransaction
                if (tmpTransaction.id != Transaction.DEFAULT_ID) {
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
                    tmpTransaction.extraKey == Transaction.TRANSACTION_TYPE_TRANSFER_KEY &&
                    tmpTransaction.extraValue.toInt() > 0
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

    private suspend fun loadNestedTransaction(id: Int) = transactionRepo.loadById(id)

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
                if (it.id != Transaction.DEFAULT_ID) {
                    if (it.extraKey == Transaction.TRANSACTION_TYPE_TRANSFER_KEY) {
                        _nestedTransaction.value?.let { nested ->
                            transactionRepo.delete(nested)
                        }
                    }
                    transactionRepo.delete(it)
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
                    if (tmpTransaction.id != Transaction.DEFAULT_ID) {
                        trUpdate(tmpTransaction)
                    } else {
                        trInsert(tmpTransaction)
                    }
                }
            }
        }
    }

    fun saveTransactionTo(actionType: Int, txtName: String, txtSum: String, txtSumTo: String) {
        _transaction.value?.let { tmpTransaction ->
            _nestedTransaction.value?.let { tmpToTransaction ->
                if (
                    _accountId.value != Transaction.DEFAULT_ID &&
                    _accountIdTo.value != Transaction.DEFAULT_ID
                ) {
                    if (txtSumTo == "" || txtSum == "") {
                        _needSum.value = true
                    } else {
                        tmpTransaction.description = txtName
                        tmpTransaction.date = date.value!!
                        tmpTransaction.accountId = _accountId.value!!
                        tmpTransaction.type = actionType
                        tmpTransaction.sum = txtSum.toFloat()
                        tmpTransaction.extraKey = Transaction.TRANSACTION_TYPE_TRANSFER_KEY

                        tmpToTransaction.sum = txtSumTo.toFloat()
                        tmpToTransaction.accountId = _accountIdTo.value!!
                        tmpToTransaction.extraKey = Transaction.TRANSACTION_TYPE_TRANSFER_KEY
                        tmpToTransaction.extraValue = tmpTransaction.id.toString()
                        tmpToTransaction.date = date.value!!
                        tmpToTransaction.type = Transaction.TRANSACTION_TYPE_INCOME
                        tmpToTransaction.system = true

                        if (tmpTransaction.id != Transaction.DEFAULT_ID) {
                            trUpdate(tmpTransaction, tmpToTransaction)
                        } else {
                            trInsert(tmpTransaction, tmpToTransaction)
                        }
                    }
                } else {
                    _needAccount.value = true
                }
            }
        }
    }

    fun navigatedBack() {
        _needToNavigate.value = false
    }

    private fun trInsert(transaction: Transaction, toTransaction: Transaction? = null) {
        viewModelScope.launch {
            toTransaction?.let {
                val idTo: Long = transactionRepo.add(it)
                transaction.extraValue = idTo.toString()
            }
            transactionRepo.add(transaction)
            _needToNavigate.value = true
        }
    }

    private fun trUpdate(transaction: Transaction, toTransaction: Transaction? = null) {
        viewModelScope.launch {
            toTransaction?.let {
                it.id = transaction.extraValue.toInt()
                transactionRepo.update(it)
            }
            transactionRepo.update(transaction)
            _needToNavigate.value = true
        }
    }
}
