package com.capston.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capston.domain.manager.LoadingStateManager
import com.capston.domain.request.PatchPlanAliasDto
import com.capston.domain.request.PostNewPeriodPlanDto
import com.capston.domain.request.PostNewTimePlanDto
import com.capston.domain.response.MessageResponse
import com.capston.domain.response.plan.PatchPlanAliasResponse
import com.capston.domain.usecase.plan.PatchPlanAliasUseCase
import com.capston.domain.usecase.plan.PostNewPeriodPlanUseCase
import com.capston.domain.usecase.plan.PostNewTimePlanUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlanViewModel @Inject constructor(
    private val postNewPeriodPlanUseCase: PostNewPeriodPlanUseCase,
    private val postNewTimePlanUseCase: PostNewTimePlanUseCase,
    private val patchPlanAliasUseCase: PatchPlanAliasUseCase,
    private val loadingStateManager: LoadingStateManager
) : ViewModel() {
    private val _postNewPlanResponse = MutableStateFlow(MessageResponse())  // 기본값 ""
    val postNewPlanResponse: StateFlow<MessageResponse> = _postNewPlanResponse.asStateFlow()

    private val _patchPlanName = MutableStateFlow(PatchPlanAliasResponse())  // 기본값 ""
    val patchPlanName: StateFlow<PatchPlanAliasResponse> = _patchPlanName.asStateFlow()

    fun postNewPlan(postNewPeriodPlanDto: PostNewPeriodPlanDto) {
        viewModelScope.launch {
            // planType에 따라 적절한 UseCase 선택
            val useCase = when (postNewPeriodPlanDto.planType) {
                "PERIOD" -> {
                    Log.d("PlanViewModel", "기간으로 계획하기 - PeriodPlanUseCase 사용")
                    postNewPeriodPlanUseCase(postNewPeriodPlanDto)
                }
                "TIME" -> {
                    Log.d("PlanViewModel", "시간으로 계획하기 - TimePlanUseCase 사용")
                    // PostNewPeriodPlanDto를 PostNewTimePlanDto로 변환
                    val timePlanDto = PostNewTimePlanDto(
                        lectureId = postNewPeriodPlanDto.lectureId,
                        planType = postNewPeriodPlanDto.planType,
                        startLessonId = postNewPeriodPlanDto.startLessonId,
                        endLessonId = postNewPeriodPlanDto.endLessonId,
                        studyDayOfWeeks = postNewPeriodPlanDto.studyDayOfWeeks,
                        dailyTime = postNewPeriodPlanDto.dailyTime, // MakePlanScreen에서 dailyTime 값 전달받음
                        playbackSpeed = postNewPeriodPlanDto.playbackSpeed
                    )
                    postNewTimePlanUseCase(timePlanDto)
                }
                else -> {
                    Log.w("PlanViewModel", "알 수 없는 planType: ${postNewPeriodPlanDto.planType}")
                    postNewPeriodPlanUseCase(postNewPeriodPlanDto)
                }
            }

            useCase
                .catch { e ->
                    Log.e("PlanViewModel", "postNewPlan 에러 (planType: ${postNewPeriodPlanDto.planType}): ${e.message}")
                }
                .collect { response ->  // 값 저장
                    _postNewPlanResponse.value = response // 공백 제거 후 저장
                    Log.d("PlanViewModel", "postNewPlan 업데이트됨 (planType: ${postNewPeriodPlanDto.planType}): $response")
                }
//            loadingStateManager.hide()
        }
    }


    fun patchPlanName(planId: Int, patchPlanAliasDto: PatchPlanAliasDto) {
        viewModelScope.launch {
            loadingStateManager.show()
            patchPlanAliasUseCase(planId, patchPlanAliasDto)
                .catch { e ->
                    Log.e("PlanViewModel", "patchPlanName 에러: ${e.message}")
                }
                .collect { response ->  // 값 저장
                    _patchPlanName.value = response // 공백 제거 후 저장
                    Log.d("PlanViewModel", "patchPlanName 업데이트됨: $response")
                }
            loadingStateManager.hide()
        }
    }
}
