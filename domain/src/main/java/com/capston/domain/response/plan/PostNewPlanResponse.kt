package com.capston.domain.response.plan

import com.capston.domain.model.Lecture
import com.capston.domain.model.Lesson
import com.capston.domain.model.User

data class PostNewPlanResponse(
    val id: Int = 0,
    val user: User = User(),
    val lecture: Lecture = Lecture(),
    val lectureName: String = "",
    val startLesson: Lesson = Lesson(),
    val endLesson: Lesson = Lesson(),
    val planType: String = "",
    val startDate: String = "",
    val endDate: String = "",
    val dailyTime: Int = 0,
    val playbackSpeed: Int = 0,
    val createdAt: String = "",
    val studyDays: List<String> = emptyList(),
    val isDeleted: Boolean = false
)