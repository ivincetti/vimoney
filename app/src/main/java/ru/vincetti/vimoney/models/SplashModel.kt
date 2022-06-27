package ru.vincetti.vimoney.models

import ru.vincetti.modules.core.models.*
import ru.vincetti.modules.core.models.Currency
import ru.vincetti.modules.database.repository.*
import ru.vincetti.modules.network.ConfigLoader
import ru.vincetti.modules.network.models.*
import ru.vincetti.vimoney.utils.SampleDescription
import java.util.*
import javax.inject.Inject

class SplashModel @Inject constructor(
    private val accountRepo: AccountRepo,
    private val transactionRepo: TransactionRepo,
    private val configRepo: ConfigRepo,
    private val categoryRepo: CategoryRepo,
    private val currencyRepo: CurrencyRepo,
    private val configLoader: ConfigLoader,
    private val sampleDescription: SampleDescription
) {

    suspend fun loadConfigByKey(): Config? = configRepo.loadByKey()

    suspend fun updateConfig() {
        val configModel = configLoader.loadPreferences()
        configDbUpdate(configModel)
    }

    private suspend fun configDbUpdate(response: ConfigFile) {
        configDbDateInsert(response.dateEdit)
        currencyImport(response.currency)
        accountsUpdate(response.accounts)
        categoriesUpdate(response.categories)
        transactionsImport(response.transactions)
    }

    private suspend fun configDbDateInsert(timeMillisLong: Long) {
        configRepo.add(timeMillisLong.toString())
    }

    private suspend fun transactionsImport(transactionItems: List<TransactionsItem>) {
        val transactions = mutableListOf<Transaction>()
        transactionItems.map {
            transactions.add(
                Transaction(
                    Date(it.date),
                    it.accountId,
                    sampleDescription.get(),
                    it.type,
                    it.sum.toFloat()
                )
            )
        }
        transactionRepo.add(transactions)
        updateAccounts(accountRepo, transactionRepo)
    }

    private suspend fun currencyImport(currencyItems: List<CurrencyItem>) {
        currencyItems.forEach {
            currencyRepo.add(
                Currency(it.code, it.name, it.symbol)
            )
        }
    }

    private suspend fun accountsUpdate(accountsItems: List<AccountsItem>) {
        for (acc in accountsItems) {
            accountUpdate(
                acc.id,
                acc.type,
                acc.title,
                acc.balance
            )
        }
    }

    private suspend fun accountUpdate(accId: Int, type: String, title: String, balance: Int) {
        val newAcc = Account(accId, title, type, balance)
        accountRepo.add(newAcc)
    }

    private suspend fun categoriesUpdate(categoriesItems: List<CategoriesItem>) {
        categoriesItems.forEach {
            categoryUpdate(
                it.categoryId,
                it.name,
                it.symbol
            )
        }
    }

    private suspend fun categoryUpdate(catId: Int, name: String, symbol: String) {
        val newCat = Category(
            catId,
            name,
            symbol,
            isForExpense = true,
            isForIncome = true,
            isArchive = false,
        )
        categoryRepo.add(newCat)
    }

    private suspend fun updateAccounts(
        accountRepo: AccountRepo,
        transactionRepo: TransactionRepo
    ) {
        accountRepo.loadAll().forEach {
            val sum = transactionRepo.loadBalanceByCheckId(it.id)
            accountRepo.updateAccountBalanceById(it.id, sum)
        }
    }
}
