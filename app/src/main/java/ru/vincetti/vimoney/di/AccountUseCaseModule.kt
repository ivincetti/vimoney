package ru.vincetti.vimoney.di

import dagger.Binds
import dagger.Module
import dagger.Reusable
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.vincetti.modules.usecases.account.*

@InstallIn(SingletonComponent::class)
@Module
interface AccountUseCaseModule {

    @Binds
    @Reusable
    fun bindAddAccountUseCase(impl: AddAccountUseCaseImpl): AddAccountUseCase

    @Binds
    @Reusable
    fun bindUpdateAccountUseCase(impl: UpdateAccountUseCaseImpl): UpdateAccountUseCase

    @Binds
    @Reusable
    fun bindGetAccountsUseCase(impl: GetAccountsUseCaseImpl): GetAccountsUseCase

    @Binds
    @Reusable
    fun bindGetActiveAccountsUseCase(impl: GetActiveAccountsUseCaseImpl): GetActiveAccountsUseCase

    @Binds
    @Reusable
    fun bindGetAccountByIdUseCase(impl: GetAccountByIdUseCaseImpl): GetAccountByIdUseCase

    @Binds
    @Reusable
    fun bindGetLiveAccountByIdUseCase(impl: GetLiveAccountByIdUseCaseImpl): GetLiveAccountByIdUseCase

    @Binds
    @Reusable
    fun bindGetListAccountByIdUseCase(impl: GetListAccountByIdUseCaseImpl): GetListAccountByIdUseCase

    @Binds
    @Reusable
    fun bindArchiveAccountByIdUseCase(impl: ArchiveAccountByIdUseCaseImpl): ArchiveAccountByIdUseCase

    @Binds
    @Reusable
    fun bindUnArchiveAccountByIdUseCase(impl: UnArchiveAccountByIdUseCaseImpl): UnArchiveAccountByIdUseCase

    @Binds
    @Reusable
    fun bindUpdateBalanceAccountByIdUseCase(impl: UpdateBalanceAccountByIdUseCaseImpl): UpdateBalanceAccountByIdUseCase
}
