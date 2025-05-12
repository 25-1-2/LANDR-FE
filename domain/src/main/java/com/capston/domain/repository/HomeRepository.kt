package com.capston.domain.repository

import com.capston.domain.request.UpdateDDayRequest
import com.capston.domain.response.CheckResponse
import com.capston.domain.response.home.DDayResponse
import com.capston.domain.response.home.DistinctHomeIdResponse
import kotlinx.coroutines.flow.Flow
import retrofit2.http.Body

interface HomeRepository {
    //홈 단 건 조회
    suspend fun getDistinctHome(): Flow<DistinctHomeIdResponse>

    // 홈 들은 강의 체크 수정
    suspend fun patchLessonSchedulesCheckToggle(lessonScheduleId: Int): Flow<CheckResponse>

    // 디데이 생성
    suspend fun postDDay(updateDDayRequest: UpdateDDayRequest): Flow<DDayResponse>

    // 디데이 조회
    suspend fun getDDay(dDayId: Int): Flow<DDayResponse>

    // 디데이 삭제
    suspend fun deleteDDay(dDayId: Int)

    // 디데이 수정
    suspend fun patchDDay(dDayId: Int, updateDDayRequest: UpdateDDayRequest): Flow<DDayResponse>
}