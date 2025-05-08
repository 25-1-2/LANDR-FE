package com.capston.data.repository.remote.api

import com.capston.domain.model.NewPlanLesson
import com.capston.domain.response.lecture.DistinctLectureResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface LectureApi {
    //강의 단 건 조회
    @GET("/v1/lectures")
    suspend fun getDistinctLecture(
        @Query("search") search: String?,
        @Query("cursorLectureId") cursorLectureId: String?,
        @Query("cursorCreatedAt") cursorCreatedAt: String?,
        @Query("offset") offset: String?
    ): DistinctLectureResponse

    //강의 전체 조회
    @GET("/v1/lectures/all")
    suspend fun getAllLecture(
        @Query("search") search: String?,
        @Query("cursorLectureId") cursorLectureId: String?,
        @Query("cursorCreatedAt") cursorCreatedAt: String?,
        @Query("offset") offset: String?
    ): DistinctLectureResponse

    // lecture id로 lesson 목록 조회
    @GET("/v1/lectures/{lectureId}/lessons")
    suspend fun getLessonsByLectureId(
        @Path("lectureId") lectureId: Int
    ): List<NewPlanLesson>
}