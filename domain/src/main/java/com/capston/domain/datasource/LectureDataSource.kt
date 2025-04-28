package com.capston.domain.datasource

import com.capston.domain.request.LectureDto
import com.capston.domain.response.CheckResponse
import com.capston.domain.response.home.DistinctHomeIdResponse
import com.capston.domain.response.lecture.DistinctLectureResponse
import kotlinx.coroutines.flow.Flow

interface LectureDataSource {
    //강의 단 건 조회
    suspend fun getDistinctLecture(lectureDto: LectureDto): Flow<DistinctLectureResponse>

    // 홈 들은 강의 체크 수정
    suspend fun getAllLecture(): Flow<DistinctLectureResponse>
}