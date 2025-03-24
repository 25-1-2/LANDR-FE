//package com.capston.data.di
//
//import dagger.Module
//import dagger.Provides
//import dagger.hilt.InstallIn
//import dagger.hilt.components.SingletonComponent
//import retrofit2.Retrofit
//import javax.inject.Singleton
//
//@Module
//@InstallIn(SingletonComponent::class)
//object ApiModule {
//    @Provides
//    @Singleton
//    fun provideLogInServer(
//        @MainRetrofit retrofit: Retrofit
//    ): LoginApi = retrofit.create(LoginApi::class.java)
//
//    @Provides
//    @Singleton
//    fun provideHomeServer(
//        @MainRetrofit retrofit: Retrofit
//    ): HomeApi = retrofit.create(HomeApi::class.java)
//}