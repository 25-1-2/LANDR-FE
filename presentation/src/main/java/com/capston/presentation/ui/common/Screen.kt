package com.capston.presentation.ui.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import com.capston.presentation.R

sealed class Screen(
    val title: String,
    val selectedIcon: Any,
    val unselectedIcon: Any
) {
    data object Home: Screen(
        title = "home",
        selectedIcon = R.drawable.activity_main_home_iv_on,
        unselectedIcon = R.drawable.activity_main_home_iv,
    )

    data object Calender: Screen(
        title = "calender",
        selectedIcon = R.drawable.activity_main_calener_iv_on,
        unselectedIcon = R.drawable.activity_main_calendar_iv,
    )

    data object Search: Screen(
        title = "search",
        selectedIcon = Icons.Default.Search,
        unselectedIcon = Icons.Default.Search,
    )

    data object LectureRoom: Screen(
        title = "lecture_room",
        selectedIcon = R.drawable.activity_main_lecture_list_iv_on,
        unselectedIcon = R.drawable.activity_main_lecture_list_iv,
    )

    data object SinglePlan: Screen(
        title = "single_plan",
        selectedIcon = R.drawable.activity_main_lecture_list_iv_on,
        unselectedIcon = R.drawable.activity_main_lecture_list_iv,
    )

    data object GroupPlan: Screen(
        title = "group_plan",
        selectedIcon = R.drawable.activity_main_lecture_list_iv_on,
        unselectedIcon = R.drawable.activity_main_lecture_list_iv,
    )

    data object Profile: Screen(
        title = "profile",
        selectedIcon = R.drawable.activity_main_profile_iv_on,
        unselectedIcon = R.drawable.activity_main_profile_iv,
    )

    data object Plan: Screen(
        title = "plan",
        selectedIcon = R.drawable.activity_main_profile_iv_on,
        unselectedIcon = R.drawable.activity_main_profile_iv
    )

    data object Notification: Screen(
        title = "notification",
        selectedIcon = R.drawable.icon_notification_on,
        unselectedIcon = R.drawable.home_screen_notification_iv,
    )

}