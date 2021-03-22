package ru.vincetti.modules.usecases.account

import ru.vincetti.modules.core.models.Account
import ru.vincetti.modules.database.repository.AccountRepo
import javax.inject.Inject

interface AddAccountUseCase {

    suspend operator fun invoke(account: Account)
}

class AddAccountUseCaseImpl @Inject constructor(
    private val accountRepo: AccountRepo,
) : AddAccountUseCase {

    override suspend operator fun invoke(account: Account) {
        return accountRepo.add(account)
    }
}
