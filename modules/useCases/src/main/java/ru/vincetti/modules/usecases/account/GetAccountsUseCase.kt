package ru.vincetti.modules.usecases.account

import androidx.lifecycle.LiveData
import ru.vincetti.modules.core.models.AccountList
import ru.vincetti.modules.database.repository.AccountRepo
import javax.inject.Inject

interface GetAccountsUseCase {

    operator fun invoke(): LiveData<List<AccountList>>
}

class GetAccountsUseCaseImpl @Inject constructor(
    private val accountRepo: AccountRepo,
) : GetAccountsUseCase {

    override operator fun invoke(): LiveData<List<AccountList>> {
        return accountRepo.loadAllFull()
    }
}
