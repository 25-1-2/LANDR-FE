package com.capston.domain.datasource

import com.capston.domain.response.BaseResponse
import com.capston.domain.response.DistinctHomeIdResponse
import kotlinx.coroutines.flow.Flow

interface HomeDataSource {
    //홈 단 건 조회
    suspend fun getDistinctHome(): Flow<DistinctHomeIdResponse>
}