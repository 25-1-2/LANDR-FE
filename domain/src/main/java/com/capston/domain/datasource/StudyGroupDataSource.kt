package com.capston.domain.datasource

import com.capston.domain.response.study_group.NewStudyGroupResponse
import com.capston.domain.response.study_group.OneStudyGroupResponse

interface StudyGroupDataSource {
    // 스터디그룹 생성
    suspend fun postNewStudyGroup(
        planId: Int,
    ): NewStudyGroupResponse

    // 스터디그룹 조회
    suspend fun getOneStudyGroup(
        studyGroupId: Int,
    ): OneStudyGroupResponse
}