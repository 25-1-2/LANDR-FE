package com.capston.domain.usecase.study_group

import com.capston.domain.repository.StudyGroupRepository
import com.capston.domain.response.study_group.NewStudyGroupResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class PostNewStudyGroupUseCase @Inject constructor(
    private val repository: StudyGroupRepository
) {
    operator fun invoke(planId: Int): Flow<NewStudyGroupResponse> = flow {
        val response = repository.postNewStudyGroup(planId)
        emit(response)
    }
}