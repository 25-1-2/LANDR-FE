package com.capston.data.repository.remote.api

import com.capston.data.repository.remote.response.BaseResponse
import com.capston.data.repository.remote.response.DistinctHomeIdResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface HomeApi {

    //홈 단 건 조회
    @GET("/v1/home")
    suspend fun getDistinctHome(
    ): BaseResponse<DistinctHomeIdResponse>
}