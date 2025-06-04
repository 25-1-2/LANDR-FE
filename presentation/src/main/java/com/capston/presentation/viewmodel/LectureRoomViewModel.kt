package com.capston.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capston.domain.manager.LoadingStateManager
import com.capston.domain.response.plan.GetPlanLectureRoomResponse
import com.capston.domain.response.CheckResponse
import com.capston.domain.response.MessageResponse
import com.capston.domain.response.plan.GetPlanDetailResponse
import com.capston.domain.response.plan.PostPlanRescheduleResponse
import com.capston.domain.response.study_group.NewStudyGroupResponse
import com.capston.domain.response.study_group.OneStudyGroupResponse
import com.capston.domain.usecase.home.PatchLessonSchedulesCheckToggleUseCase
import com.capston.domain.usecase.plan.DeleteOnePlanUseCase
import com.capston.domain.usecase.plan.GetPlanDetailUseCase
import com.capston.domain.usecase.plan.GetPlanLectureRoomUseCase
import com.capston.domain.usecase.plan.PostPlanRescheduleUseCase
import com.capston.domain.usecase.study_group.GetOneStudyGroupUseCase
import com.capston.domain.usecase.study_group.PostJoinStudyGroupUseCase
import com.capston.domain.usecase.study_group.PostNewStudyGroupUseCase
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
    private val postNewStudyGroupUseCase: PostNewStudyGroupUseCase,
    private val getOneStudyGroupUseCase: GetOneStudyGroupUseCase,
    private val postJoinStudyGroupUseCase: PostJoinStudyGroupUseCase,
    private val loadingStateManager: LoadingStateManager
) : ViewModel() {

    private val _getPlanLectureRoomResponse = MutableStateFlow(emptyList<GetPlanLectureRoomResponse>())
    val getPlanLectureRoomResponse: StateFlow<List<GetPlanLectureRoomResponse>> = _getPlanLectureRoomResponse.asStateFlow()

    private val _getPlanDetailResponse = MutableStateFlow(GetPlanDetailResponse())
    val getPlanDetailResponse: StateFlow<GetPlanDetailResponse> = _getPlanDetailResponse.asStateFlow()

    private val _postPlanRescheduleResponse = MutableStateFlow(PostPlanRescheduleResponse())
    val postPlanRescheduleResponse: StateFlow<PostPlanRescheduleResponse> = _postPlanRescheduleResponse.asStateFlow()

    private val _deleteOnePlanResponse = MutableStateFlow(MessageResponse())
    val deleteOnePlanResponse: StateFlow<MessageResponse> = _deleteOnePlanResponse.asStateFlow()

    private val _patchLessonSchedulesCheckToggle = MutableStateFlow(CheckResponse())
    val patchLessonSchedulesCheckToggle = _patchLessonSchedulesCheckToggle

    private val _postNewStudyGroupResponse = MutableStateFlow(NewStudyGroupResponse())
    val postNewStudyGroupResponse: StateFlow<NewStudyGroupResponse> = _postNewStudyGroupResponse.asStateFlow()

    private val _getOneStudyGroupResponse = MutableStateFlow(OneStudyGroupResponse())
    val getOneStudyGroupResponse: StateFlow<OneStudyGroupResponse> = _getOneStudyGroupResponse.asStateFlow()

    private val _postJoinStudyGroupResponse = MutableStateFlow(MessageResponse())
    val postJoinStudyGroupResponse: StateFlow<MessageResponse> = _postJoinStudyGroupResponse.asStateFlow()

    // HomeViewModel과의 동기화를 위한 콜백
    var onDataChanged: (() -> Unit)? = null

    // 스낵바 표시용 콜백 - UI에서 설정할 함수
    var onShowSnackbar: ((String) -> Unit)? = null

    fun getPlanLectureRoom() {
        viewModelScope.launch {
            loadingStateManager.show()
            getPlanLectureRoomUseCase()
                .catch { e ->
                    Log.e("LectureRoomViewModel", "getPlanLectureRoom 에러: ${e.message}")
                }
                .collect { response ->
                    _getPlanLectureRoomResponse.value = response
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

    fun deleteOnePlan(planId: Int) {
        viewModelScope.launch {
            deleteOnePlanUseCase(planId)
                .catch { e ->
                    Log.e("LectureRoomViewModel", "deleteOnePlan 에러: ${e.message}")
                }
                .collect { response ->
                    _deleteOnePlanResponse.value = response
                    Log.d("LectureRoomViewModel", "deleteOnePlan 업데이트됨: $response")

                    // 삭제 후 강의실 목록 새로고침
                    getPlanLectureRoom()

                    // HomeViewModel 동기화
                    onDataChanged?.invoke()
                }
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

                    // 강의실 목록도 새로고침
                    getPlanLectureRoom()

                    // HomeViewModel과 다른 ViewModel들과 동기화
                    onDataChanged?.invoke()
                }
            } catch (e: Exception) {
                Log.e("LectureRoomViewModel", "체크 토글 오류: ${e.message}", e)
            }
        }
    }

    fun postNewStudyGroup(planId: Int) {
        viewModelScope.launch {
            try {
                postNewStudyGroupUseCase(planId).collect { response ->
                    _postNewStudyGroupResponse.value = response
                    Log.d("LectureRoomViewModel", "스터디그룹 생성 완료: $response")
                }
            } catch (e: Exception) {
                Log.e("LectureRoomViewModel", "스터디그룹 생성 오류: ${e.message}", e)
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

    fun postJoinStudyGroup(inviteCode: String) {
        viewModelScope.launch {
            try {
                postJoinStudyGroupUseCase(inviteCode).collect { response ->
                    _postJoinStudyGroupResponse.value = response
                    Log.d("LectureRoomViewModel", "스터디그룹 가입 완료: $response")
                }
            } catch (e: Exception) {
                Log.e("LectureRoomViewModel", "스터디그룹 가입 오류: ${e.message}", e)
            }
        }
    }
}