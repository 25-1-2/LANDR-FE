package com.capston.domain.usecase.lecture

import com.capston.domain.repository.LectureRepository
import com.capston.domain.request.LectureDto
import com.capston.domain.response.lecture.DistinctLectureResponse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetDistinctLectureUseCase @Inject constructor(
    private val lectureRepository: LectureRepository
) {
    suspend operator fun invoke(searchName: String): Flow<DistinctLectureResponse> {
        return lectureRepository.getDistinctLecture(searchName)
    }
}