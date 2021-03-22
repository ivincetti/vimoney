package ru.vincetti.vimoney.models

import androidx.lifecycle.LiveData
import ru.vincetti.modules.core.models.AccountList
import ru.vincetti.modules.core.models.Category
import ru.vincetti.modules.core.models.Transaction
import ru.vincetti.modules.usecases.account.GetActiveAccountsUseCase
import ru.vincetti.modules.usecases.account.GetLiveAccountByIdUseCase
import ru.vincetti.modules.usecases.category.GetAllCategoriesUseCase
import ru.vincetti.modules.usecases.category.GetCategoryByIdUseCase
import ru.vincetti.modules.usecases.transaction.AddTransactionUseCase
import ru.vincetti.modules.usecases.transaction.DeleteTransactionUseCase
import ru.vincetti.modules.usecases.transaction.GetTransactionByIdUseCase
import ru.vincetti.modules.usecases.transaction.UpdateTransactionUseCase
import javax.inject.Inject

class TransactionsModel @Inject constructor(
    private val getLiveAccountByIdUseCase: GetLiveAccountByIdUseCase,
    private val getActiveAccountUseCase: GetActiveAccountsUseCase,
    private val getTransactionByIdUseCase: GetTransactionByIdUseCase,
    private val addTransactionUseCase: AddTransactionUseCase,
    private val updateTransactionUseCase: UpdateTransactionUseCase,
    private val deleteTransactionUseCase: DeleteTransactionUseCase,
    private val getCategoryByIdUseCase: GetCategoryByIdUseCase,
    private val getAllCategoriesUseCase: GetAllCategoriesUseCase,
) {

    fun loadNotArchiveAccounts(): LiveData<List<AccountList>> = getActiveAccountUseCase()

    fun loadAccountById(id: Int): LiveData<AccountList?> = getLiveAccountByIdUseCase(id)

    fun loadCategoryById(id: Int): LiveData<Category?> = getCategoryByIdUseCase(id)

    fun loadAllCategories(): LiveData<List<Category>> = getAllCategoriesUseCase()

    suspend fun loadTransactionById(id: Int): Transaction? = getTransactionByIdUseCase(id)

    suspend fun addTransaction(transaction: Transaction, toTransaction: Transaction?) {
        if (transaction.id != Transaction.DEFAULT_ID) {
            updateTransactionUseCase(transaction, toTransaction)
        } else {
            addTransactionUseCase(transaction, toTransaction)
        }
    }

    suspend fun deleteTransaction(transaction: Transaction) {
        deleteTransactionUseCase(transaction)
    }
}
