package com.capston.data.repository.remote.api

import com.capston.domain.response.enum_class.Platform
import com.capston.domain.response.enum_class.Subject
import com.capston.domain.response.lecture.DistinctLectureResponse
import com.capston.domain.response.lecture.GetLessonsByLectureIdResponse
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
        @Query("offset") offset: String?,
        @Query("platform") platform: Platform? = null,
        @Query("subject") subject: Subject? = null
    ): DistinctLectureResponse

    //강의 전체 조회
    @GET("/v1/lectures/all")
    suspend fun getAllLecture(
        @Query("search") search: String?,
        @Query("cursorLectureId") cursorLectureId: String?,
        @Query("cursorCreatedAt") cursorCreatedAt: String?,
        @Query("offset") offset: String?,
        @Query("platform") platform: Platform? = null,
        @Query("subject") subject: Subject? = null
    ): DistinctLectureResponse

    // lecture id로 lesson 목록 조회
    @GET("/v1/lectures/{lectureId}/lessons")
    suspend fun getLessonsByLectureId(
        @Path("lectureId") lectureId: Int
    ): GetLessonsByLectureIdResponse
}