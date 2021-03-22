package ru.vincetti.modules.usecases.currency

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import ru.vincetti.modules.core.models.Currency
import ru.vincetti.modules.database.repository.CurrencyRepo
import javax.inject.Inject

interface GetLiveCurrencySymbolByCodeUseCase {

    operator fun invoke(code: Int): LiveData<String>
}

class GetLiveCurrencySymbolByCodeUseCaseImpl @Inject constructor(
    private val currencyRepo: CurrencyRepo,
) : GetLiveCurrencySymbolByCodeUseCase {

    override operator fun invoke(code: Int): LiveData<String> {
        return currencyRepo.loadLiveSymbolByCode(code).map { it ?: Currency.DEFAULT_CURRENCY_SYMBOL }
    }
}
