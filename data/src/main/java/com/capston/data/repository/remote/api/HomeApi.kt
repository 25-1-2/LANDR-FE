package com.capston.data.repository.remote.api

import com.capston.domain.request.UpdateDDayRequest
import com.capston.domain.response.CheckResponse
import com.capston.domain.response.home.DDayResponse
import com.capston.domain.response.home.DistinctHomeIdResponse
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
    ): DistinctHomeIdResponse

    //홈 들은 강의 체크 수정
    @PATCH("/v1/lesson-schedules/{lessonScheduleId}/check/toggle")
    suspend fun patchLessonSchedulesCheckToggle(
        @Path("lessonScheduleId") lessonScheduleId: Int
    ): CheckResponse

    //디데이 생성
    @POST("/v1/d-day")
    suspend fun postDDay(
        @Body updateDDayRequest: UpdateDDayRequest
    ): DDayResponse

    //디데이 조회
    @GET("/v1/d-day/{dDayId}")
    suspend fun getDDay(
        @Path("dDayId") dDayId: Int
    ): DDayResponse

    //디데이 삭제
    @DELETE("/v1/d-day/{dDayId}")
    suspend fun deleteDDay(
        @Path("dDayId") dDayId: Int
    )

    //디데이 수정
    @PATCH("/v1/d-day/{dDayId}")
    suspend fun patchDDay(
        @Path("dDayId") dDayId: Int,
        @Body updateDDayRequest: UpdateDDayRequest
    ): DDayResponse
}