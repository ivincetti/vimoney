package ru.vincetti.modules.usecases.transaction

import ru.vincetti.modules.core.models.Transaction
import ru.vincetti.modules.database.repository.AccountRepo
import ru.vincetti.modules.database.repository.TransactionRepo
import javax.inject.Inject

interface UpdateTransactionUseCase {

    suspend operator fun invoke(
        transaction: Transaction,
        toTransaction: Transaction? = null,
    )
}

class UpdateTransactionUseCaseImpl @Inject constructor(
    private val accountRepo: AccountRepo,
    private val transactionRepo: TransactionRepo,
) : UpdateTransactionUseCase {

    override suspend operator fun invoke(
        transaction: Transaction,
        toTransaction: Transaction?,
    ) {
        updateTransaction(transaction, toTransaction)
    }

    // todo update single to transfer vise versa - error
    private suspend fun updateTransaction(transaction: Transaction, toTransaction: Transaction?) {
        if (toTransaction == null) {
            if (transaction.isTransfer.not() && transaction.extraValue.isNotBlank()) {
                val idToDelete = transaction.extraValue.toInt()
                deleteUnusedNestedTransactionById(idToDelete)
            }
        } else {
            if (toTransaction.id != Transaction.DEFAULT_ID) {
                val idTo = addNewNestedTransaction(toTransaction)
                transaction.extraValue = idTo.toString()
            } else {
                updateTransaction(toTransaction)
            }
        }
        updateTransaction(transaction)
    }

    private suspend fun addNewNestedTransaction(transaction: Transaction): Long = addTransaction(transaction)

    private suspend fun deleteUnusedNestedTransactionById(transactionId: Int) = deleteTransactionById(transactionId)

    private suspend fun addTransaction(transaction: Transaction): Long {
        val id = transactionRepo.add(transaction)
        updateBalance(transaction.accountId)
        return id
    }

    private suspend fun updateTransaction(transaction: Transaction) {
        transactionRepo.update(transaction)
        updateBalance(transaction.accountId)
    }

    private suspend fun deleteTransactionById(transactionId: Int) {
        transactionRepo.loadById(transactionId)?.let {
            val accountIdToUpdate = it.accountId
            transactionRepo.delete(it)
            updateBalance(accountIdToUpdate)
        }
    }

    private suspend fun updateBalance(accountId: Int) {
        TransactionsUtils.updateBalance(
            accountRepo = accountRepo,
            transactionRepo = transactionRepo,
            accountId = accountId
        )
    }
}
