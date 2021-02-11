package ru.vincetti.modules.database.sqlite

import ru.vincetti.modules.core.models.Account
import ru.vincetti.modules.core.models.Category
import ru.vincetti.modules.core.models.Transaction
import ru.vincetti.modules.database.repository.AccountRepo
import ru.vincetti.modules.database.repository.CategoryRepo
import ru.vincetti.modules.database.repository.TransactionRepo

object ImportData {

    suspend fun import(
        transactionRepo: TransactionRepo,
        accountRepo: AccountRepo,
        categoryRepo: CategoryRepo,
        data: Triple<List<Transaction>, List<Account>, List<Category>>
    ) {
        if (!data.first.isNullOrEmpty()) {
            importTransactions(transactionRepo, data.first)
            importAccounts(accountRepo, data.second)
            importCategories(categoryRepo, data.third)
        }
    }

    private suspend fun importTransactions(
        transactionRepo: TransactionRepo,
        transactions: List<Transaction>
    ) {
        transactionRepo.deleteAll()
        transactionRepo.add(transactions)
    }

    private suspend fun importAccounts(
        accountRepo: AccountRepo,
        accounts: List<Account>
    ) {
        accountRepo.deleteAll()
        accountRepo.add(accounts)
    }

    private suspend fun importCategories(
        categoryRepo: CategoryRepo,
        categories: List<Category>
    ) {
        categoryRepo.deleteAll()
        categoryRepo.add(categories)
    }
}
