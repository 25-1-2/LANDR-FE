package com.capston.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capston.domain.manager.LoadingStateManager
import com.capston.domain.model.NewPlanLesson
import com.capston.domain.request.LectureDto
import com.capston.domain.response.lecture.DistinctLectureResponse
import com.capston.domain.response.lecture.LectureItemDto
import com.capston.domain.usecase.lecture.GetAllLectureUseCase
import com.capston.domain.usecase.lecture.GetDistinctLectureUseCase
import com.capston.domain.usecase.lecture.GetLessonsByLectureIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val getDistinctLectureUseCase: GetDistinctLectureUseCase,
    private val getAllLectureUseCase: GetAllLectureUseCase,
    private val getLessonsByLectureIdUseCase: GetLessonsByLectureIdUseCase,
    private val loadingStateManager: LoadingStateManager
) : ViewModel() {

    private val _distinctLecture = MutableStateFlow(DistinctLectureResponse(data = emptyList()))
    val distinctLecture: StateFlow<DistinctLectureResponse> = _distinctLecture

    private var _allLectureList = MutableStateFlow<List<LectureItemDto>>(emptyList())
    val allLectureList: StateFlow<List<LectureItemDto>> = _allLectureList

    private val _searchLectureItems = MutableStateFlow<List<LectureItemDto>>(emptyList())
    val searchLectureItems: StateFlow<List<LectureItemDto>> = _searchLectureItems

    private val _selectedLecture = MutableStateFlow<LectureItemDto?>(null)
    val selectedLecture: StateFlow<LectureItemDto?> = _selectedLecture.asStateFlow()

    private val _lessonsByLectureId = MutableStateFlow<List<NewPlanLesson>>(emptyList())
    val lessonsByLectureId: StateFlow<List<NewPlanLesson>> = _lessonsByLectureId

    // LectureDto를 직접 받는 함수
    fun getDistinctLecture(lectureDto: LectureDto) {
        viewModelScope.launch {
            try {
                // 로그 함수 호출 추가
                logLectureDtoParameters(lectureDto, "검색")

                getDistinctLectureUseCase(lectureDto).collect { response ->
                    Log.d("LectureViewModel", "API 응답: ${response.data?.size ?: 0}개 항목")
                    if (response.data?.isNotEmpty() == true) {
                        Log.d("LectureViewModel", "첫 번째 항목: ${response.data?.first()?.title}")
                    } else {
                        Log.d("LectureViewModel", "응답에 항목이 없습니다. 검색어: '${lectureDto.search}'")
                    }
                    _distinctLecture.value = response
                }
            } catch (e: Exception) {
                Log.e("LectureViewModel", "강의 조회 실패: ${e.message}", e)
            }
        }
    }

    // LectureDto를 직접 받는 함수
    fun getAllLecture(lectureDto: LectureDto) {
        viewModelScope.launch {
            try {
                // 로그 함수 호출 추가
                logLectureDtoParameters(lectureDto, "전체 목록")

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
            try {
                getLessonsByLectureIdUseCase(lectureId)
                    .catch { e ->
                        Log.e("LectureViewModel", "getLessonsByLectureId 에러: ${e.message}")
                        _lessonsByLectureId.value = emptyList() // Set empty list on error
                    }
                    .collect { response ->
                        _lessonsByLectureId.value = response.lessons // Extract data field
                        Log.d("LectureViewModel", "getLessonsByLectureId 업데이트됨: ${response.lessons.size ?: 0}개 항목")
                    }
            } catch (e: Exception) {
                Log.e("LectureViewModel", "getLessonsByLectureId 예외 발생: ${e.message}", e)
                _lessonsByLectureId.value = emptyList()
            } finally {
                loadingStateManager.hide()
            }
        }
    }
}

// API 호출 시 명확한 정보를 로그에 기록하여 문제 추적을 용이하게 함
fun logLectureDtoParameters(dto: LectureDto, apiName: String) {
    Log.d("LectureViewModel", "===== $apiName API 호출 =====")
    Log.d("LectureViewModel", "검색어: '${dto.search}'")
    Log.d("LectureViewModel", "플랫폼: ${dto.platform?.label ?: "없음"}")
    Log.d("LectureViewModel", "과목: ${dto.subject?.label ?: "없음"}")
    Log.d("LectureViewModel", "커서 ID: ${dto.cursorLectureId}")
    Log.d("LectureViewModel", "커서 생성일: ${dto.cursorCreatedAt ?: "없음"}")
    Log.d("LectureViewModel", "오프셋: ${dto.offset}")
}