package com.capston.data.di

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class AppInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain) : Response = with(chain) {
        val newRequest = request().newBuilder()
            .addHeader("(header Key)", "(header Value)")
            .build()
        proceed(newRequest)
    }
}