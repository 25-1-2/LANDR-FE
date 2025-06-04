package com.capston.domain.response.study_group

data class OneStudyGroupResponse(
    val studyGroupId: Int = 0,
    val name: String = "",
    val inviteCode: String = "",
    val leaderId: Int = 0,
    val leaderName: String = "",
    val lectureName: String = "",
    val members: List<StudyGroupMember> = emptyList()
)

data class StudyGroupMember(
    val userId: Int = 0,
    val userName: String = "",
    val planId: Int = 0
)