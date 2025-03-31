package com.capston.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capston.domain.base.Result
import com.capston.domain.usecase.error.GetExceptionApiUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ErrorViewModel @Inject constructor(
    private val getExceptionApiUseCase: GetExceptionApiUseCase,
) : ViewModel() {
    private val _getExceptionApi = MutableStateFlow(Result())
    val getExceptionApi: StateFlow<Result> = _getExceptionApi

    fun getExceptionApi() {
        viewModelScope.launch {
            try {
                getExceptionApiUseCase().collect{ response ->
                    val safeErrorCode = response.code
                    val safeErrorMessage = response.message
                    _getExceptionApi.value = response.copy(code = safeErrorCode,message = safeErrorMessage)
                }
            } catch (e: Exception) {
                Log.e("errorViewModel", "에러 메시지를 불러오는 데 실패했습니다.")
            }
        }
    }
}