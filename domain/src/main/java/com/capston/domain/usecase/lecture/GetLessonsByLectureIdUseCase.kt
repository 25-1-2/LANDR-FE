package com.capston.domain.usecase.lecture

import com.capston.domain.model.NewPlanLesson
import com.capston.domain.repository.LectureRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetLessonsByLectureIdUseCase @Inject constructor(
    private val repository: LectureRepository
) {
    operator fun invoke(lectureId: Int): Flow<List<NewPlanLesson>> = flow {
        val response = repository.getLessonsByLectureId(lectureId)
        emit(response)  // JSON 변환 없이 문자열 그대로 emit
    }
}