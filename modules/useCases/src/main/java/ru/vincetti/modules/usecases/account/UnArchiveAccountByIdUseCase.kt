package ru.vincetti.modules.usecases.account

import ru.vincetti.modules.database.repository.AccountRepo
import javax.inject.Inject

interface UnArchiveAccountByIdUseCase {

    suspend operator fun invoke(accountId: Int)
}

class UnArchiveAccountByIdUseCaseImpl @Inject constructor(
    private val accountRepo: AccountRepo,
) : UnArchiveAccountByIdUseCase {

    override suspend operator fun invoke(accountId: Int) {
        return accountRepo.unArchiveById(accountId)
    }
}
