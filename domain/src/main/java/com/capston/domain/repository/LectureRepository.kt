package com.capston.domain.repository

import com.capston.domain.request.LectureDto
import com.capston.domain.response.CheckResponse
import com.capston.domain.response.lecture.DistinctLectureResponse
import kotlinx.coroutines.flow.Flow

interface LectureRepository {
    // 강의 단 건 조회
    suspend fun getDistinctLecture(lectureDto: LectureDto): Flow<DistinctLectureResponse>

    // 강의 전체 조회
    suspend fun getAllLecture(): Flow<DistinctLectureResponse>
}