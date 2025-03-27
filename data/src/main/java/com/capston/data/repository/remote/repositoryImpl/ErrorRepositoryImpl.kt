package com.capston.data.repository.remote.repositoryImpl

import com.capston.domain.base.Result
import com.capston.domain.datasource.ErrorDataSource
import com.capston.domain.repository.ErrorRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ErrorRepositoryImpl @Inject constructor(
    private val errorDataSource: ErrorDataSource
) : ErrorRepository {

    override suspend fun getExceptionApi(): Flow<Result> {
        return errorDataSource.getExceptionApi()
    }
}