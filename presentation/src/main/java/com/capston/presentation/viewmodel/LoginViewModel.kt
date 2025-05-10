package com.capston.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capston.data.local.storage.UserPreferencesRepository
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
    private val saveTokensUseCase: SaveAccessTokenUseCase,
    private val getAccessTokenUseCase: GetAccessTokenUseCase,
    private val clearTokensUseCase: ClearTokensUseCase,
    private val loadingStateManager: LoadingStateManager,
    private val userPreferencesRepository: UserPreferencesRepository
): ViewModel() {
    // 회원가입 성공 시 받아오는 액세스 토큰
    private val _loginResponse = MutableStateFlow(LoginResponse())
    val loginResponse: StateFlow<LoginResponse> = _loginResponse.asStateFlow()

    // 로그인 성공 여부를 나타내는 상태 변수(예: 이벤트 또는 플래그)
    private val _loginSuccess = MutableStateFlow(false)
    val loginSuccess: StateFlow<Boolean> = _loginSuccess.asStateFlow()

    private val _getUserprofile = MutableStateFlow(UserProfileResponse())
    val getUserProfile: StateFlow<UserProfileResponse> = _getUserprofile.asStateFlow()

    private var cachedUserName: String? = null

    // Load saved name when ViewModel is created
    init {
        viewModelScope.launch {
            userPreferencesRepository.getUserName().collect { savedName ->
                if (!savedName.isNullOrEmpty()) {
                    cachedUserName = savedName
                    Log.d("LoginViewModel", "Loaded persistent name: $savedName")
                }
            }
        }
    }

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
                    saveTokensUseCase(
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

    // 로그아웃 기능
    fun logout() {
        viewModelScope.launch {
            loadingStateManager.show()
            clearTokensUseCase()

            // Also clear saved name
            userPreferencesRepository.saveUserName("")
            cachedUserName = null

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
                        // Override with saved name if available
                        if (cachedUserName != null) {
                            val updatedProfile = UserProfileResponse(
                                id = response.id,
                                email = response.email,
                                name = cachedUserName!!
                            )
                            _getUserprofile.value = updatedProfile
                            Log.d("LoginViewModel", "Profile retrieved with persistent name: ${updatedProfile.name}")
                        } else {
                            _getUserprofile.value = response
                            Log.d("LoginViewModel", "Profile retrieved from server: ${response.name}")
                        }
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

                // Immediately store locally for app restart persistence
                userPreferencesRepository.saveUserName(userNameDto.name)
                cachedUserName = userNameDto.name
                Log.d("LoginViewModel", "Saved name persistently: ${userNameDto.name}")

                patchUserNameUseCase(userNameDto)
                    .catch { e ->
                        Log.e("LoginViewModel", "Name update error: ${e.message}")
                    }
                    .collect { response ->
                        // Always use our saved name for UI
                        val updatedProfile = UserProfileResponse(
                            id = _getUserprofile.value.id,
                            email = _getUserprofile.value.email,
                            name = userNameDto.name
                        )

                        _getUserprofile.value = updatedProfile
                        Log.d("LoginViewModel", "Updated profile: ${updatedProfile.name}")
                    }
            } finally {
                loadingStateManager.hide()
            }
        }
    }
}