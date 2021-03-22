package ru.vincetti.vimoney.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.vincetti.modules.network.ConfigLoader
import ru.vincetti.modules.network.ConfigLoaderImpl
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
abstract class NetworkModule {

    @Binds
    @Singleton
    abstract fun bindsConfigLoader(impl: ConfigLoaderImpl): ConfigLoader
}
