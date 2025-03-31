package com.capston.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capston.domain.response.CheckResponse
import com.capston.domain.response.home.DistinctHomeIdResponse
import com.capston.domain.response.home.TodayScheduleResponse
import com.capston.domain.response.home.UserProgressResponse
import com.capston.domain.usecase.home.GetDistinctHomeUseCase
import com.capston.domain.usecase.home.PatchLessonSchedulesCheckToggleUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getDistinctHomeUseCase: GetDistinctHomeUseCase,
    private val patchLessonSchedulesCheckToggleUseCase: PatchLessonSchedulesCheckToggleUseCase
) : ViewModel() {

    private val _getDistinctHome = MutableStateFlow(DistinctHomeIdResponse())
    private val _patchLessonSchedulesCheckToggle = MutableStateFlow(CheckResponse())

    val getDistinctHome: StateFlow<DistinctHomeIdResponse> = _getDistinctHome
    val patchLessonSchedulesCheckToggle = _patchLessonSchedulesCheckToggle

    fun getDistinctHome() {
        viewModelScope.launch {
            // 로딩 상태로 초기화
            //_getDistinctHome.value = _getDistinctHome.value.copy(status = BaseLoadingState.LOADING)
            try {
                getDistinctHomeUseCase().collect { response ->
                    // todaySchedule이 null일 경우 빈 객체로 처리
                    val safeTodaySchedule = response.todaySchedule ?: TodayScheduleResponse()
                    val safeUserProgress = response.userProgress ?: UserProgressResponse()

                    // userProgress와 todaySchedule을 포함한 응답을 MutableStateFlow에 업데이트
                    _getDistinctHome.value = response.copy(todaySchedule = safeTodaySchedule, userProgress = safeUserProgress)

                }
            } catch (e: Exception) {
                Log.e("getDistinctHome", "에러: ${e.message}", e)
                // 에러 상태로 업데이트
//                _getDistinctHome.value =
//                    _getDistinctHome.value.copy()
            }
        }
    }

    fun patchLessonSchedulesCheckToggle(
        lessonScheduleId: Int
    ) {
        viewModelScope.launch {
            try {
                patchLessonSchedulesCheckToggleUseCase(lessonScheduleId).collect {
                    _patchLessonSchedulesCheckToggle.value = it
                    getDistinctHome()
                }
            } catch (e: Exception) {
                Log.e("patch lesson schedule toggle 에러", e.message.toString())
            }
        }
    }
}