package com.capston.domain.usecase.error

import com.capston.domain.repository.ErrorRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import com.capston.domain.base.Result

class GetExceptionApiUseCase @Inject constructor(
    private val errorRepository: ErrorRepository
) {
    suspend operator fun invoke(): Flow<Result> {
        return errorRepository.getExceptionApi()
    }
}