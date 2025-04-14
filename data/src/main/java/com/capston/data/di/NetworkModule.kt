package com.capston.data.di

import android.util.Log
import com.capston.data.BuildConfig
import com.capston.data.local.storage.TokenDataStore
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Named
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val BASE_URL = BuildConfig.BASE_URL

    @Singleton
    @Provides
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Singleton
    @Provides
    fun provideGson(): Gson {
        return GsonBuilder().setLenient().create() // setLenient() 추가
    }

    @Provides
    @Singleton
    fun provideAccessTokenInterceptor(tokenDataStore: TokenDataStore): AccessTokenInterceptor {
        return AccessTokenInterceptor(tokenDataStore)
    }

    @Provides
    @Singleton
    fun provideConverterFactory(): GsonConverterFactory = GsonConverterFactory.create()

    @Singleton
    @Provides
    @Named("defaultOkHttpClient")
    fun provideOkHttpClient(
        httpLoggingInterceptor: HttpLoggingInterceptor,
        accessTokenInterceptor: AccessTokenInterceptor,
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(150, TimeUnit.SECONDS)
            .readTimeout(150, TimeUnit.SECONDS)
            .writeTimeout(150, TimeUnit.SECONDS)
            .addInterceptor(httpLoggingInterceptor)
            .addInterceptor(accessTokenInterceptor)
            .addInterceptor{ chain ->
                val request = chain.request()
                Log.d("NetworkModule", "Headers: ${request.headers}")
                chain.proceed(request)
            }
//            .addInterceptor(accessTokenInterceptor)
            .build()
    }

    @MainRetrofit
    @Singleton
    @Provides
    fun provideMainRetrofit(
        @Named("defaultOkHttpClient") okHttpClient: OkHttpClient,
        gsonConverterFactory: GsonConverterFactory
    ): Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(gsonConverterFactory)
            .client(okHttpClient)
            .baseUrl(BASE_URL)
            .build()
    }
}
