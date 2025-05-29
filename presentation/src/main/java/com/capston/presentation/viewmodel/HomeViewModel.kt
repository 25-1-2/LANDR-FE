package com.capston.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capston.domain.manager.LoadingStateManager
import com.capston.domain.request.UpdateDDayRequest
import com.capston.domain.response.CheckResponse
import com.capston.domain.response.home.DDayResponse
import com.capston.domain.response.home.DistinctHomeIdResponse
import com.capston.domain.response.home.TodayScheduleResponse
import com.capston.domain.usecase.home.DeleteDDayUseCase
import com.capston.domain.usecase.home.GetDDayUseCase
import com.capston.domain.usecase.home.GetDistinctHomeUseCase
import com.capston.domain.usecase.home.PatchDDayUseCase
import com.capston.domain.usecase.home.PatchLessonSchedulesCheckToggleUseCase
import com.capston.domain.usecase.home.PostDDayUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getDistinctHomeUseCase: GetDistinctHomeUseCase,
    private val patchLessonSchedulesCheckToggleUseCase: PatchLessonSchedulesCheckToggleUseCase,
    private val getDDayUseCase: GetDDayUseCase,
    private val postDDayUseCase: PostDDayUseCase,
    private val deleteDDayUseCase: DeleteDDayUseCase,
    private val patchDDayUseCase: PatchDDayUseCase,
    private val loadingStateManager: LoadingStateManager
) : ViewModel() {

    private val _getDistinctHome = MutableStateFlow(DistinctHomeIdResponse())
    val getDistinctHome: StateFlow<DistinctHomeIdResponse> = _getDistinctHome

    private val _patchLessonSchedulesCheckToggle = MutableStateFlow(CheckResponse())
    val patchLessonSchedulesCheckToggle = _patchLessonSchedulesCheckToggle

    private val _dDay = MutableStateFlow<DDayResponse?>(null)
    val dDay: StateFlow<DDayResponse?> = _dDay.asStateFlow()

    // 캐시된 데이터 유지
    private var lastLoadTime: Long = 0
    private val cacheValidDuration = 30_000 // 30초

    fun getDistinctHome(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            val currentTime = System.currentTimeMillis()
            val isCacheValid = currentTime - lastLoadTime < cacheValidDuration

            // forceRefresh가 true이면 캐시 무시
            if (isCacheValid && !forceRefresh) {
                return@launch
            }

            loadingStateManager.show()
            try {
                getDistinctHomeUseCase().catch { e ->
                    Log.e("getDistinctHome", "에러: ${e.message}", e)
                    val currentState = _getDistinctHome.value
                    _getDistinctHome.value = DistinctHomeIdResponse(
                        userProgress = currentState.userProgress,
                        todaySchedule = TodayScheduleResponse()
                    )
                }.collect { response ->
                    Log.d("HomeViewModel", "수신된 응답: $response")

                    if (response.todaySchedule == null) {
                        _getDistinctHome.value = DistinctHomeIdResponse(
                            userProgress = response.userProgress,
                            todaySchedule = TodayScheduleResponse()
                        )
                    } else {
                        _getDistinctHome.value = response
                    }

                    // 성공적으로 데이터를 받아온 경우만 lastLoadTime 업데이트
                    lastLoadTime = currentTime
                }
            } catch (e: Exception) {
                Log.e("getDistinctHome", "전체 예외 처리: ${e.message}", e)
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

    fun patchLessonSchedulesCheckToggle(lessonScheduleId: Int) {
        viewModelScope.launch {
            // 체크박스 토글 시에는 로딩 인디케이터 표시하지 않음 (빠른 반응성을 위해)
            try {
                patchLessonSchedulesCheckToggleUseCase(lessonScheduleId).collect { response ->
                    _patchLessonSchedulesCheckToggle.value = response

                    // 체크 토글 후 강제로 홈 데이터 새로고침 (캐시 무시)
                    // lastLoadTime을 리셋하여 캐시 무효화
                    lastLoadTime = 0
                    getDistinctHome(forceRefresh = true)
                }
            } catch (e: Exception) {
                Log.e("patch lesson schedule toggle 에러", e.message.toString())
            }
        }
    }

    // 백그라운드 갱신 (deeplink나 다른 화면에서 돌아왔을 때)
    fun refreshInBackground(dDayId: Int) {
        viewModelScope.launch {
            getDistinctHomeUseCase().collect { response ->
                _getDistinctHome.value = response

                viewModelScope.launch {
                    getDDayUseCase(dDayId).collect { ddayResponse ->
                        _dDay.value = ddayResponse
                    }
                }
            }
        }
    }

    fun getDDay(dDayId: Int) {
        viewModelScope.launch {
            loadingStateManager.show()
            getDDayUseCase(dDayId)
                .catch { e ->
                    Log.e("HomeViewModel", "HomeViewModel 에러: ${e.message}")
                }
                .collect { response ->
                    _dDay.value = response
                    refreshInBackground(dDayId)
                    Log.d("HomeViewModel", "HomeViewModel 업데이트됨: ${response}")
                }
            loadingStateManager.hide()
        }
    }

    fun postDDay(updateDDayRequest: UpdateDDayRequest) {
        viewModelScope.launch {
            loadingStateManager.show()
            postDDayUseCase(updateDDayRequest)
                .catch { e ->
                    Log.e("HomeViewModel", "HomeViewModel 에러: ${e.message}")
                }
                .collect { response ->
                    _dDay.value = response
                    dDay.value?.let { refreshInBackground(it.ddayId) }
                    Log.d("HomeViewModel", "HomeViewModel 업데이트됨: ${response}")
                }
            getDistinctHome(forceRefresh = true)
            loadingStateManager.hide()
        }
    }

    fun deleteDDay(ddayId: Int) {
        viewModelScope.launch {
            try {
                deleteDDayUseCase(ddayId)
                _dDay.value = null
                refreshInBackground(ddayId)
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error deleting D-Day: ${e.message}", e)
            }
            getDistinctHome(forceRefresh = true)
        }
    }

    fun patchDDay(dDayId: Int, updateDDayRequest: UpdateDDayRequest) {
        viewModelScope.launch {
            loadingStateManager.show()
            patchDDayUseCase(dDayId, updateDDayRequest)
                .catch { e ->
                    Log.e("HomeViewModel", "HomeViewModel 에러: ${e.message}")
                }
                .collect { response ->
                    _dDay.value = response
                    refreshInBackground(dDayId)
                    Log.d("HomeViewModel", "HomeViewModel 업데이트됨: ${response}")
                }
            getDistinctHome(forceRefresh = true)
            loadingStateManager.hide()
        }
    }

    // 다른 ViewModel에서 호출할 수 있는 강제 새로고침 함수
    fun forceRefresh() {
        lastLoadTime = 0
        getDistinctHome(forceRefresh = true)
    }
}