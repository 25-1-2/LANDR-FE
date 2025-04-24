package com.capston.data.loading

import com.capston.domain.manager.LoadingStateManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoadingStateManagerImpl @Inject constructor() : LoadingStateManager {
    private val _isLoading = MutableStateFlow(false)
    override val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    override fun show() {
        _isLoading.value = true
    }

    override fun hide() {
        _isLoading.value = false
    }
}