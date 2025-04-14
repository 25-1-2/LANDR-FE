package com.capston.domain.model

data class User(
    val id: Int = 0,
    val email: String = "",
    val name: String = "",
    val provider: String = "",
    val createdAt: String = "",
    val updatedAt: String = "",
    val deleted: Boolean = false
)