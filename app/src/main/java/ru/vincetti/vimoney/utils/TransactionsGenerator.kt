package ru.vincetti.vimoney.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.vincetti.vimoney.data.models.TransactionModel
import ru.vincetti.vimoney.data.sqlite.TransactionDao
import java.util.*

private const val SAMPLE_COUNT = 5
private const val SAMPLE_ACC_COUNT = 3
private const val SAMPLE_DESC = "Sample"
private const val SAMPLE_SUM = 150F

/** Sample accounts transactions generate. */
private suspend fun generate(transactionDao: TransactionDao, count: Int) {
    withContext(Dispatchers.IO) {
        for (i in 0..count) {
            val tmp = TransactionModel(
                Date(),
                (Random().nextInt(SAMPLE_ACC_COUNT) + 1),
                SAMPLE_DESC,
                (i % 2 + 1),
                SAMPLE_SUM
            )
            transactionDao.insertTransaction(tmp)
        }
    }
}

/** Generate [SAMPLE_COUNT] transactions. */
suspend fun generateSample(transactionDao: TransactionDao) {
    generate(transactionDao, SAMPLE_COUNT)
}
