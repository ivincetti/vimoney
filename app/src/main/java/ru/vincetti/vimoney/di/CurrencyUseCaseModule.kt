package ru.vincetti.vimoney.di

import dagger.Binds
import dagger.Module
import dagger.Reusable
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.vincetti.modules.usecases.currency.*

@InstallIn(SingletonComponent::class)
@Module
interface CurrencyUseCaseModule {

    @Binds
    @Reusable
    fun bindGetAllCurrenciesUseCase(impl: GetAllCurrenciesUseCaseImpl): GetAllCurrenciesUseCase

    @Binds
    @Reusable
    fun bindGetLiveCurrencySymbolByCodeUseCase(impl: GetLiveCurrencySymbolByCodeUseCaseImpl): GetLiveCurrencySymbolByCodeUseCase

    @Binds
    @Reusable
    fun bindGetCurrencyByCodeUseCase(impl: GetCurrencyByCodeUseCaseImpl): GetCurrencyByCodeUseCase
}
