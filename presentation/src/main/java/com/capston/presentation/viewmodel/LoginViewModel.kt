package com.capston.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capston.domain.request.LoginDto
import com.capston.domain.usecase.login.PostLoginInfoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val postLoginInfoUseCase: PostLoginInfoUseCase
): ViewModel() {
    // 로그인 성공 여부를 나타내는 상태 변수(예: 이벤트 또는 플래그)
    private val _basicResult = MutableStateFlow(Result)
    private val _loginSuccess = MutableStateFlow(false)
    val loginSuccess: StateFlow<Boolean> = _loginSuccess.asStateFlow()

    fun postLogin(loginDto: LoginDto) {
        viewModelScope.launch {
            postLoginInfoUseCase(loginDto)
                .catch { e ->
                    Log.e("LoginViewModel", "postLogin error: ${e.message}")
                    // 실패 처리를 위한 추가 로직 추가 가능
                }
                .collect {
                    // 응답 데이터가 없다면 단순히 성공 여부를 업데이트
                    _loginSuccess.value = true
                    Log.d("LoginViewModel", "로그인 성공")
                }
        }
    }
}
