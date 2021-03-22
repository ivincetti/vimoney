package ru.vincetti.vimoney.models

import ru.vincetti.modules.core.models.Account
import ru.vincetti.modules.core.models.AccountList
import ru.vincetti.modules.usecases.account.ArchiveAccountByIdUseCase
import ru.vincetti.modules.usecases.account.GetListAccountByIdUseCase
import ru.vincetti.modules.usecases.account.UnArchiveAccountByIdUseCase
import ru.vincetti.modules.usecases.account.UpdateBalanceAccountByIdUseCase
import javax.inject.Inject

class CheckModel @Inject constructor(
    private val getListAccountByIdUseCase: GetListAccountByIdUseCase,
    private val archiveAccountByIdUseCase: ArchiveAccountByIdUseCase,
    private val unArchiveAccountByIdUseCase: UnArchiveAccountByIdUseCase,
    private val updateBalanceAccountByIdUseCase: UpdateBalanceAccountByIdUseCase,
) {

    private var accountId: Int = Account.DEFAULT_CHECK_ID

    fun setAccountId(id: Int) {
        accountId = id
    }

    suspend fun update() {
        updateBalanceAccountByIdUseCase(accountId)
    }

    suspend fun restore() {
        if (accountId != Account.DEFAULT_CHECK_ID) {
            unArchiveAccountByIdUseCase(accountId)
        }
    }

    suspend fun delete() {
        if (accountId != Account.DEFAULT_CHECK_ID) {
            archiveAccountByIdUseCase(accountId)
        }
    }

    suspend fun loadLiveAccountById(): AccountList? {
        return getListAccountByIdUseCase(accountId)
    }
}
