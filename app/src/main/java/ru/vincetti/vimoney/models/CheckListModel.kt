package ru.vincetti.vimoney.models

import androidx.lifecycle.LiveData
import ru.vincetti.modules.core.models.AccountList
import ru.vincetti.modules.usecases.account.GetAccountsUseCase
import javax.inject.Inject

class CheckListModel @Inject constructor(
    private val getAccountsUseCase: GetAccountsUseCase,
) {

    fun loadAllAccounts(): LiveData<List<AccountList>> = getAccountsUseCase()
}
