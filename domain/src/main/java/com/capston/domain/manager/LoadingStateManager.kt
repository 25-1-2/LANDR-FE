package com.capston.domain.manager

import kotlinx.coroutines.flow.StateFlow

interface LoadingStateManager {
    val isLoading: StateFlow<Boolean>
    fun show()
    fun hide()
}