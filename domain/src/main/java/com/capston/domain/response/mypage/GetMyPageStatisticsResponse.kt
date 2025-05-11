package com.capston.domain.response.mypage

data class GetMyPageStatisticsResponse(
    val date: MyDate = MyDate(),
    val totalStudyMinutes: Int = 0,
    val subjectTimes: List<SubjectTimeDto> = emptyList(),
    val weeklyTimes: List<WeeklyTimeDto> = emptyList()
)