package com.capston.data.di

import com.capston.data.repository.remote.api.ErrorApi
import com.capston.data.repository.remote.api.HomeApi
import com.capston.data.repository.remote.api.PlanApi
import com.capston.data.repository.remote.datasourcelmpl.ErrorDataSourceImpl
import com.capston.data.repository.remote.datasourcelmpl.HomeDataSourceImpl
import com.capston.data.repository.remote.datasourcelmpl.PlanDataSourceImpl
import com.capston.data.repository.remote.repositoryImpl.ErrorRepositoryImpl
import com.capston.data.repository.remote.repositoryImpl.HomeRepositoryImpl
import com.capston.data.repository.remote.repositoryImpl.PlanRepositoryImpl
import com.capston.domain.datasource.ErrorDataSource
import com.capston.domain.datasource.HomeDataSource
import com.capston.domain.datasource.PlanDataSource
import com.capston.domain.repository.ErrorRepository
import com.capston.domain.repository.HomeRepository
import com.capston.domain.repository.PlanRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    // 홈
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

    // 에러
    @Provides
    @Singleton
    fun provideErrorDataSource(
        errorApi: ErrorApi
    ): ErrorDataSource {
        return ErrorDataSourceImpl(errorApi)
    }

    @Singleton
    @Provides
    fun provideErrorRepository(errorDataSource: ErrorDataSource): ErrorRepository =
        ErrorRepositoryImpl(errorDataSource)

    // 계획

    @Provides
    @Singleton
    fun providePlanDataSource(
        planApi: PlanApi
    ): PlanDataSource {
        return PlanDataSourceImpl(planApi)
    }

    @Singleton
    @Provides
    fun providePlanRepository(planDataSource: PlanDataSource): PlanRepository =
        PlanRepositoryImpl(planDataSource)
}