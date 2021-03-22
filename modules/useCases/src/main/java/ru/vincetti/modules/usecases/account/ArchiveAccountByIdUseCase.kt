package ru.vincetti.modules.usecases.account

import ru.vincetti.modules.database.repository.AccountRepo
import javax.inject.Inject

interface ArchiveAccountByIdUseCase {

    suspend operator fun invoke(accountId: Int)
}

class ArchiveAccountByIdUseCaseImpl @Inject constructor(
    private val accountRepo: AccountRepo,
) : ArchiveAccountByIdUseCase {

    override suspend operator fun invoke(accountId: Int) {
        return accountRepo.archiveById(accountId)
    }
}
