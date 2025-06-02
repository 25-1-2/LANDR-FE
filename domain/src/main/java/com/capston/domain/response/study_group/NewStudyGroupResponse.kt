package com.capston.domain.response.study_group


data class NewStudyGroupResponse(
    val studyGroupId: Int = 0,
    val inviteCode: String = "",
    val name: String = ""
)