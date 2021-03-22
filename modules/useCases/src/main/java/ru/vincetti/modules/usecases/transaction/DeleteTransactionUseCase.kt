package ru.vincetti.modules.usecases.transaction

import ru.vincetti.modules.core.models.Transaction
import ru.vincetti.modules.database.repository.AccountRepo
import ru.vincetti.modules.database.repository.TransactionRepo
import javax.inject.Inject

interface DeleteTransactionUseCase {

    suspend operator fun invoke(transaction: Transaction)
}

class DeleteTransactionUseCaseImpl @Inject constructor(
    private val accountRepo: AccountRepo,
    private val transactionRepo: TransactionRepo,
) : DeleteTransactionUseCase {

    override suspend operator fun invoke(transaction: Transaction) {
        val accountIdToUpdate = transaction.accountId
        transactionRepo.delete(transaction)
        updateBalance(accountIdToUpdate)
    }

    private suspend fun updateBalance(accountId: Int) {
        TransactionsUtils.updateBalance(
            accountRepo = accountRepo,
            transactionRepo = transactionRepo,
            accountId = accountId
        )
    }
}
