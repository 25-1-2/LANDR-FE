package com.capston.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capston.domain.manager.LoadingStateManager
import com.capston.domain.model.LectureItemDto
import com.capston.domain.model.MyLecture
import com.capston.domain.model.NewPlanLesson
import com.capston.domain.request.LectureDto
import com.capston.domain.request.PatchPlanDto
import com.capston.domain.request.PostNewPlanDto
import com.capston.domain.response.CheckResponse
import com.capston.domain.response.lecture.DistinctLectureResponse
import com.capston.domain.response.lecture.LectureResponseDto
import com.capston.domain.response.plan.GetPlanDetailResponse
import com.capston.domain.response.plan.LectureAliasResponse
import com.capston.domain.response.plan.PostNewPlanResponse
import com.capston.domain.usecase.home.PatchLessonSchedulesCheckToggleUseCase
import com.capston.domain.usecase.lecture.GetAllLectureUseCase
import com.capston.domain.usecase.lecture.GetDistinctLectureUseCase
import com.capston.domain.usecase.lecture.GetLessonsByLectureIdUseCase
import com.capston.domain.usecase.plan.GetPlanDetailUseCase
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
class LectureRoomViewModel @Inject constructor(
    private val getPlanLectureRoomUseCase: GetPlanLectureRoomUseCase,
    private val getPlanDetailUseCase: GetPlanDetailUseCase,
    private val patchLessonSchedulesCheckToggleUseCase: PatchLessonSchedulesCheckToggleUseCase,
    private val loadingStateManager: LoadingStateManager
) : ViewModel() {

    private val _getPlanLectureRoom = MutableStateFlow(emptyList<MyLecture>())  // 기본값 ""
    val getPlanLectureRoom: StateFlow<List<MyLecture>> = _getPlanLectureRoom.asStateFlow()

    private val _getPlanDetail = MutableStateFlow(GetPlanDetailResponse())  // 기본값 ""
    val getPlanDetail: StateFlow<GetPlanDetailResponse> = _getPlanDetail.asStateFlow()

    private val _patchLessonSchedulesCheckToggle = MutableStateFlow(CheckResponse())
    val patchLessonSchedulesCheckToggle = _patchLessonSchedulesCheckToggle

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

    fun getPlanDetail(planId: Int) {
        viewModelScope.launch {
            getPlanDetailUseCase(planId)
                .catch { e ->
                    Log.e("PlanViewModel", "getPlanDetail 에러: ${e.message}")
                }
                .collect { response ->  // 값 저장
                    _getPlanDetail.value = response // 공백 제거 후 저장
                    Log.d("PlanViewModel", "getPlanDetail 업데이트됨: $response")
                }
        }
    }

    fun patchLessonSchedulesCheckToggle(
        lessonScheduleId: Int
    ) {
        viewModelScope.launch {

            val currentTime = System.currentTimeMillis()
            val isCacheValid = currentTime - lastLoadTime < cacheValidDuration

            if (isCacheValid) {
                // 캐시된 데이터 사용, 로딩 인디케이터 표시 안 함
                return@launch
            }

            loadingStateManager.show()
            try {
                patchLessonSchedulesCheckToggleUseCase(lessonScheduleId).collect {
                    _patchLessonSchedulesCheckToggle.value = it
                    // 체크 토글 후 홈 데이터 새로고침
                    getDistinctHome()
                }
            } catch (e: Exception) {
                Log.e("patch lesson schedule toggle 에러", e.message.toString())
            } finally {
                loadingStateManager.hide()
            }
        }
    }
}
