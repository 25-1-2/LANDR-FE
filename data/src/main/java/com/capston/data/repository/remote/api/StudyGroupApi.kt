package com.capston.data.repository.remote.api

import com.capston.domain.request.LoginDto
import com.capston.domain.response.MessageResponse
import com.capston.domain.response.study_group.NewStudyGroupResponse
import com.capston.domain.response.study_group.OneStudyGroupResponse
import com.capston.domain.response.user.LoginResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface StudyGroupApi {
    // 스터디그룹 생성
    @POST("/v1/study-groups/plans/{planId}")
    suspend fun postNewStudyGroup(
        @Path("planId") planId: Int,
    ): NewStudyGroupResponse

    // 스터디그룹 조회
    @GET("/v1/study-groups/{studyGroupId}")
    suspend fun getOneStudyGroup(
        @Path("studyGroupId") studyGroupId: Int,
    ): OneStudyGroupResponse

    // 스터디그룹 가입
    @POST("/v1/study-groups/join")
    suspend fun postJoinStudyGroup(
        @Body inviteCode: String
    ): MessageResponse
}