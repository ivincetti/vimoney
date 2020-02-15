package ru.vincetti.vimoney.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.vincetti.vimoney.data.models.AccountListModel
import ru.vincetti.vimoney.data.sqlite.AccountDao
import ru.vincetti.vimoney.data.sqlite.TransactionDao

class LogicMath {

    companion object {
        // set correct account (accID) balance
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

        // Math allUser balance
        fun userBalanceChange(accList: List<AccountListModel>): Int {
            var bal = 0
            for (o in accList) bal += o.sum
            return bal
        }
    }
}
