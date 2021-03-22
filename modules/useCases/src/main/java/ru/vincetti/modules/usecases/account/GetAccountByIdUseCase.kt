package ru.vincetti.modules.usecases.account

import ru.vincetti.modules.core.models.Account
import ru.vincetti.modules.database.repository.AccountRepo
import javax.inject.Inject

interface GetAccountByIdUseCase {

    suspend operator fun invoke(accountId: Int): Account?
}

class GetAccountByIdUseCaseImpl @Inject constructor(
    private val accountRepo: AccountRepo,
) : GetAccountByIdUseCase {

    override suspend operator fun invoke(accountId: Int): Account? {
        return accountRepo.loadById(accountId)
    }
}
