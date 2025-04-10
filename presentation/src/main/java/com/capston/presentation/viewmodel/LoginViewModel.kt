package com.capston.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capston.domain.request.LoginDto
import com.capston.domain.response.LoginResponse
import com.capston.domain.usecase.login.PostLoginInfoUseCase
import com.capston.domain.usecase.token.ClearTokensUseCase
import com.capston.domain.usecase.token.GetAccessTokenUseCase
import com.capston.domain.usecase.token.SaveAccessTokenUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val postLoginInfoUseCase: PostLoginInfoUseCase,
    private val saveTokensUseCase: SaveAccessTokenUseCase,
    private val getAccessTokenUseCase: GetAccessTokenUseCase,
    private val clearTokensUseCase: ClearTokensUseCase
): ViewModel() {
    // 회원가입 성공 시 받아오는 액세스 토큰
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
                    Log.d("LoginViewModel", "로그인 성공! Access Token: ${response.token}")

                    // 토큰 저장 (비동기 작업)
                    saveTokensUseCase(
                        response.token,
                    )
                    Log.d("LoginViewModel", "토큰 저장 요청 완료")

                    checkAccessToken()
                }
        }
    }

    // 토큰 정보 수집 예시
    fun checkAccessToken() {
        viewModelScope.launch {
            getAccessTokenUseCase().collect { token ->
                Log.d("LoginViewModel", "현재 저장된 액세스 토큰: $token")
            }
        }
    }

    // 로그아웃 기능
    fun logout() {
        viewModelScope.launch {
            clearTokensUseCase()
            Log.d("LoginViewModel", "토큰 삭제 완료")
        }
    }
}