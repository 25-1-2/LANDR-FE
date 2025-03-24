package com.capston.data.di

import com.capston.data.repository.remote.api.HomeApi
import com.capston.data.repository.remote.datasourcelmpl.HomeDataSourceImpl
import com.capston.data.repository.remote.repository.HomeRepositoryImpl
import com.capston.domain.datasource.HomeDataSource
import com.capston.domain.repository.HomeRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideHomeDataSource(
        homeApi: HomeApi
    ): HomeDataSource {
        return HomeDataSourceImpl(homeApi)
    }

    @Singleton
    @Provides
    fun provideHomeRepository(homeDataSource: HomeDataSource): HomeRepository =
        HomeRepositoryImpl(homeDataSource)
}