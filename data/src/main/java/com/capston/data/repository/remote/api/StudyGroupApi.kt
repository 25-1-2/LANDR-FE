package com.capston.data.repository.remote.api

import com.capston.domain.response.study_group.NewStudyGroupResponse
import com.capston.domain.response.study_group.OneStudyGroupResponse
import retrofit2.http.POST
import retrofit2.http.Path

interface StudyGroupApi {
    // 스터디그룹 생성
    @POST("/v1/study-groups/plans/{planId}")
    suspend fun postNewStudyGroup(
        @Path("planId") planId: Int,
    ): NewStudyGroupResponse

    // 스터디그룹 조회
    @POST("/v1/study-groups/plans/{planId}")
    suspend fun getOneStudyGroup(
        @Path("groupId") groupId: Int,
    ): OneStudyGroupResponse
}