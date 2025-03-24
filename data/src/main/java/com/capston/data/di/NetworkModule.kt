package com.capston.data.di

import android.util.Log
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Named
import jakarta.inject.Qualifier
import jakarta.inject.Singleton
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class MainRetrofit

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    val BaseUrl = "http://211.188.52.64:8080"

    @Singleton
    @Provides
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

//    @Singleton
//    @Provides
//    fun provideAccessTokenInterceptor(tokenManager: TokenManager): AccessTokenInterceptor {
//        return AccessTokenInterceptor(tokenManager)
//    }

    @Provides
    @Singleton
    fun provideConverterFactory(): GsonConverterFactory = GsonConverterFactory.create()

    @Singleton
    @Provides
    @Named("defaultOkHttpClient")
    fun provideOkHttpClient(
        httpLoggingInterceptor: HttpLoggingInterceptor,
//        accessTokenInterceptor: AccessTokenInterceptor,
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(httpLoggingInterceptor)
            .addInterceptor{ chain ->
                val request = chain.request()
                Log.d("토큰", "Headers: ${request.headers}")
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
            .addConverterFactory(gsonConverterFactory)
            .client(okHttpClient)
            .baseUrl(BaseUrl)
            .build()
    }
}
