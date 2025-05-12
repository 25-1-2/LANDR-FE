package com.capston.domain.response.mypage

import com.capston.domain.response.enum_class.Monthly

data class MyDate(
    val year: Int = 2000,
    val month: Monthly = Monthly.JANUARY,
    val monthValue: Int = 0,
    val leapYear: Boolean = false
)
