package ru.vincetti.vimoney.ui.transaction.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.vincetti.modules.core.models.AccountList
import ru.vincetti.modules.core.models.Category
import ru.vincetti.modules.core.models.Transaction
import ru.vincetti.modules.core.utils.SingleLiveEvent
import ru.vincetti.vimoney.models.TransactionsModel
import java.util.*
import javax.inject.Inject

@HiltViewModel
@Suppress("TooManyFunctions")
class TransactionMainViewModel @Inject constructor(
    private val transactionsModel: TransactionsModel,
) : ViewModel() {

//    private var _transaction = MutableLiveData<Transaction>()
//    val transaction: LiveData<Transaction>
//        get() = _transaction
//
//    private val _nestedTransaction = MutableLiveData<Transaction>()
//    val nestedTransaction: LiveData<Transaction>
//        get() = _nestedTransaction

    private val _contentState = SingleLiveEvent<TransactionMainContentState>()
    val contentState: LiveData<TransactionMainContentState>
        get() = _contentState

    private val _action = SingleLiveEvent<TransactionMainContentAction>()
    val action: LiveData<TransactionMainContentAction>
        get() = _action

//    private val _needToUpdate = MutableLiveData<Boolean>()
//    val needToUpdate: LiveData<Boolean>
//        get() = _needToUpdate
//
//    private val _date = MutableLiveData<Date>()
//    val date: LiveData<Date>
//        get() = _date

//    private val _accountId = MutableLiveData<Int>()
//    private val account: LiveData<AccountList> = _accountId.switchMap {
//        transactionsModel.loadAccountById(it)
//    }.filterOutNull()
//
//    val accountName: LiveData<String> = account.map { it.name }
//    val currency: LiveData<String> = account.map { it.curSymbol }
//
//    private val _accountIdTo = MutableLiveData<Int>()
//    private val accountTo: LiveData<AccountList> = _accountIdTo.switchMap {
//        transactionsModel.loadAccountById(it)
//    }.filterOutNull()

//    val accountToName: LiveData<String> = accountTo.map { it.name }
//    val currencyTo: LiveData<String> = accountTo.map { it.curSymbol }
//
//    private val _sum = MutableLiveData<Float>()
//    val sum: LiveData<Float>
//        get() = _sum
//
//    private val _categoryId = MutableLiveData<Int>()
//    val category: LiveData<Category?> = _categoryId.switchMap {
//        transactionsModel.loadCategoryById(it)
//    }

    val categoriesList: LiveData<List<Category>> = transactionsModel.loadAllCategories()

    val accountNotArchiveNames: LiveData<List<AccountList>> = transactionsModel.loadNotArchiveAccounts()

//    private val _description = MutableLiveData<String>()
//    val description: LiveData<String>
//        get() = _description

//    private var oldTransactionAccountID = Transaction.DEFAULT_ID
//    private var oldTransactionAccountToID = Transaction.DEFAULT_ID
//
//    private var nestedId = Transaction.DEFAULT_ID
//    private var mTransId = Transaction.DEFAULT_ID

    init {
        _contentState.value = TransactionMainContentState.Empty
//        _transaction.value = Transaction()
//        _nestedTransaction.value = Transaction()
//        _accountId.value = Transaction.DEFAULT_ID
//        _accountIdTo.value = Transaction.DEFAULT_ID
//        _categoryId.value = Transaction.DEFAULT_CATEGORY
//        _needToUpdate.value = false
//        _date.value = Date()
    }

    fun loadTransaction(id: Int) {
        viewModelScope.launch {
            val tmpTransaction = transactionsModel.loadTransactionById(id)
            tmpTransaction?.let { transition ->
                when (transition.type) {
                    Transaction.TRANSACTION_TYPE_INCOME -> {
                        _contentState.value = TransactionMainContentState.Filled.Income(true, transition)
                    }
                    Transaction.TRANSACTION_TYPE_SPENT -> {
                        _contentState.value = TransactionMainContentState.Filled.Spending(true, transition)
                    }
                    Transaction.TRANSACTION_TYPE_TRANSFER -> {
                        check(tmpTransaction.extraValue.toInt() > 0)
                        val nestedId = tmpTransaction.extraValue.toInt()
                        transactionsModel.loadTransactionById(nestedId)?.let { nestedTransaction ->
                            _contentState.value = TransactionMainContentState.Filled.Transfer(
                                true,
                                transition,
                                nestedTransaction
                            )
//                            _accountIdTo.value = nestedTransaction.accountId
//                            oldTransactionAccountToID = nestedTransaction.accountId
                        }
                    }
                }
//                _transaction.value = it
//                if (tmpTransaction.id != Transaction.DEFAULT_ID) {
//                    _needToUpdate.value = true
//                    _accountId.value = it.accountId
//                    _categoryId.value = it.categoryId
//                    _sum.value = tmpTransaction.sum
//                    _description.value = it.description ?: ""
//                    _date.value = it.date
//                    mTransId = tmpTransaction.id
//                    oldTransactionAccountID = tmpTransaction.id
//                }
//                if (
//                    tmpTransaction.extraKey == Transaction.TRANSACTION_TYPE_TRANSFER_KEY &&
//                    tmpTransaction.extraValue.toInt() > 0
//                ) {
//
//                    transactionsModel.loadTransactionById(nestedId)?.let { nestedTransaction ->
//                        _nestedTransaction.value = nestedTransaction
//                        _accountIdTo.value = nestedTransaction.accountId
//                        oldTransactionAccountToID = nestedTransaction.accountId
//                    }
//                }
            }
        }
    }

    fun setAccount(id: Int) {
//        _accountId.value = id
    }

    fun setAccountTo(id: Int) {
//        _accountIdTo.value = id
    }

    fun setSum(newSum: String) {
        if (newSum.isNotEmpty()) {
            val sumF = newSum.toFloat()
            if (sumF > 0) {
//                _sum.value = sumF
//                _nestedTransaction.value?.sum = sumF
            }
        }
    }

    fun setDescription(newDescription: String) {
//        _description.value = newDescription
    }

    fun setDate(newDate: Date) {
//        _date.value = newDate
    }

    fun setCategoryID(categoryID: Int) {
//        _categoryId.value = categoryID
    }

    fun delete() {
        viewModelScope.launch {
//            _transaction.value?.let {
//                if (it.id != Transaction.DEFAULT_ID) {
//                    if (it.isTransfer) {
//                        _nestedTransaction.value?.let { nested ->
//                            transactionsModel.deleteTransaction(nested)
//                        }
//                    }
//                    transactionsModel.deleteTransaction(it)
//                    _action.value = TransactionMainContentAction.CloseSelf
//                }
//            }
        }
    }

    fun saveTransaction(actionType: Int, txtName: String, txtSum: String) {
//        val tmpTransaction = _transaction.value
//        tmpTransaction?.let {
//            when {
//                txtSum == "" -> _action.value = TransactionMainContentAction.SumError
//                _accountId.value!! < 1 -> _action.value = TransactionMainContentAction.AccountError
//                else -> {
//                    tmpTransaction.description = txtName
//                    tmpTransaction.date = date.value!!
//                    tmpTransaction.type = actionType
//                    tmpTransaction.accountId = _accountId.value!!
//                    tmpTransaction.sum = txtSum.toFloat()
//                    tmpTransaction.categoryId = _categoryId.value!!
//
//                    saveTransaction(tmpTransaction)
//                }
//            }
//        }
    }

    fun saveTransactionTo(txtName: String, txtSum: String, txtSumTo: String) {
//        _transaction.value?.let { tmpTransaction ->
//            _nestedTransaction.value?.let { tmpToTransaction ->
//                when {
//                    _accountId.value == Transaction.DEFAULT_ID -> _action.value = TransactionMainContentAction.AccountError
//                    _accountIdTo.value == Transaction.DEFAULT_ID -> _action.value = TransactionMainContentAction.AccountError
//                    txtSumTo == "" || txtSum == "" -> TransactionMainContentAction.SumError
//                    else -> {
//                        tmpTransaction.description = txtName
//                        tmpTransaction.date = date.value!!
//                        tmpTransaction.accountId = _accountId.value!!
//                        tmpTransaction.type = Transaction.TRANSACTION_TYPE_TRANSFER
//                        tmpTransaction.sum = txtSum.toFloat()
//                        tmpTransaction.extraKey = Transaction.TRANSACTION_TYPE_TRANSFER_KEY
//
//                        tmpToTransaction.sum = txtSumTo.toFloat()
//                        tmpToTransaction.accountId = _accountIdTo.value!!
//                        tmpToTransaction.extraKey = Transaction.TRANSACTION_TYPE_TRANSFER_KEY
//                        tmpToTransaction.extraValue = tmpTransaction.id.toString()
//                        tmpToTransaction.date = date.value!!
//                        tmpToTransaction.type = Transaction.TRANSACTION_TYPE_INCOME
//                        tmpToTransaction.system = true
//
//                        saveTransaction(tmpTransaction, tmpToTransaction)
//                    }
//                }
//            }
//        }
    }

    private fun saveTransaction(transaction: Transaction, toTransaction: Transaction? = null) {
        viewModelScope.launch {
            transactionsModel.addTransaction(transaction, toTransaction)
            _action.value = TransactionMainContentAction.CloseSelf
        }
    }
}
