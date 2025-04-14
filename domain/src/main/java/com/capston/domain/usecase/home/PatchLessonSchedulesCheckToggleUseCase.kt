package com.capston.domain.usecase.home

import com.capston.domain.repository.HomeRepository
import com.capston.domain.response.CheckResponse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PatchLessonSchedulesCheckToggleUseCase @Inject constructor(
    private val homeRepository: HomeRepository
) {
    suspend operator fun invoke(
        lessonScheduleId: Int
    ): Flow<CheckResponse> {
        return homeRepository.patchLessonSchedulesCheckToggle(lessonScheduleId)
    }
}