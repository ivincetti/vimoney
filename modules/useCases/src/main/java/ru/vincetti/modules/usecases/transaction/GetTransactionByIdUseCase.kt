package ru.vincetti.modules.usecases.transaction

import ru.vincetti.modules.core.models.Transaction
import ru.vincetti.modules.database.repository.TransactionRepo
import javax.inject.Inject

interface GetTransactionByIdUseCase {

    suspend operator fun invoke(
        transactionId: Int,
    ): Transaction?
}

class GetTransactionByIdUseCaseImpl @Inject constructor(
    private val transactionRepo: TransactionRepo,
) : GetTransactionByIdUseCase {

    override suspend operator fun invoke(transactionId: Int): Transaction? {
        return transactionRepo.loadById(transactionId)
    }
}
