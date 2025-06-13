package com.capston.domain.datasource

import com.capston.domain.request.LectureDto
import com.capston.domain.response.lecture.DistinctLectureResponse
import com.capston.domain.response.lecture.GetLessonsByLectureIdResponse
import kotlinx.coroutines.flow.Flow

interface LectureDataSource {
    // 강의 단 건 조회
    suspend fun getDistinctLecture(lectureDto: LectureDto): Flow<DistinctLectureResponse>

    // 홈 들은 강의 체크 수정
    suspend fun getAllLecture(lectureDto: LectureDto): Flow<DistinctLectureResponse>

    // lecture id로 lesson 목록 조회
    suspend fun getLessonsByLectureId(lectureId: Int): GetLessonsByLectureIdResponse
}