package ru.vincetti.vimoney.models

import androidx.lifecycle.LiveData
import ru.vincetti.modules.core.models.AccountList
import ru.vincetti.modules.database.repository.AccountRepo
import ru.vincetti.modules.database.repository.TransactionRepo
import javax.inject.Inject

class CheckModel @Inject constructor(
    private val accountRepo: AccountRepo,
    private val transactionRepo: TransactionRepo,
) {

    fun loadLiveAccountById(id: Int): LiveData<AccountList?> = accountRepo.loadLiveForListById(id)

    suspend fun archiveAccountById(id: Int) = accountRepo.archiveById(id)

    suspend fun unArchiveAccountById(id: Int) = accountRepo.unArchiveById(id)

    suspend fun accountBalanceUpdateById(accId: Int) {
        val accountBalance = transactionRepo.loadBalanceByCheckId(accId)
        accountRepo.updateAccountBalanceById(accId, accountBalance)
    }
}
