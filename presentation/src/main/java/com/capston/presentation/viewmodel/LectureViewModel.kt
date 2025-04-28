package com.capston.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capston.domain.manager.LoadingStateManager
import com.capston.domain.request.LectureDto
import com.capston.domain.response.lecture.DistinctLectureResponse
import com.capston.domain.response.lecture.LectureResponseDto
import com.capston.domain.usecase.lecture.GetAllLectureUseCase
import com.capston.domain.usecase.lecture.GetDistinctLectureUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LectureViewModel @Inject constructor(
    private val getDistinctLectureUseCase: GetDistinctLectureUseCase,
    private val getAllLectureUseCase: GetAllLectureUseCase,
    private val loadingStateManager: LoadingStateManager
) : ViewModel() {

    private val _distinctLecture = MutableStateFlow(DistinctLectureResponse(data = emptyList()))
    val distinctLecture: StateFlow<DistinctLectureResponse> = _distinctLecture

    private val _allLectureList = MutableStateFlow<List<LectureResponseDto>>(emptyList())
    val allLectureList: StateFlow<List<LectureResponseDto>> = _allLectureList

    fun getDistinctLecture(lectureDto: LectureDto) {
        viewModelScope.launch {
            loadingStateManager.show()
            try {
                getDistinctLectureUseCase(lectureDto).collect { response ->
                    _distinctLecture.value = response
                }
            } catch (e: Exception) {
                Log.e("LectureViewModel", "getDistinctLecture 에러: ${e.message}", e)
            } finally {
                loadingStateManager.hide()
            }
        }
    }

    fun getAllLecture() {
        viewModelScope.launch {
            loadingStateManager.show()
            try {
                getAllLectureUseCase().collect { response ->
                    _allLectureList.value = response.data ?: emptyList()
                }
            } catch (e: Exception) {
                Log.e("LectureViewModel", "getAllLecture 에러: ${e.message}", e)
            } finally {
                loadingStateManager.hide()
            }
        }
    }
}