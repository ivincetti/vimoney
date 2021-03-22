package ru.vincetti.modules.usecases.account

import ru.vincetti.modules.core.models.Account
import ru.vincetti.modules.database.repository.AccountRepo
import javax.inject.Inject

interface UpdateAccountUseCase {

    suspend operator fun invoke(account: Account)
}

class UpdateAccountUseCaseImpl @Inject constructor(
    private val accountRepo: AccountRepo,
) : UpdateAccountUseCase {

    override suspend operator fun invoke(account: Account) {
        return accountRepo.update(account)
    }
}
