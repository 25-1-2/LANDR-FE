package com.capston.domain.datasource

import com.capston.domain.response.BaseResponse
import com.capston.domain.response.home.DistinctHomeIdResponse
import kotlinx.coroutines.flow.Flow

interface HomeDataSource {
    //홈 단 건 조회
    suspend fun getDistinctHome(): Flow<DistinctHomeIdResponse>

    // 홈 들은 강의 체크 수정
    suspend fun patchLessonSchedulesCheckToggle(lessonScheduleId: Int): Flow<BaseResponse<Any>>
}