package com.capston.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capston.domain.request.LoginDto
import com.capston.domain.response.BaseResult
import com.capston.domain.response.LoginResponse
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
    private val _loginResponse = MutableStateFlow(LoginResponse())
    val loginResponse: StateFlow<LoginResponse> = _loginResponse.asStateFlow()

    // 로그인 성공 여부를 나타내는 상태 변수(예: 이벤트 또는 플래그)
    private val _loginSuccess = MutableStateFlow(false)
    val loginSuccess: StateFlow<Boolean> = _loginSuccess.asStateFlow()

    fun postLogin(loginDto: LoginDto) {
        viewModelScope.launch {
            postLoginInfoUseCase(loginDto)
                .catch { e ->
                    Log.e("LoginViewModel", "postLogin error: ${e.message}")
                    // 실패 처리를 위한 추가 로직 추가 가능
                }
                .collect { response ->
                    _loginResponse.value = response
                    _loginSuccess.value = true
                    Log.d("LoginViewModel", "로그인 성공! Token: ${response.token}")

                    // 토큰 저장 로직을 여기에 추가할 수 있습니다
                    // saveTokens(response.accessToken, response.refreshToken)
                }
        }
    }

    // 토큰 저장 메서드를 추가할 수 있습니다
    private fun saveTokens(token: String) {
//        viewModelScope.launch {
//            dataStore.edit { preferences ->
//                preferences[ACCESS_TOKEN_KEY] = token
//            }
//        }
    }
}
