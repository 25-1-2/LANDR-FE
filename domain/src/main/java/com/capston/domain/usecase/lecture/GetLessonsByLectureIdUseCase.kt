package com.capston.domain.usecase.lecture

import com.capston.domain.repository.LectureRepository
import com.capston.domain.response.lecture.GetLessonsByLectureIdResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetLessonsByLectureIdUseCase @Inject constructor(
    private val repository: LectureRepository
) {
    operator fun invoke(lectureId: Int): Flow<GetLessonsByLectureIdResponse> = flow {
        val response = repository.getLessonsByLectureId(lectureId)
        emit(response)
    }
}