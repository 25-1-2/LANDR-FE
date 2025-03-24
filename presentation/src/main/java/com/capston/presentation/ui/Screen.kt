package com.capston.presentation.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import com.capston.presentation.R

sealed class Screen(
    val title: String,
    val selectedIcon: Any,
    val unselectedIcon: Any
) {
    data object Login: Screen(
        title = "Login",
        selectedIcon = R.drawable.activity_main_home_iv_on,
        unselectedIcon = R.drawable.activity_main_home_iv,
    )

    data object Home: Screen(
        title = "home",
        selectedIcon = R.drawable.activity_main_home_iv_on,
        unselectedIcon = R.drawable.activity_main_home_iv,
    )

    data object Calender: Screen(
        title = "calender",
        selectedIcon = R.drawable.activity_main_calener_iv_on,
        unselectedIcon = R.drawable.activity_main_calender_iv,
    )

    data object Search: Screen(
        title = "search",
        selectedIcon = Icons.Default.Search,
        unselectedIcon = Icons.Default.Search,
    )

    data object LectureList: Screen(
        title = "lecture_list",
        selectedIcon = R.drawable.activity_main_lecture_list_iv_on,
        unselectedIcon = R.drawable.activity_main_lecture_list_iv,
    )

    data object Profile: Screen(
        title = "profile",
        selectedIcon = R.drawable.activity_main_profile_iv_on,
        unselectedIcon = R.drawable.activity_main_profile_iv,
    )
}