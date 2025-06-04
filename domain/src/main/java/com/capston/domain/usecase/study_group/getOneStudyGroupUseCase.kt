package com.capston.domain.usecase.study_group

import com.capston.domain.repository.StudyGroupRepository
import com.capston.domain.response.study_group.OneStudyGroupResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetOneStudyGroupUseCase @Inject constructor(
    private val repository: StudyGroupRepository
) {
    operator fun invoke(studyGroupId: Int): Flow<OneStudyGroupResponse> = flow {
        val response = repository.getOneStudyGroup(studyGroupId)
        emit(response)
    }
}