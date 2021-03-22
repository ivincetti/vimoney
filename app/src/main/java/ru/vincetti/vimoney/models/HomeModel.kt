package ru.vincetti.vimoney.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import ru.vincetti.modules.core.models.AccountList
import ru.vincetti.modules.database.repository.AccountRepo
import ru.vincetti.modules.database.repository.TransactionRepo
import javax.inject.Inject

class HomeModel @Inject constructor(
    private val accountRepo: AccountRepo,
    private val transactionRepo: TransactionRepo,
) {

    private val allAccounts: LiveData<List<AccountList>> = accountRepo.loadAllFull()

    fun mainAccounts(): LiveData<List<AccountList>> = allAccounts.map { list ->
        list.filter { it.needOnMain }
    }

    fun userBalance(): LiveData<Int> = allAccounts.map { list ->
        var balance = 0
        val balanceList = list.filter { it.needAllBalance && it.isArchive.not() }

        balanceList.forEach { acc ->
            balance += acc.sum
        }
        balance
    }

    fun loadIncomeActual(): LiveData<Int> = transactionRepo.loadIncomeMonthActualLive()

    fun loadExpenseActual(): LiveData<Int> = transactionRepo.loadExpenseMonthActualLive()

    suspend fun balancesUpdate() {
        accountRepo.loadAll()?.forEach {
            val sum = transactionRepo.loadBalanceByCheckId(it.id)
            accountRepo.updateAccountBalanceById(it.id, sum)
        }
    }
}
