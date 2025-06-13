package com.capston.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capston.domain.manager.LoadingStateManager
import com.capston.domain.request.PatchPeriodPlanDto
import com.capston.domain.request.PatchTimePlanDto
import com.capston.domain.response.MessageResponse
import com.capston.domain.response.lecture.LessonByLectureId
import com.capston.domain.response.plan.PlanDetailResponse
import com.capston.domain.usecase.lecture.GetLessonsByLectureIdUseCase
import com.capston.domain.usecase.plan.DeleteOnePlanUseCase
import com.capston.domain.usecase.plan.GetPlanDetailUseCase
import com.capston.domain.usecase.plan.PatchPeriodPlanUseCase
import com.capston.domain.usecase.plan.PatchTimePlanUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel

class PlanDetailViewModel @Inject constructor(
    private val getPlanDetailUseCase: GetPlanDetailUseCase,
    private val getLessonsByLectureIdUseCase: GetLessonsByLectureIdUseCase,
    private val patchPeriodPlanUseCase: PatchPeriodPlanUseCase,
    private val patchTimePlanUseCase: PatchTimePlanUseCase,
    private val deleteOnePlanUseCase: DeleteOnePlanUseCase,
    private val loadingStateManager: LoadingStateManager
) : ViewModel() {

    private val _planDetailResponse = MutableStateFlow(PlanDetailResponse())  // 기본값 ""
    val planDetailResponse: StateFlow<PlanDetailResponse> = _planDetailResponse.asStateFlow()

    private val _lessonsByLectureId = MutableStateFlow<List<LessonByLectureId>>(emptyList())
    val lessonsByLectureId: StateFlow<List<LessonByLectureId>> = _lessonsByLectureId.asStateFlow()

    private val _patchPeriodPlanResponse = MutableStateFlow(MessageResponse())  // 기본값 ""
    val patchPeriodPlanResponse: StateFlow<MessageResponse> = _patchPeriodPlanResponse.asStateFlow()

    private val _patchTimePlanResponse = MutableStateFlow(MessageResponse())  // 기본값 ""
    val patchTimePlanResponse: StateFlow<MessageResponse> = _patchTimePlanResponse.asStateFlow()

    private val _deleteOnePlanResponse = MutableStateFlow(MessageResponse())
    val deleteOnePlanResponse: StateFlow<MessageResponse> = _deleteOnePlanResponse.asStateFlow()

    var onLectureRoomDataChanged: (() -> Unit)? = null

    fun getPlanDetail(planId: Int) {
        viewModelScope.launch {
            getPlanDetailUseCase(planId)
                .catch { e ->
                    Log.e("LectureRoomViewModel", "getPlanDetail 에러: ${e.message}")
                }
                .collect { response ->
                    _planDetailResponse.value = response
                    Log.d("LectureRoomViewModel", "getPlanDetail 업데이트됨: $response")
                }
        }
    }

    fun getLessonsByLectureId(lectureId: Int) {
        viewModelScope.launch {
            loadingStateManager.show()
            try {
                getLessonsByLectureIdUseCase(lectureId)
                    .catch { e ->
                        Log.e("LectureViewModel", "getLessonsByLectureId 에러: ${e.message}")
                        _lessonsByLectureId.value = emptyList()
                    }
                    .collect { response ->
                        _lessonsByLectureId.value = response.lessons // Extract data field
                        Log.d("LectureViewModel", "getLessonsByLectureId 업데이트됨: ${response.lessons.size}개 항목")
                    }
            } catch (e: Exception) {
                Log.e("LectureViewModel", "getLessonsByLectureId 예외 발생: ${e.message}")
                _lessonsByLectureId.value = emptyList()
            } finally {
                loadingStateManager.hide()
            }
        }
    }

    fun patchPeriodPlan(planId: Int, patchPeriodPlanDto: PatchPeriodPlanDto) {
        viewModelScope.launch {
            patchPeriodPlanUseCase(planId, patchPeriodPlanDto)
                .catch { e ->
                    Log.e("PlanEditViewModel", "patchPeriodPlan 에러: ${e.message}")
                }
                .collect { response ->  // 값 저장
                    _patchPeriodPlanResponse.value = response // 공백 제거 후 저장
                    Log.d("PlanEditViewModel", "patchPeriodPlan 업데이트됨: $response")
                }
        }
    }

    fun deleteOnePlan(planId: Int) {
        viewModelScope.launch {
            deleteOnePlanUseCase(planId)
                .catch { e ->
                    Log.e("LectureRoomViewModel", "deleteOnePlan 에러: ${e.message}")
                }
                .collect { response ->
                    _deleteOnePlanResponse.value = response
                    Log.d("LectureRoomViewModel", "deleteOnePlan 업데이트됨: $response")

                    // HomeViewModel 동기화
                    onLectureRoomDataChanged?.invoke()
                }
        }
    }

    fun patchTimePlan(planId: Int, patchTimePlanDto: PatchTimePlanDto) {
        viewModelScope.launch {
            patchTimePlanUseCase(planId, patchTimePlanDto)
                .catch { e ->
                    Log.e("PlanEditViewModel", "patchTimePlan 에러: ${e.message}")
                }
                .collect { response ->  // 값 저장
                    _patchTimePlanResponse.value = response // 공백 제거 후 저장
                    Log.d("PlanEditViewModel", "patchTimePlan 업데이트됨: $response")
                }
        }
    }
}