package com.capston.domain.response.mypage

data class GetDistinctMyPageResponse(
    val userName: String = "",
    val todayTotalLessonCount: Int = 0,
    val todayCompletedLessonCount: Int = 0,
    val completedLectureCount: Int = 0,
    val studyStreak: Int = 0,
    val goalDate: Int = 0,
    val completedPlanList: List<CompletedPlanDto> = emptyList(),
    val subjectAchievementList: List<SubjectAchievementDto> = emptyList()
)
