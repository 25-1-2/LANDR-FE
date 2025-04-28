package com.capston.data.repository.remote.repositoryImpl

import com.capston.domain.datasource.LectureDataSource
import com.capston.domain.repository.LectureRepository
import com.capston.domain.response.CheckResponse
import com.capston.domain.response.lecture.DistinctLectureResponse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LectureRepositoryImpl @Inject constructor(
    private val lectureDataSource: LectureDataSource
) : LectureRepository {

    override suspend fun getDistinctLecture(): Flow<DistinctLectureResponse> {
        return lectureDataSource.getDistinctLecture()
    }

    override suspend fun getAllLecture(): Flow<DistinctLectureResponse> {
        return lectureDataSource.getAllLecture()
    }
}
