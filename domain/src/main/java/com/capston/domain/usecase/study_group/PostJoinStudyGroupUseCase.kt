package com.capston.domain.usecase.study_group

import com.capston.domain.repository.StudyGroupRepository
import com.capston.domain.request.JoinStudyGroupDto
import com.capston.domain.response.MessageResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class PostJoinStudyGroupUseCase @Inject constructor(
    private val repository: StudyGroupRepository
) {
    operator fun invoke(joinStudyGroupDto: JoinStudyGroupDto): Flow<MessageResponse> = flow {
        val response = repository.postJoinStudyGroup(joinStudyGroupDto)
        emit(response)
    }
}