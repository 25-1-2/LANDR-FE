package com.capston.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capston.domain.manager.LoadingStateManager
import com.capston.domain.response.CheckResponse
import com.capston.domain.response.MessageResponse
import com.capston.domain.response.plan.GetPlanDetailResponse
import com.capston.domain.response.study_group.OneStudyGroupResponse
import com.capston.domain.usecase.home.PatchLessonSchedulesCheckToggleUseCase
import com.capston.domain.usecase.plan.DeleteOnePlanUseCase
import com.capston.domain.usecase.plan.GetPlanDetailUseCase
import com.capston.domain.usecase.plan.PostPlanRescheduleUseCase
import com.capston.domain.usecase.study_group.GetOneStudyGroupUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GroupPlanViewModel @Inject constructor(
    private val getPlanDetailUseCase: GetPlanDetailUseCase,
    private val postPlanRescheduleUseCase: PostPlanRescheduleUseCase,
    private val deleteOnePlanUseCase: DeleteOnePlanUseCase,
    private val patchLessonSchedulesCheckToggleUseCase: PatchLessonSchedulesCheckToggleUseCase,
    private val getOneStudyGroupUseCase: GetOneStudyGroupUseCase,
    private val loadingStateManager: LoadingStateManager
) : ViewModel() {

    private val _getPlanDetailResponse = MutableStateFlow(GetPlanDetailResponse())
    val getPlanDetailResponse: StateFlow<GetPlanDetailResponse> = _getPlanDetailResponse.asStateFlow()

    private val _postPlanRescheduleResponse = MutableStateFlow(MessageResponse())
    val postPlanRescheduleResponse: StateFlow<MessageResponse> = _postPlanRescheduleResponse.asStateFlow()

    private val _deleteOnePlanResponse = MutableStateFlow(MessageResponse())
    val deleteOnePlanResponse: StateFlow<MessageResponse> = _deleteOnePlanResponse.asStateFlow()

    private val _patchLessonSchedulesCheckToggle = MutableStateFlow(CheckResponse())
    val patchLessonSchedulesCheckToggle = _patchLessonSchedulesCheckToggle

    private val _getOneStudyGroupResponse = MutableStateFlow(OneStudyGroupResponse())
    val getOneStudyGroupResponse: StateFlow<OneStudyGroupResponse> = _getOneStudyGroupResponse.asStateFlow()

    // HomeViewModel과의 동기화를 위한 콜백
    var onDataChanged: (() -> Unit)? = null

    fun getPlanDetail(planId: Int) {
        viewModelScope.launch {
            getPlanDetailUseCase(planId)
                .catch { e ->
                    Log.e("LectureRoomViewModel", "getPlanDetail 에러: ${e.message}")
                }
                .collect { response ->
                    _getPlanDetailResponse.value = response
                    Log.d("LectureRoomViewModel", "getPlanDetail 업데이트됨: $response")
                }
        }
    }

    fun postPlanReschedule(planId: Int) = viewModelScope.launch {
        postPlanRescheduleUseCase(planId)
            .catch { e ->
                Log.e("LectureRoomViewModel", "postPlanReschedule 에러: ${e.message}")
            }
            .collect { response ->
                _postPlanRescheduleResponse.value = response
                Log.d("LectureRoomViewModel", "postPlanReschedule 업데이트됨: $response")
            }
    }

    fun patchLessonSchedulesCheckToggle(lessonScheduleId: Int) {
        viewModelScope.launch {
            try {
                patchLessonSchedulesCheckToggleUseCase(lessonScheduleId).collect { response ->
                    _patchLessonSchedulesCheckToggle.value = response

                    Log.d("LectureRoomViewModel", "체크 토글 완료: $response")

                    // 현재 보고 있는 계획 세부 정보 새로고침
                    val currentPlanId = _getPlanDetailResponse.value.planId
                    if (currentPlanId > 0) {
                        getPlanDetail(currentPlanId)
                    }

                    // HomeViewModel과 다른 ViewModel들과 동기화
                    onDataChanged?.invoke()
                }
            } catch (e: Exception) {
                Log.e("LectureRoomViewModel", "체크 토글 오류: ${e.message}", e)
            }
        }
    }

    fun getOneStudyGroup(groupId: Int) {
        viewModelScope.launch {
            try {
                getOneStudyGroupUseCase(groupId).collect { response ->
                    _getOneStudyGroupResponse.value = response
                    Log.d("LectureRoomViewModel", "스터디그룹 불러오기 완료: $response")
                }
            } catch (e: Exception) {
                Log.e("LectureRoomViewModel", "스터디그룹 불러오기 오류: ${e.message}", e)
            }
        }
    }
}