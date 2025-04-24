package com.capston.data.di

import com.capston.data.loading.LoadingManager
import com.capston.data.repository.remote.api.DailyScheduleApi
import com.capston.data.repository.remote.api.ErrorApi
import com.capston.data.repository.remote.api.HomeApi
import com.capston.data.repository.remote.api.LoginApi
import com.capston.data.repository.remote.api.PlanApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {
    @Provides
    @Singleton
    fun provideLogInServer(
        @MainRetrofit retrofit: Retrofit
    ): LoginApi = retrofit.create(LoginApi::class.java)

    @Provides
    @Singleton
    fun provideHomeServer(
        @MainRetrofit retrofit: Retrofit
    ): HomeApi = retrofit.create(HomeApi::class.java)

    @Provides
    @Singleton
    fun provideErrorServer(
        @MainRetrofit retrofit: Retrofit
    ) : ErrorApi = retrofit.create(ErrorApi::class.java)

    @Provides
    @Singleton
    fun providePlanServer(
        @MainRetrofit retrofit: Retrofit
    ) : PlanApi = retrofit.create(PlanApi::class.java)

    @Provides
    @Singleton
    fun provideDailyScheduleServer(
        @MainRetrofit retrofit: Retrofit
    ) : DailyScheduleApi = retrofit.create(DailyScheduleApi::class.java)
}