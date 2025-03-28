package com.capston.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capston.domain.request.PatchPlanDto
import com.capston.domain.response.plan.LectureAliasResponse
import com.capston.domain.usecase.plan.PatchPlanNameUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlanViewModel @Inject constructor(
    private val patchPlanNameUseCase: PatchPlanNameUseCase
) : ViewModel() {
    private val _patchPlanName = MutableStateFlow(LectureAliasResponse())  // 기본값 ""
    val patchPlanName: StateFlow<LectureAliasResponse> = _patchPlanName.asStateFlow()

    fun patchPlanName(planId: Int, patchPlanDto: PatchPlanDto) {
        viewModelScope.launch {
            patchPlanNameUseCase(planId, patchPlanDto)
                .catch { e ->
                    Log.e("PlanViewModel", "patchPlanName 에러: ${e.message}")
                }
                .collect { response ->  // 값 저장
                    _patchPlanName.value = response // 공백 제거 후 저장
                    Log.d("PlanViewModel", "patchPlanName 업데이트됨: $response")
                }
        }
    }
}
