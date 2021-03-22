package ru.vincetti.modules.usecases.currency

import ru.vincetti.modules.core.models.Currency
import ru.vincetti.modules.database.repository.CurrencyRepo
import javax.inject.Inject

interface GetCurrencyByCodeUseCase {

    suspend operator fun invoke(code: Int): Currency?
}

class GetCurrencyByCodeUseCaseImpl @Inject constructor(
    private val currencyRepo: CurrencyRepo,
) : GetCurrencyByCodeUseCase {

    override suspend operator fun invoke(code: Int): Currency? {
        return currencyRepo.loadByCode(code)
    }
}
