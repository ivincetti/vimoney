package ru.vincetti.vimoney.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.vincetti.vimoney.data.models.TransactionModel
import ru.vincetti.vimoney.data.repository.TransactionRepo
import java.util.*

object TransactionsGenerator {

    private const val SAMPLE_COUNT = 5
    private const val SAMPLE_ACC_COUNT = 3
    private const val SAMPLE_DESC = "Sample"
    private const val SAMPLE_SUM = 150F

    private suspend fun generate(transactionRepo: TransactionRepo, count: Int) {
        withContext(Dispatchers.IO) {
            for (i in 0..count) {
                val tmp = TransactionModel(
                    Date(),
                    (Random().nextInt(SAMPLE_ACC_COUNT) + 1),
                    SAMPLE_DESC,
                    (i % 2 + 1),
                    SAMPLE_SUM
                )
                transactionRepo.addTransaction(tmp)
            }
        }
    }

    suspend fun generateSample(transactionRepo: TransactionRepo) {
        generate(transactionRepo, SAMPLE_COUNT)
    }
}
