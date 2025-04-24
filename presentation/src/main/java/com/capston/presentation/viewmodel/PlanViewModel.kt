package com.capston.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capston.domain.manager.LoadingStateManager
import com.capston.domain.model.MyLecture
import com.capston.domain.request.PatchPlanDto
import com.capston.domain.request.PostPlanDto
import com.capston.domain.response.plan.LectureAliasResponse
import com.capston.domain.response.plan.PostPlanResponse
import com.capston.domain.usecase.plan.GetPlanLectureRoomUseCase
import com.capston.domain.usecase.plan.PatchPlanNameUseCase
import com.capston.domain.usecase.plan.PostPlanDetailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlanViewModel @Inject constructor(
    private val postPlanDetailUseCase: PostPlanDetailUseCase,
    private val patchPlanNameUseCase: PatchPlanNameUseCase,
    private val getPlanLectureRoomUseCase: GetPlanLectureRoomUseCase,
    private val loadingStateManager: LoadingStateManager
) : ViewModel() {
    private val _postPlanDetail = MutableStateFlow(PostPlanResponse())  // 기본값 ""
    val postPlanDetail: StateFlow<PostPlanResponse> = _postPlanDetail.asStateFlow()

    private val _patchPlanName = MutableStateFlow(LectureAliasResponse())  // 기본값 ""
    val patchPlanName: StateFlow<LectureAliasResponse> = _patchPlanName.asStateFlow()

    private val _getPlanLectureRoom = MutableStateFlow(emptyList<MyLecture>())  // 기본값 ""
    val getPlanLectureRoom: StateFlow<List<MyLecture>> = _getPlanLectureRoom.asStateFlow()

    fun postPlanDetail(postPlanDto: PostPlanDto) {
        viewModelScope.launch {
            loadingStateManager.show()
            postPlanDetailUseCase(postPlanDto)
                .catch { e ->
                    Log.e("PlanViewModel", "postPlanDetail 에러: ${e.message}")
                }
                .collect { response ->  // 값 저장
                    _postPlanDetail.value = response // 공백 제거 후 저장
                    Log.d("PlanViewModel", "postPlanDetail 업데이트됨: $response")
                }
            loadingStateManager.hide()
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

    fun getPlanLectureRoom() {
        viewModelScope.launch {
            loadingStateManager.show()
            getPlanLectureRoomUseCase()
                .catch { e ->
                    Log.e("PlanViewModel", "getPlanLectureRoom 에러: ${e.message}")
                }
                .collect { response ->  // 값 저장
                    _getPlanLectureRoom.value = response // 공백 제거 후 저장
                    Log.d("PlanViewModel", "getPlanLectureRoom 업데이트됨: $response")
                }
            loadingStateManager.hide()
        }
    }
}
