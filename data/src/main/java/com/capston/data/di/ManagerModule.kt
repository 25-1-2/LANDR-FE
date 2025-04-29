package com.capston.data.di

import com.capston.data.loading.LoadingStateManagerImpl
import com.capston.domain.manager.LoadingStateManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ManagerModule {
    @Binds
    @Singleton
    abstract fun bindLoadingStateManager(
        loadingStateManagerImpl: LoadingStateManagerImpl
    ): LoadingStateManager
}