package ru.vincetti.vimoney.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.vincetti.vimoney.data.repository.TransactionRepo
import ru.vincetti.vimoney.data.sqlite.AccountDao
import ru.vincetti.vimoney.data.sqlite.AppDatabase
import ru.vincetti.vimoney.data.sqlite.TransactionDao

/** Set correct account (accID) balance. */
suspend fun accountBalanceUpdateById(
    transactionDao: TransactionDao,
    accountDao: AccountDao,
    accId: Int
) {
    withContext(Dispatchers.IO) {
        val sum = transactionDao.loadSumByCheckId(accId)
        accountDao.updateSumByAccId(accId, sum)
    }
}

/** Set correct all account balance. */
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

/** Math all user balance. */
suspend fun userBalanceUpdate(accountDao: AccountDao): Int {
    var balance = 0
    val accounts = accountDao.loadAllAccounts()
    accounts?.let { accs ->
        accs.filter { it.needAllBalance && !it.isArchive }.forEach { balance += it.sum }
    }
    return balance
}
