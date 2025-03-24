package com.capston.data.repository.remote.api

import com.capston.domain.response.BaseResponse
import com.capston.domain.response.DistinctHomeIdResponse
import retrofit2.http.GET

interface HomeApi {

    //홈 단 건 조회
    @GET("/v1/home")
    suspend fun getDistinctHome(
    ): BaseResponse<DistinctHomeIdResponse>
}