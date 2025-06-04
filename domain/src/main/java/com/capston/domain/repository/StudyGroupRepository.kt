package com.capston.domain.repository

import com.capston.domain.request.JoinStudyGroupDto
import com.capston.domain.response.MessageResponse
import com.capston.domain.response.study_group.NewStudyGroupResponse
import com.capston.domain.response.study_group.OneStudyGroupResponse

interface StudyGroupRepository {
    // 스터디그룹 생성
    suspend fun postNewStudyGroup(
        planId: Int
    ): NewStudyGroupResponse

    // 스터디그룹 조회
    suspend fun getOneStudyGroup(
        studyGroupId: Int
    ): OneStudyGroupResponse

    // 스터디그룹 가입
    suspend fun postJoinStudyGroup(
        joinStudyGroupDto: JoinStudyGroupDto
    ): MessageResponse
}