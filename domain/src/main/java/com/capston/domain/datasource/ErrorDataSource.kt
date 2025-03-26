package com.capston.domain.datasource

import kotlinx.coroutines.flow.Flow
import com.capston.domain.base.Result

interface ErrorDataSource {
    // API 예외 발생
    suspend fun getExceptionApi(): Flow<Result>
}