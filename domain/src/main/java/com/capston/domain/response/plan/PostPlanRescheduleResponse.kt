package com.capston.domain.response.plan

data class PostPlanRescheduleResponse(
    val dailySchedules: List<RescheduleDailySchedule> = emptyList(),
    val lessonSchedules: List<RescheduleLessonSchedule> = emptyList()
)

data class RescheduleDailySchedule(
    val id: Int = 0,
    val plan: ReschedulePlan = ReschedulePlan(),
    val date: String = "",
    val dayOfWeek: String = "",
    val totalLessons: Int = 0,
    val totalDuration: Int = 0
)

data class RescheduleLessonSchedule(
    val id: Int = 0,
    val dailySchedule: RescheduleDailySchedule = RescheduleDailySchedule(),
    val lesson: RescheduleLesson = RescheduleLesson(),
    val adjustedDuration: Int = 0,
    val displayOrder: Int = 0,
    val completed: Boolean = false,
    val updatedAt: String = ""
)

data class ReschedulePlan(
    val id: Int = 0,
    val user: RescheduleUser = RescheduleUser(),
    val lecture: RescheduleLecture = RescheduleLecture(),
    val lectureName: String = "",
    val startLesson: RescheduleLesson = RescheduleLesson(),
    val endLesson: RescheduleLesson = RescheduleLesson(),
    val planType: String = "",
    val startDate: String = "",
    val endDate: String = "",
    val dailyTime: Int = 0,
    val playbackSpeed: Int = 0,
    val createdAt: String = "",
    val studyDays: List<String> = emptyList(),
    val isDeleted: Boolean = false
)

data class RescheduleUser(
    val id: Int = 0,
    val email: String = "",
    val name: String = "",
    val provider: String = "",
    val createdAt: String = "",
    val updatedAt: String = "",
    val deleted: Boolean = false
)

data class RescheduleLecture(
    val id: Int = 0,
    val title: String = "",
    val teacher: String = "",
    val platform: String = "",
    val subject: String = "",
    val totalLessons: Int = 0,
    val totalDuration: Int = 0,
    val createdAt: String = "",
    val tag: String = ""
)

data class RescheduleLesson(
    val id: Int = 0,
    val lecture: RescheduleLecture = RescheduleLecture(),
    val order: Int = 0,
    val title: String = "",
    val duration: Int = 0
)