package com.capston.data.repository.remote.api

import com.capston.domain.response.CheckResponse
import com.capston.domain.response.home.DistinctHomeIdResponse
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path

interface HomeApi {
    //홈 단 건 조회
    @GET("/v1/home")
    suspend fun getDistinctHome(
    ): DistinctHomeIdResponse

    //홈 들은 강의 체크 수정
    @PATCH("/v1/lesson-schedules/{lessonScheduleId}/check/toggle")
    suspend fun patchLessonSchedulesCheckToggle(
        @Path("lessonScheduleId") lessonScheduleId: Int
    ): CheckResponse
}