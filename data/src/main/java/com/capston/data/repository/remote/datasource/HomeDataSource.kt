package com.capston.data.repository.remote.datasource

import com.capston.data.repository.remote.response.BaseResponse
import com.capston.data.repository.remote.response.DistinctHomeIdResponse
import kotlinx.coroutines.flow.Flow

interface HomeDataSource {
    //홈 단 건 조회
    suspend fun getDistinctHome(): Flow<BaseResponse<DistinctHomeIdResponse>>
}