package com.capston.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capston.domain.manager.LoadingStateManager
import com.capston.domain.response.plan.GetPlanLectureRoomResponse
import com.capston.domain.response.CheckResponse
import com.capston.domain.response.plan.DeleteOnePlanResponse
import com.capston.domain.response.plan.GetPlanDetailResponse
import com.capston.domain.response.plan.PostPlanRescheduleResponse
import com.capston.domain.usecase.home.PatchLessonSchedulesCheckToggleUseCase
import com.capston.domain.usecase.plan.DeleteOnePlanUseCase
import com.capston.domain.usecase.plan.GetPlanDetailUseCase
import com.capston.domain.usecase.plan.GetPlanLectureRoomUseCase
import com.capston.domain.usecase.plan.PostPlanRescheduleUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LectureRoomViewModel @Inject constructor(
    private val getPlanLectureRoomUseCase: GetPlanLectureRoomUseCase,
    private val getPlanDetailUseCase: GetPlanDetailUseCase,
    private val postPlanRescheduleUseCase: PostPlanRescheduleUseCase,
    private val deleteOnePlanUseCase: DeleteOnePlanUseCase,
    private val patchLessonSchedulesCheckToggleUseCase: PatchLessonSchedulesCheckToggleUseCase,
    private val loadingStateManager: LoadingStateManager
) : ViewModel() {

    private val _getPlanLectureRoomResponse = MutableStateFlow(emptyList<GetPlanLectureRoomResponse>())  // 기본값 ""
    val getPlanLectureRoomResponse: StateFlow<List<GetPlanLectureRoomResponse>> = _getPlanLectureRoomResponse.asStateFlow()

    private val _getPlanDetailResponse = MutableStateFlow(GetPlanDetailResponse())  // 기본값 ""
    val getPlanDetailResponse: StateFlow<GetPlanDetailResponse> = _getPlanDetailResponse.asStateFlow()

    private val _postPlanRescheduleResponse = MutableStateFlow(PostPlanRescheduleResponse())  // 기본값 ""
    val postPlanRescheduleResponse: StateFlow<PostPlanRescheduleResponse> = _postPlanRescheduleResponse.asStateFlow()

    private val _deleteOnePlanResponse = MutableStateFlow(DeleteOnePlanResponse())  // 기본값 ""
    val deleteOnePlanResponse: StateFlow<DeleteOnePlanResponse> = _deleteOnePlanResponse.asStateFlow()

    private val _patchLessonSchedulesCheckToggle = MutableStateFlow(CheckResponse())
    val patchLessonSchedulesCheckToggle = _patchLessonSchedulesCheckToggle

    fun getPlanLectureRoom() {
        viewModelScope.launch {
            loadingStateManager.show()
            getPlanLectureRoomUseCase()
                .catch { e ->
                    Log.e("LectureRoomViewModel", "getPlanLectureRoom 에러: ${e.message}")
                }
                .collect { response ->  // 값 저장
                    _getPlanLectureRoomResponse.value = response // 공백 제거 후 저장
                    Log.d("LectureRoomViewModel", "getPlanLectureRoom 업데이트됨: $response")
                }
            loadingStateManager.hide()
        }
    }

    fun getPlanDetail(planId: Int) {
        viewModelScope.launch {
            getPlanDetailUseCase(planId)
                .catch { e ->
                    Log.e("LectureRoomViewModel", "getPlanDetail 에러: ${e.message}")
                }
                .collect { response ->  // 값 저장
                    _getPlanDetailResponse.value = response // 공백 제거 후 저장
                    Log.d("LectureRoomViewModel", "getPlanDetail 업데이트됨: $response")
                }
        }
    }

    fun postPlanReschedule(planId: Int) {
        viewModelScope.launch {
            postPlanRescheduleUseCase(planId)
                .catch { e ->
                    Log.e("LectureRoomViewModel", "postPlanReschedule 에러: ${e.message}")
                }
                .collect { response ->  // 값 저장
                    _postPlanRescheduleResponse.value = response // 공백 제거 후 저장
                    Log.d("LectureRoomViewModel", "postPlanReschedule 업데이트됨: $response")
                }
        }
    }

    fun deleteOnePlan(planId: Int) {
        viewModelScope.launch {
            deleteOnePlanUseCase(planId)
                .catch { e ->
                    Log.e("LectureRoomViewModel", "deleteOnePlan 에러: ${e.message}")
                }
                .collect { response ->  // 값 저장
                    _deleteOnePlanResponse.value = response // 공백 제거 후 저장
                    Log.d("LectureRoomViewModel", "deleteOnePlan 업데이트됨: $response")
                }
        }
    }

    fun patchLessonSchedulesCheckToggle(lessonScheduleId: Int) {
        viewModelScope.launch {
            loadingStateManager.show()
            try {
                patchLessonSchedulesCheckToggleUseCase(lessonScheduleId).collect { response ->
                    _patchLessonSchedulesCheckToggle.value = response
                    // 체크 토글 후 강의실 데이터 새로고침 (대신 getPlanLectureRoom 호출)
                    getPlanLectureRoom()

                    // 현재 보고 있는 계획 세부 정보도 새로고침
                    val currentPlanId = _getPlanDetailResponse.value.planId
                    if (currentPlanId > 0) {
                        getPlanDetail(currentPlanId)
                    }
                }
            } catch (e: Exception) {
                Log.e("LectureRoomViewModel", "체크 토글 오류: ${e.message}", e)
            } finally {
                loadingStateManager.hide()
            }
        }
    }
}
