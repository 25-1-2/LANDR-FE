package com.capston.data.repository

import com.capston.data.local.storage.TokenDataStore
import com.capston.domain.repository.TokenRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TokenRepositoryImpl @Inject constructor(
    private val tokenDataStore: TokenDataStore
) : TokenRepository {

    override suspend fun saveAccessToken(token: String) {
        tokenDataStore.saveAccessToken(token)
    }

    override fun getAccessToken(): Flow<String?> = tokenDataStore.accessToken

    override suspend fun clearTokens() {
        tokenDataStore.clearTokens()
    }
}