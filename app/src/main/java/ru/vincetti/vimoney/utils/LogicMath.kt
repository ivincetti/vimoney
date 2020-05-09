package ru.vincetti.vimoney.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.vincetti.vimoney.data.models.AccountListModel
import ru.vincetti.vimoney.data.sqlite.AccountDao
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
suspend fun accountBalanceUpdateAll(
        transactionDao: TransactionDao,
        accountDao: AccountDao
) {
    withContext(Dispatchers.IO) {
        val accounts = accountDao.loadAllAccounts()
        accounts?.let {
            for (account in it) {
                val sum = transactionDao.loadSumByCheckId(account.id)
                accountDao.updateSumByAccId(account.id, sum)
            }
        }
    }
}

/** Math all user balance. */
suspend fun userBalanceUpdate(accountDao: AccountDao): Int {
    var balance = 0
    val accounts = accountDao.loadAllAccounts()
    accounts?.let {
        for (account in it) balance += account.sum
    }
    return balance
}

/** Math all user balance. */
fun userBalanceChange(accList: List<AccountListModel>): Int {
    var bal = 0
    for (account in accList) bal += account.sum
    return bal
}
