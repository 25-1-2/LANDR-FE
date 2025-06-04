package com.capston.domain.repository

import com.capston.domain.response.study_group.NewStudyGroupResponse
import com.capston.domain.response.study_group.OneStudyGroupResponse

interface StudyGroupRepository {
    // 스터디그룹 생성
    suspend fun postNewStudyGroup(
        planId: Int
    ): NewStudyGroupResponse

    // 스터디그룹 조회
    suspend fun getOneStudyGroup(
        groupId: Int
    ): OneStudyGroupResponse
}