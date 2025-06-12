package com.capston.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capston.domain.manager.LoadingStateManager
import com.capston.domain.response.daily_schedule.DailyScheduleResponse
import com.capston.domain.response.home.DistinctHomeIdResponse
import com.capston.domain.usecase.daily_schedule.GetDailyScheduleUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DailyScheduleViewModel @Inject constructor(
    private val getDailyScheduleUseCase: GetDailyScheduleUseCase,
    private val loadingStateManager: LoadingStateManager
) : ViewModel() {
    private val _getDailySchedule = MutableStateFlow(DailyScheduleResponse())
    val getDailySchedule: StateFlow<DailyScheduleResponse> = _getDailySchedule

    // HomeViewModel과의 동기화를 위한 콜백
    var onDataChanged: (() -> Unit)? = null

    fun getDailySchedule(
        date: String,
        forceRefresh: Boolean = false
    ) {
        // forceRefresh가 아닐 때만 로딩 인디케이터 표시
        if (!forceRefresh) {
            loadingStateManager.show()
        }

        viewModelScope.launch {
            try {
                getDailyScheduleUseCase(date).collect{ response ->
                    _getDailySchedule.value = response
                    Log.d("DailyScheduleViewModel", "일정 데이터 업데이트: $response")
                }
            } catch (e: Exception) {
                Log.e("getDailySchedule", e.toString())
            } finally {
                loadingStateManager.hide()
            }
        }
    }

    // 강제 새로고침 함수 (현재 선택된 날짜로 다시 로드)
    fun forceRefresh(currentDate: String) {
        getDailySchedule(
            date = currentDate,
            forceRefresh = true
        )
    }
}