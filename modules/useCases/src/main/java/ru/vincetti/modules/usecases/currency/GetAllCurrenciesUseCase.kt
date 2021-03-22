package ru.vincetti.modules.usecases.currency

import ru.vincetti.modules.core.models.Currency
import ru.vincetti.modules.database.repository.CurrencyRepo
import javax.inject.Inject

interface GetAllCurrenciesUseCase {

    suspend operator fun invoke(): List<Currency>
}

class GetAllCurrenciesUseCaseImpl @Inject constructor(
    private val currencyRepo: CurrencyRepo,
) : GetAllCurrenciesUseCase {

    override suspend operator fun invoke(): List<Currency> {
        return currencyRepo.loadAll()
    }
}
