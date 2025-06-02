package com.capston.domain.repository

import com.capston.domain.response.study_group.NewStudyGroupResponse

interface StudyGroupRepository {
    // 재스케줄링
    suspend fun postNewStudyGroup(
        planId: Int
    ): NewStudyGroupResponse
}