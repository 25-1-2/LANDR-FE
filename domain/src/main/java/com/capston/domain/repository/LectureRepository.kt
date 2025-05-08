package com.capston.domain.repository

import com.capston.domain.request.LectureDto
import com.capston.domain.response.lecture.DistinctLectureResponse
import com.capston.domain.response.lecture.GetLessonsByLectureIdResponse
import kotlinx.coroutines.flow.Flow

interface LectureRepository {
    // 강의 단 건 조회
    suspend fun getDistinctLecture(lectureDto: LectureDto): Flow<DistinctLectureResponse>

    // 강의 전체 조회
    suspend fun getAllLecture(lectureDto: LectureDto): Flow<DistinctLectureResponse>

    // lecture id로 lesson 목록 조회
    suspend fun getLessonsByLectureId(lectureId: Int): GetLessonsByLectureIdResponse
}