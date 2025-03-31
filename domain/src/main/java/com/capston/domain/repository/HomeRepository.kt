package com.capston.domain.repository

import com.capston.domain.response.CheckResponse
import com.capston.domain.response.home.DistinctHomeIdResponse
import kotlinx.coroutines.flow.Flow

interface HomeRepository {
    //홈 단 건 조회
    suspend fun getDistinctHome(): Flow<DistinctHomeIdResponse>

    // 홈 들은 강의 체크 수정
    suspend fun patchLessonSchedulesCheckToggle(lessonScheduleId: Int): Flow<CheckResponse>
}