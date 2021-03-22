package ru.vincetti.modules.usecases.account

import androidx.lifecycle.LiveData
import ru.vincetti.modules.core.models.AccountList
import ru.vincetti.modules.database.repository.AccountRepo
import javax.inject.Inject

interface GetLiveAccountByIdUseCase {

    operator fun invoke(accountId: Int): LiveData<AccountList?>
}

class GetLiveAccountByIdUseCaseImpl @Inject constructor(
    private val accountRepo: AccountRepo,
) : GetLiveAccountByIdUseCase {

    override operator fun invoke(accountId: Int): LiveData<AccountList?> {
        return accountRepo.loadLiveForListById(accountId)
    }
}
