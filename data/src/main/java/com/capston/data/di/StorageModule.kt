// data/src/main/java/com/capston/data/di/StorageModule.kt
package com.capston.data.di

import android.content.Context
import com.capston.data.local.storage.OnboardingPreferenceStorageImpl
import com.capston.data.local.storage.RecommendationStorage
import com.capston.data.local.storage.TokenDataStore
import com.capston.data.repository.TokenRepositoryImpl
import com.capston.domain.datasource.OnboardingPreferenceStorage
import com.capston.domain.repository.TokenRepository
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object StorageModule {

    @Provides
    @Singleton
    fun provideTokenDataStore(@ApplicationContext context: Context): TokenDataStore {
        return TokenDataStore(context)
    }

    @Provides
    @Singleton
    fun provideTokenRepository(tokenDataStore: TokenDataStore): TokenRepository {
        return TokenRepositoryImpl(tokenDataStore)
    }

    @Provides
    @Singleton
    fun provideOnboardingPreferenceStorage(
        @ApplicationContext context: Context
    ): OnboardingPreferenceStorage {
        return OnboardingPreferenceStorageImpl(context)
    }


    // 로컬에 추천 결과 임시 저장
    @Provides
    @Singleton
    fun provideRecommendationStorage(
        @ApplicationContext context: Context,
        gson: Gson
    ): RecommendationStorage {
        return RecommendationStorage(context, gson)
    }
}