package com.capston.domain.response.home

data class WeeklyAchievement(
    val mondayAchieved: Boolean = false,
    val tuesdayAchieved: Boolean = false,
    val wednesdayAchieved: Boolean = false,
    val thursdayAchieved: Boolean = false,
    val fridayAchieved: Boolean = false,
    val saturdayAchieved: Boolean = false,
    val sundayAchieved: Boolean = false
)
