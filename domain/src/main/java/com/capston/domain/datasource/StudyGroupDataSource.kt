package com.capston.domain.datasource

import com.capston.domain.response.study_group.NewStudyGroupResponse

interface StudyGroupDataSource {
    // 스터디그룹 생성
    suspend fun postNewStudyGroup(
        planId: Int,
    ): NewStudyGroupResponse
}