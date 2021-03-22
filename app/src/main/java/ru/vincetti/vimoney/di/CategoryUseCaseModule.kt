package ru.vincetti.vimoney.di

import dagger.Binds
import dagger.Module
import dagger.Reusable
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.vincetti.modules.usecases.category.GetAllCategoriesUseCase
import ru.vincetti.modules.usecases.category.GetAllCategoriesUseCaseImpl
import ru.vincetti.modules.usecases.category.GetCategoryByIdUseCase
import ru.vincetti.modules.usecases.category.GetCategoryByIdUseCaseImpl

@InstallIn(SingletonComponent::class)
@Module
interface CategoryUseCaseModule {

    @Binds
    @Reusable
    fun bindGetAllCategoriesUseCase(impl: GetAllCategoriesUseCaseImpl): GetAllCategoriesUseCase

    @Binds
    @Reusable
    fun bindGetCategoryByIdUseCase(impl: GetCategoryByIdUseCaseImpl): GetCategoryByIdUseCase
}
