package com.capston.domain.request

data class LoginDto (
    val email: String = "",
    val name: String = "",
    val fcmToken: String = ""
)