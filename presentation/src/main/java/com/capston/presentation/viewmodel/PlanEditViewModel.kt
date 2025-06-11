package com.capston.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capston.domain.manager.LoadingStateManager
import com.capston.domain.request.PatchPeriodPlanDto
import com.capston.domain.request.PatchPlanAliasDto
import com.capston.domain.request.PatchTimePlanDto
import com.capston.domain.request.PostNewPlanDto
import com.capston.domain.response.MessageResponse
import com.capston.domain.response.plan.PatchPlanAliasResponse
import com.capston.domain.usecase.plan.GetPlanDetailUseCase
import com.capston.domain.usecase.plan.GetPlanLectureRoomUseCase
import com.capston.domain.usecase.plan.PatchPeriodPlanUseCase
import com.capston.domain.usecase.plan.PatchPlanAliasUseCase
import com.capston.domain.usecase.plan.PatchTimePlanUseCase
import com.capston.domain.usecase.plan.PostNewPlanUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlanEditViewModel @Inject constructor(
    private val patchPeriodPlanUseCase: PatchPeriodPlanUseCase,
    private val patchTimePlanUseCase: PatchTimePlanUseCase,
    private val loadingStateManager: LoadingStateManager
) : ViewModel() {

    private val _patchPeriodPlanResponse = MutableStateFlow(MessageResponse())  // 기본값 ""
    val patchPeriodPlanResponse: StateFlow<MessageResponse> = _patchPeriodPlanResponse.asStateFlow()

    private val _patchTimePlanResponse = MutableStateFlow(MessageResponse())  // 기본값 ""
    val patchTimePlanResponse: StateFlow<MessageResponse> = _patchTimePlanResponse.asStateFlow()

    fun patchPeriodPlan(planId: Int, patchPeriodPlanDto: PatchPeriodPlanDto) {
        viewModelScope.launch {
            patchPeriodPlanUseCase(planId, patchPeriodPlanDto)
                .catch { e ->
                    Log.e("PlanEditViewModel", "patchPeriodPlan 에러: ${e.message}")
                }
                .collect { response ->  // 값 저장
                    _patchPeriodPlanResponse.value = response // 공백 제거 후 저장
                    Log.d("PlanEditViewModel", "postpatchPeriodPlanNewPlan 업데이트됨: $response")
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