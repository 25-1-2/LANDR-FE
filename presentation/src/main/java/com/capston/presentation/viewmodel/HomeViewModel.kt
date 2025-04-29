package com.capston.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capston.domain.manager.LoadingStateManager
import com.capston.domain.response.CheckResponse
import com.capston.domain.response.home.DistinctHomeIdResponse
import com.capston.domain.response.home.TodayScheduleResponse
import com.capston.domain.response.home.UserProgressResponse
import com.capston.domain.usecase.home.GetDistinctHomeUseCase
import com.capston.domain.usecase.home.PatchLessonSchedulesCheckToggleUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getDistinctHomeUseCase: GetDistinctHomeUseCase,
    private val patchLessonSchedulesCheckToggleUseCase: PatchLessonSchedulesCheckToggleUseCase,
    private val loadingStateManager: LoadingStateManager
) : ViewModel() {

    private val _getDistinctHome = MutableStateFlow(DistinctHomeIdResponse())
    val getDistinctHome: StateFlow<DistinctHomeIdResponse> = _getDistinctHome

    private val _patchLessonSchedulesCheckToggle = MutableStateFlow(CheckResponse())
    val patchLessonSchedulesCheckToggle = _patchLessonSchedulesCheckToggle

    fun getDistinctHome() {
        viewModelScope.launch {
            loadingStateManager.show()
            try {
                getDistinctHomeUseCase().catch { e ->
                    Log.e("getDistinctHome", "에러: ${e.message}", e)

                    // 오류 발생 시에도 UI에 표시할 기본 상태 설정
                    // 이전 상태의 userProgress는 유지하면서 todaySchedule만 기본값으로
                    val currentState = _getDistinctHome.value
                    _getDistinctHome.value = DistinctHomeIdResponse(
                        userProgress = currentState.userProgress,
                        todaySchedule = TodayScheduleResponse()
                    )
                }.collect { response ->
                    Log.d("HomeViewModel", "수신된 응답: $response")

                    // 수신된 응답이 완전한지 확인 후 상태 업데이트
                    if (response.todaySchedule == null) {
                        // todaySchedule이 null이면 기본값으로 설정하되 userProgress는 유지
                        _getDistinctHome.value = DistinctHomeIdResponse(
                            userProgress = response.userProgress,
                            todaySchedule = TodayScheduleResponse()
                        )
                    } else {
                        _getDistinctHome.value = response
                    }
                }
            } catch (e: Exception) {
                Log.e("getDistinctHome", "전체 예외 처리: ${e.message}", e)

                // 전체 예외 처리에서도 이전 상태의 userProgress 유지
                val currentState = _getDistinctHome.value
                _getDistinctHome.value = DistinctHomeIdResponse(
                    userProgress = currentState.userProgress,
                    todaySchedule = TodayScheduleResponse()
                )
            } finally {
                loadingStateManager.hide()
            }
        }
    }

    fun patchLessonSchedulesCheckToggle(
        lessonScheduleId: Int
    ) {
        viewModelScope.launch {
            loadingStateManager.show()
            try {
                patchLessonSchedulesCheckToggleUseCase(lessonScheduleId).collect {
                    _patchLessonSchedulesCheckToggle.value = it
                    // 체크 토글 후 홈 데이터 새로고침
                    getDistinctHome()
                }
            } catch (e: Exception) {
                Log.e("patch lesson schedule toggle 에러", e.message.toString())
            } finally {
                loadingStateManager.hide()
            }
        }
    }
}