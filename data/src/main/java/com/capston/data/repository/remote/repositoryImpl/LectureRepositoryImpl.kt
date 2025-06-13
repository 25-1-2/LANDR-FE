package com.capston.data.repository.remote.repositoryImpl

import com.capston.domain.datasource.LectureDataSource
import com.capston.domain.repository.LectureRepository
import com.capston.domain.request.LectureDto
import com.capston.domain.response.lecture.DistinctLectureResponse
import com.capston.domain.response.lecture.GetLessonsByLectureIdResponse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LectureRepositoryImpl @Inject constructor(
    private val lectureDataSource: LectureDataSource
) : LectureRepository {

    override suspend fun getDistinctLecture(lectureDto: LectureDto): Flow<DistinctLectureResponse> {
        return lectureDataSource.getDistinctLecture(lectureDto)
    }

    override suspend fun getAllLecture(lectureDto: LectureDto): Flow<DistinctLectureResponse> {
        return lectureDataSource.getAllLecture(lectureDto)
    }

    override suspend fun getLessonsByLectureId(lectureId: Int): GetLessonsByLectureIdResponse =
        lectureDataSource.getLessonsByLectureId(lectureId)
}
