package ru.vincetti.vimoney.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.vincetti.vimoney.data.repository.TransactionRepo
import ru.vincetti.vimoney.data.sqlite.AccountDao
import ru.vincetti.vimoney.data.sqlite.AppDatabase

object BalanceMathUtils {

    suspend fun accountBalanceUpdateById(
        db: AppDatabase,
        accId: Int
    ) {
        withContext(Dispatchers.IO) {
            val sum = TransactionRepo(db).loadCheckSum(accId)
            db.accountDao().updateSumByAccId(accId, sum)
        }
    }

    suspend fun accountBalanceUpdateAll(db: AppDatabase) {
        withContext(Dispatchers.IO) {
            val accDao = db.accountDao()
            val accounts = accDao.loadAllAccounts()
            val transRepo = TransactionRepo(db)
            accounts?.forEach {
                val sum = transRepo.loadCheckSum(it.id)
                accDao.updateSumByAccId(it.id, sum)
            }
        }
    }

    suspend fun userBalanceUpdate(accountDao: AccountDao): Int {
        var balance = 0
        val accounts = accountDao.loadAllAccounts()
        accounts?.let { list ->
            list.filter { it.needAllBalance && !it.isArchive }.forEach { balance += it.sum }
        }
        return balance
    }
}
