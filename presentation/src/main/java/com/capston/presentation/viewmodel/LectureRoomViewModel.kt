package com.capston.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capston.domain.manager.LoadingStateManager
import com.capston.domain.request.JoinStudyGroupDto
import com.capston.domain.response.plan.GetPlanLectureRoomResponse
import com.capston.domain.response.MessageResponse
import com.capston.domain.usecase.plan.GetPlanLectureRoomUseCase
import com.capston.domain.usecase.study_group.PostJoinStudyGroupUseCase
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
    private val postJoinStudyGroupUseCase: PostJoinStudyGroupUseCase,
    private val loadingStateManager: LoadingStateManager
) : ViewModel() {

    private val _getPlanLectureRoomResponse = MutableStateFlow(emptyList<GetPlanLectureRoomResponse>())
    val getPlanLectureRoomResponse: StateFlow<List<GetPlanLectureRoomResponse>> = _getPlanLectureRoomResponse.asStateFlow()

    private val _postJoinStudyGroupResponse = MutableStateFlow(MessageResponse())
    val postJoinStudyGroupResponse: StateFlow<MessageResponse> = _postJoinStudyGroupResponse.asStateFlow()

    // HomeViewModel과의 동기화를 위한 콜백
    var onDataChanged: (() -> Unit)? = null

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

    fun postJoinStudyGroup(joinStudyGroupDto: JoinStudyGroupDto) {
        viewModelScope.launch {
            try {
                postJoinStudyGroupUseCase(joinStudyGroupDto).collect { response ->
                    _postJoinStudyGroupResponse.value = response
                    Log.d("LectureRoomViewModel", "스터디그룹 가입 완료: $response")
                }
            } catch (e: Exception) {
                Log.e("LectureRoomViewModel", "스터디그룹 가입 오류: ${e.message}", e)
            }
        }
    }
}