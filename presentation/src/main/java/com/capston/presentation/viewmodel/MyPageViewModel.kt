package com.capston.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capston.domain.manager.LoadingStateManager
import com.capston.domain.request.UserNameDto
import com.capston.domain.response.mypage.GetDistinctMyPageResponse
import com.capston.domain.response.mypage.GetMyPageStatisticsResponse
import com.capston.domain.usecase.mypage.GetDistinctMyPageUseCase
import com.capston.domain.usecase.mypage.GetMonthlyStatisticsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyPageViewModel @Inject constructor(
    private val getDistinctMyPageUseCase: GetDistinctMyPageUseCase,
    private val getMonthlyStatisticsUseCase: GetMonthlyStatisticsUseCase,
    private val loadingStateManager: LoadingStateManager
) : ViewModel() {
    private val _getDistinctMyPage = MutableStateFlow(GetDistinctMyPageResponse())
    val getDistinctMyPage: StateFlow<GetDistinctMyPageResponse> = _getDistinctMyPage.asStateFlow()

    private val _getMyPageStatistics = MutableStateFlow(GetMyPageStatisticsResponse())
    val getMyPageStatistics: StateFlow<GetMyPageStatisticsResponse> = _getMyPageStatistics.asStateFlow()

    fun getDistinctMyPage() {
        viewModelScope.launch {
            loadingStateManager.show()
            getDistinctMyPageUseCase()
                .catch { e ->
                    Log.e("MyPageViewModel", "MyPageViewModel 에러: ${e.message}")
                }
                .collect { response ->  // 값 저장
                    _getDistinctMyPage.value = response // 공백 제거 후 저장
                    Log.d("MyPageViewModel", "MyPageViewModel 업데이트됨: $response")
                }
            loadingStateManager.hide()
        }
    }

    fun getMonthlyStatistics(date: String) {
        viewModelScope.launch {
            loadingStateManager.show()
            getMonthlyStatisticsUseCase(date)
                .catch { e ->
                    Log.e("MyPageViewModel", "MyPageViewModel 에러: ${e.message}")
                }
                .collect { response ->  // 값 저장
                    _getMyPageStatistics.value = response // 공백 제거 후 저장
                    Log.d("MyPageViewModel", "MyPageViewModel 업데이트됨: $response")
                }
            loadingStateManager.hide()
        }
    }
}
