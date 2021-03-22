package ru.vincetti.modules.usecases.account

import ru.vincetti.modules.core.models.AccountList
import ru.vincetti.modules.database.repository.AccountRepo
import javax.inject.Inject

interface GetListAccountByIdUseCase {

    suspend operator fun invoke(accountId: Int): AccountList?
}

class GetListAccountByIdUseCaseImpl @Inject constructor(
    private val accountRepo: AccountRepo,
) : GetListAccountByIdUseCase {

    override suspend operator fun invoke(accountId: Int): AccountList? {
        return accountRepo.loadForListById(accountId)
    }
}
