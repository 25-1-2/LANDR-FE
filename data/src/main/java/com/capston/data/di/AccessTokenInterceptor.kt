package com.capston.data.di

import com.capston.data.local.storage.TokenDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import javax.inject.Inject

class AccessTokenInterceptor @Inject constructor(
    private val tokenDataStore: TokenDataStore
) : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response = with(chain) {
        // runBlocking을 사용하여 Flow에서 최신 토큰 값을 가져옴
        val token = runBlocking { tokenDataStore.accessToken.first() }

        // 토큰이 있는 경우에만 헤더에 추가
        val newRequest = if (token != null) {
            request().newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            request()
        }

        proceed(newRequest)
    }
}