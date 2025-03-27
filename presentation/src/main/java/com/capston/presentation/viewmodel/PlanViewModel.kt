package com.capston.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capston.domain.request.PatchPlanDto
import com.capston.domain.response.BaseResponse
import com.capston.domain.usecase.plan.PatchPlanNameUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlanViewModel @Inject constructor(
    private val patchPlanNameUseCase: PatchPlanNameUseCase
) : ViewModel() {
    private val _patchPlanName = MutableStateFlow(String())
    val patchPlanName: StateFlow<String> = _patchPlanName

    fun patchPlanName(
        planId: Int,
        patchPlanDto: PatchPlanDto
    ) {
        viewModelScope.launch {
            try {
                patchPlanNameUseCase(planId, patchPlanDto).collect {
                    _patchPlanName.value = it
                }
            } catch (e: Exception) {
                Log.e("patch plan name 에러", e.message.toString())
            }
        }
    }
}