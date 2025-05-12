package com.capston.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capston.domain.manager.LoadingStateManager
import com.capston.domain.request.LoginDto
import com.capston.domain.request.UserNameDto
import com.capston.domain.response.user.LoginResponse
import com.capston.domain.response.user.UserProfileResponse
import com.capston.domain.usecase.login.GetUserProfileUseCase
import com.capston.domain.usecase.login.PatchUserNameUseCase
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
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val patchUserNameUseCase: PatchUserNameUseCase,
    private val saveAccessTokenUseCase: SaveAccessTokenUseCase,
    private val getAccessTokenUseCase: GetAccessTokenUseCase,
    private val clearTokensUseCase: ClearTokensUseCase,
    private val loadingStateManager: LoadingStateManager
): ViewModel() {
    // 회원가입 성공 시 받아오는 액세스 토큰
    private val _loginResponse = MutableStateFlow(LoginResponse())
    val loginResponse: StateFlow<LoginResponse> = _loginResponse.asStateFlow()

    // 로그인 성공 여부를 나타내는 상태 변수(예: 이벤트 또는 플래그)
    private val _loginSuccess = MutableStateFlow(false)
    val loginSuccess: StateFlow<Boolean> = _loginSuccess.asStateFlow()

    private val _getUserprofile = MutableStateFlow(UserProfileResponse())
    val getUserProfile: StateFlow<UserProfileResponse> = _getUserprofile.asStateFlow()

    fun postLogin(loginDto: LoginDto) {
        viewModelScope.launch {
            loadingStateManager.show()
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
                    saveAccessTokenUseCase(
                        response.token,
                    )
                    Log.d("LoginViewModel", "토큰 저장 요청 완료")

                    checkAccessToken()
                }
                loadingStateManager.hide()
        }
    }

    // 토큰 정보 수집 예시
    fun checkAccessToken() {
        viewModelScope.launch {
            loadingStateManager.show()
            getAccessTokenUseCase().collect { token ->
                Log.d("LoginViewModel", "현재 저장된 액세스 토큰: $token")
            }
            loadingStateManager.hide()
        }
    }

    fun logout() {
        viewModelScope.launch {
            loadingStateManager.show()
            clearTokensUseCase()

            Log.d("LoginViewModel", "Tokens and saved name cleared")
            loadingStateManager.hide()
        }
    }

    fun getUserProfile() {
        viewModelScope.launch {
            try {
                loadingStateManager.show()
                getUserProfileUseCase()
                    .catch { e ->
                        Log.e("LoginViewModel", "Get profile error: ${e.message}")
                    }
                    .collect { response ->
                        // Apply any name override from our manager
                        _getUserprofile.value = response
                        Log.d("LoginViewModel", "Profile retrieved: ${response.name}")
                    }
            } finally {
                loadingStateManager.hide()
            }
        }
    }

    fun patchUserName(userNameDto: UserNameDto) {
        viewModelScope.launch {
            try {
                loadingStateManager.show()

                patchUserNameUseCase(userNameDto)
                    .catch { e ->
                        Log.e("LoginViewModel", "Name update error: ${e.message}")
                    }
                    .collect { response ->
                        // Apply our cached name override
                        _getUserprofile.value = response
                        Log.d("LoginViewModel", "User profile updated: ${response.name}")
                    }
            } finally {
                loadingStateManager.hide()
            }
        }
    }
}