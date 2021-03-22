package ru.vincetti.modules.usecases.account

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import ru.vincetti.modules.core.models.AccountList
import ru.vincetti.modules.database.repository.AccountRepo
import javax.inject.Inject

interface GetActiveAccountsUseCase {

    operator fun invoke(): LiveData<List<AccountList>>
}

class GetActiveAccountsUseCaseImpl @Inject constructor(
    private val accountRepo: AccountRepo,
) : GetActiveAccountsUseCase {

    override operator fun invoke(): LiveData<List<AccountList>> {
        return accountRepo.loadAllFull().map { list ->
            list.filter { it.isArchive.not() }
        }
    }
}
