// domain/src/main/java/com/capston/domain/repository/TokenRepository.kt
package com.capston.domain.repository

import kotlinx.coroutines.flow.Flow

interface TokenRepository {
    suspend fun saveAccessToken(token: String)
    fun getAccessToken(): Flow<String?>
    suspend fun clearTokens()
}