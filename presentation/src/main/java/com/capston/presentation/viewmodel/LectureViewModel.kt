package com.capston.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capston.domain.manager.LoadingStateManager
import com.capston.domain.model.Lecture
import com.capston.domain.model.NewPlanLesson
import com.capston.domain.request.LectureDto
import com.capston.domain.response.lecture.DistinctLectureResponse
import com.capston.domain.response.lecture.LectureResponseDto
import com.capston.domain.usecase.lecture.GetAllLectureUseCase
import com.capston.domain.usecase.lecture.GetDistinctLectureUseCase
import com.capston.domain.usecase.lecture.GetLessonsByLectureIdUseCase
import com.capston.presentation.ui.LectureItemDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LectureViewModel @Inject constructor(
    private val getDistinctLectureUseCase: GetDistinctLectureUseCase,
    private val getAllLectureUseCase: GetAllLectureUseCase,
    private val getLessonsByLectureIdUseCase: GetLessonsByLectureIdUseCase,
    private val loadingStateManager: LoadingStateManager
) : ViewModel() {

    private val _distinctLecture = MutableStateFlow(DistinctLectureResponse(data = emptyList()))
    val distinctLecture: StateFlow<DistinctLectureResponse> = _distinctLecture

    private var _allLectureList = MutableStateFlow<List<LectureResponseDto>>(emptyList())
    val allLectureList: StateFlow<List<LectureResponseDto>> = _allLectureList

    private val _searchLectureItems = MutableStateFlow<List<LectureItemDto>>(emptyList())
    val searchLectureItems: StateFlow<List<LectureItemDto>> = _searchLectureItems

    private val _selectedLecture = MutableStateFlow<LectureItemDto?>(null)
    val selectedLecture: StateFlow<LectureItemDto?> = _selectedLecture.asStateFlow()

    private val _lessonsByLectureId = MutableStateFlow<List<NewPlanLesson>>(emptyList())
    val lessonsByLectureId: StateFlow<List<NewPlanLesson>> = _lessonsByLectureId

    // 검색어만 받는 함수 (기존 코드와의 호환성 유지)
    fun getDistinctLecture(searchName: String) {
        val lectureDto = LectureDto(
            search = searchName,
            cursorLectureId = "",
            cursorCreatedAt = "",
            offset = "10"
        )
        getDistinctLecture(lectureDto)
    }

    // LectureDto를 직접 받는 함수
    fun getDistinctLecture(lectureDto: LectureDto) {
        viewModelScope.launch {
            loadingStateManager.show()
            try {
                Log.d("LectureViewModel", "강의 검색 API 호출: search=${lectureDto.search}, cursor=${lectureDto.cursorLectureId}")

                getDistinctLectureUseCase(lectureDto).collect { response ->
                    Log.d("LectureViewModel", "API 응답: ${response.data?.size ?: 0}개 항목")
                    if (response.data?.isNotEmpty() == true) {
                        Log.d("LectureViewModel", "첫 번째 항목: ${response.data?.first()?.title}")
                    }
                    _distinctLecture.value = response
                }
            } catch (e: Exception) {
                Log.e("LectureViewModel", "강의 조회 실패: ${e.message}", e)
            } finally {
                loadingStateManager.hide()
            }
        }
    }

    // 기본 파라미터만 받는 함수 (기존 코드와의 호환성 유지)
    fun getAllLecture() {
        val lectureDto = LectureDto(
            search = "",
            cursorLectureId = "",
            cursorCreatedAt = "",
            offset = "10"
        )
        getAllLecture(lectureDto)
    }

    // LectureDto를 직접 받는 함수
    fun getAllLecture(lectureDto: LectureDto) {
        viewModelScope.launch {
            loadingStateManager.show()
            try {
                Log.d("LectureViewModel", "강의 목록 조회 API 호출: cursor=${lectureDto.cursorLectureId}")

                getAllLectureUseCase(lectureDto).collect { response ->
                    val lectures = response.data ?: emptyList()
                    Log.d("LectureViewModel", "강의 목록 응답: ${lectures.size}개 항목")

                    // 개별 강의 로그
                    if (lectures.isNotEmpty()) {
                        Log.d("LectureViewModel", "첫 번째 강의: ${lectures.first().title}, 교사: ${lectures.first().teacher}")
                    }

                    _allLectureList.value = lectures
                }
            } catch (e: Exception) {
                Log.e("LectureViewModel", "강의 목록 조회 실패: ${e.message}", e)
                _allLectureList.value = emptyList()
            } finally {
                loadingStateManager.hide()
            }
        }
    }

    // 저장하는 함수
    fun updateSearchLectureItems(items: List<LectureItemDto>) {
        _searchLectureItems.value = items
    }

    fun selectLecture(lecture: LectureItemDto) {
        _selectedLecture.value = LectureItemDto(
            id = lecture.id,
            title = lecture.title,
            teacher = lecture.teacher,
            platform = lecture.platform,
            subject = lecture.subject,
            totalLessons = lecture.totalLessons,
            tag = lecture.tag,
            createdAt = lecture.createdAt
        )
    }

    fun getLessonsByLectureId(lectureId: Int) {
        viewModelScope.launch {
            loadingStateManager.show()
            getLessonsByLectureIdUseCase(lectureId)
                .catch { e ->
                    Log.e("LectureViewModel", "getLessonsByLectureId 에러: ${e.message}")
                }
                .collect { response ->  // 값 저장
                    _lessonsByLectureId.value = response // 공백 제거 후 저장
                    Log.d("LectureViewModel", "getLessonsByLectureId 업데이트됨: $response")
                }
            loadingStateManager.hide()
        }
    }
}