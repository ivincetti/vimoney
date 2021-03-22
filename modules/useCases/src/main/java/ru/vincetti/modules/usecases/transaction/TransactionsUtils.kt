package ru.vincetti.modules.usecases.transaction

import ru.vincetti.modules.database.repository.AccountRepo
import ru.vincetti.modules.database.repository.TransactionRepo

object TransactionsUtils {

    @JvmStatic
    suspend fun updateBalance(
        accountRepo: AccountRepo,
        transactionRepo: TransactionRepo,
        accountId: Int
    ) {
        val newBalance = transactionRepo.loadBalanceByCheckId(accountId)
        accountRepo.updateAccountBalanceById(accountId, newBalance)
    }
}
