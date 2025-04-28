package com.capston.data.repository.remote.api

import com.capston.domain.response.lecture.DistinctLectureResponse
import retrofit2.http.GET

interface LectureApi {
    //강의 단 건 조회
    @GET("/v1/lectures")
    suspend fun getDistinctLecture(
    ): DistinctLectureResponse

    //강의 전체 조회
    @GET("/v1/lectures/all")
    suspend fun getAllLecture(
    ): DistinctLectureResponse
}