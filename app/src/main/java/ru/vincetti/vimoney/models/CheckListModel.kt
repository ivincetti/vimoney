package ru.vincetti.vimoney.models

import androidx.lifecycle.LiveData
import ru.vincetti.modules.core.models.AccountList
import ru.vincetti.modules.database.repository.AccountRepo
import javax.inject.Inject

class CheckListModel @Inject constructor(
    private val accountRepo: AccountRepo,
) {

    fun loadAllAccounts(): LiveData<List<AccountList>> = accountRepo.loadAllFull()
}
