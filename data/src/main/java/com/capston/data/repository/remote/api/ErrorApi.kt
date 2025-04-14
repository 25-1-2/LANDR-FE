package com.capston.data.repository.remote.api

import retrofit2.http.GET
import com.capston.domain.base.Result

interface ErrorApi {
    //API 예외 발생
    @GET("/v1/exception/api")
    suspend fun getExceptionApi(
    ): Result
}