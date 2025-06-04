package com.capston.domain.usecase.study_group

import com.capston.domain.repository.StudyGroupRepository
import com.capston.domain.response.study_group.OneStudyGroupResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class getOneStudyGroupUseCase @Inject constructor(
    private val repository: StudyGroupRepository
) {
    operator fun invoke(groupId: Int): Flow<OneStudyGroupResponse> = flow {
        val response = repository.getOneStudyGroup(groupId)
        emit(response)
    }
}