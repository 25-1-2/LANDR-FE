package com.capston.domain.usecase.lecture

import com.capston.domain.repository.LectureRepository
import com.capston.domain.response.lecture.DistinctLectureResponse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllLectureUseCase @Inject constructor(
    private val lectureRepository: LectureRepository
) {
    suspend operator fun invoke(): Flow<DistinctLectureResponse> {
        return lectureRepository.getAllLecture()
    }
}