package com.capston.domain.repository

import kotlinx.coroutines.flow.Flow
import com.capston.domain.base.Result

interface ErrorRepository {
    suspend fun getExceptionApi(): Flow<Result>
}