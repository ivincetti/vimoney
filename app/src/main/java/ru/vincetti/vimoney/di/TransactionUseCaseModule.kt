package ru.vincetti.vimoney.di

import dagger.Binds
import dagger.Module
import dagger.Reusable
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.vincetti.modules.usecases.transaction.*

@InstallIn(SingletonComponent::class)
@Module
interface TransactionUseCaseModule {

    @Binds
    @Reusable
    fun bindGetTransactionByIdUseCase(impl: GetTransactionByIdUseCaseImpl): GetTransactionByIdUseCase

    @Binds
    @Reusable
    fun bindAddTransactionUseCase(impl: AddTransactionUseCaseImpl): AddTransactionUseCase

    @Binds
    @Reusable
    fun bindUpdateTransactionUseCase(impl: UpdateTransactionUseCaseImpl): UpdateTransactionUseCase

    @Binds
    @Reusable
    fun bindDeleteTransactionUseCase(impl: DeleteTransactionUseCaseImpl): DeleteTransactionUseCase
}
