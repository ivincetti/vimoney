package ru.vincetti.modules.usecases.transaction

import ru.vincetti.modules.core.models.Transaction
import ru.vincetti.modules.database.repository.AccountRepo
import ru.vincetti.modules.database.repository.TransactionRepo
import javax.inject.Inject

interface AddTransactionUseCase {

    suspend operator fun invoke(
        transaction: Transaction,
        toTransaction: Transaction? = null,
    )
}

class AddTransactionUseCaseImpl @Inject constructor(
    private val accountRepo: AccountRepo,
    private val transactionRepo: TransactionRepo,
) : AddTransactionUseCase {

    override suspend operator fun invoke(
        transaction: Transaction,
        toTransaction: Transaction?,
    ) {
        addTransaction(transaction, toTransaction)
    }

    private suspend fun addTransaction(
        transaction: Transaction,
        toTransaction: Transaction?,
    ) {
        toTransaction?.let {
            val idTo: Long = addTransaction(it)
            transaction.extraValue = idTo.toString()
        }
        addTransaction(transaction)
    }

    private suspend fun addTransaction(transaction: Transaction): Long {
        val id = transactionRepo.add(transaction)
        updateBalance(transaction.accountId)
        return id
    }

    private suspend fun updateBalance(accountId: Int) {
        TransactionsUtils.updateBalance(
            accountRepo = accountRepo,
            transactionRepo = transactionRepo,
            accountId = accountId
        )
    }
}
