package ru.vincetti.modules.usecases.account

import ru.vincetti.modules.database.repository.AccountRepo
import ru.vincetti.modules.database.repository.TransactionRepo
import ru.vincetti.modules.usecases.transaction.TransactionsUtils
import javax.inject.Inject

interface UpdateBalanceAccountByIdUseCase {

    suspend operator fun invoke(accountId: Int)
}

class UpdateBalanceAccountByIdUseCaseImpl @Inject constructor(
    private val transactionRepo: TransactionRepo,
    private val accountRepo: AccountRepo,
) : UpdateBalanceAccountByIdUseCase {

    override suspend operator fun invoke(accountId: Int) {
        return updateBalance(accountId)
    }

    private suspend fun updateBalance(accountId: Int) {
        TransactionsUtils.updateBalance(
            accountRepo = accountRepo,
            transactionRepo = transactionRepo,
            accountId = accountId
        )
    }
}
