package ru.vincetti.vimoney.utils;

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.vincetti.vimoney.data.models.TransactionModel
import ru.vincetti.vimoney.data.sqlite.TransactionDao
import java.util.*

class TransactionsGenerator {

    companion object {
        private const val TRANSACTION_COUNT = 5

        // sample accounts transactions generate
        private suspend fun generate(transactionDao: TransactionDao, count: Int) {
            withContext(Dispatchers.IO) {
                for (i in 0..count) {
                    val tmp = TransactionModel(
                            Date(),
                            (Random().nextInt(3) + 1),
                            "Sample",
                            (i % 2 + 1),
                            150f
                    )
                    transactionDao.insertTransaction(tmp)
                }
            }
        }

        // use default count
        suspend fun generate(transactionDao: TransactionDao) {
            generate(transactionDao, TRANSACTION_COUNT)
        }

    }
}
