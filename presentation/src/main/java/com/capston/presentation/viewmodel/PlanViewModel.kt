package com.capston.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capston.domain.manager.LoadingStateManager
import com.capston.domain.request.PatchPlanDto
import com.capston.domain.request.PostNewPlanDto
import com.capston.domain.response.MessageResponse
import com.capston.domain.response.MessageResponse
import com.capston.domain.response.plan.GetPlanDetailResponse
import com.capston.domain.response.plan.LectureAliasResponse
\import com.capston.domain.usecase.plan.GetPlanDetailUseCase
import com.capston.domain.usecase.plan.GetPlanLectureRoomUseCase
import com.capston.domain.usecase.plan.PatchPlanNameUseCase
import com.capston.domain.usecase.plan.PostNewPlanUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlanViewModel @Inject constructor(
    private val postNewPlanUseCase: PostNewPlanUseCase,
    private val patchPlanNameUseCase: PatchPlanNameUseCase,
    private val getPlanLectureRoomUseCase: GetPlanLectureRoomUseCase,
    private val getPlanDetailUseCase: GetPlanDetailUseCase,
    private val loadingStateManager: LoadingStateManager
) : ViewModel() {
    private val _postNewPlanResponse = MutableStateFlow(MessageResponse())  // 기본값 ""
    val postNewPlanResponse: StateFlow<MessageResponse> = _postNewPlanResponse.asStateFlow()

    private val _patchPlanName = MutableStateFlow(LectureAliasResponse())  // 기본값 ""
    val patchPlanName: StateFlow<LectureAliasResponse> = _patchPlanName.asStateFlow()

    fun postNewPlan(postNewPlanDto: PostNewPlanDto) {
        viewModelScope.launch {
            postNewPlanUseCase(postNewPlanDto)
                .catch { e ->
                    Log.e("PlanViewModel", "postNewPlan 에러: ${e.message}")
                }
                .collect { response ->  // 값 저장
                    _postNewPlanResponse.value = response // 공백 제거 후 저장
                    Log.d("PlanViewModel", "postNewPlan 업데이트됨: $response")
                }
//            loadingStateManager.hide()
        }
    }

    fun patchPlanName(planId: Int, patchPlanDto: PatchPlanDto) {
        viewModelScope.launch {
            loadingStateManager.show()
            patchPlanNameUseCase(planId, patchPlanDto)
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
