package com.capston.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capston.domain.manager.LoadingStateManager
import com.capston.domain.request.RecommendDto
import com.capston.domain.response.recommend.RecommendResponse
import com.capston.domain.usecase.recommend.PostRecommendLecturesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecommendViewModel @Inject constructor(
    private val postRecommendLecturesUseCase: PostRecommendLecturesUseCase,
    private val loadingStateManager: LoadingStateManager
) : ViewModel() {
    private val _postRecommendLectures = MutableStateFlow<List<RecommendResponse>>(emptyList())
    val postRecommendLectures: StateFlow<List<RecommendResponse>> = _postRecommendLectures.asStateFlow()

    // 단일 과목 추천 (기존 호환성 유지)
    fun postRecommendLectures(recommendDto: RecommendDto) {
        postMultipleRecommendLectures(listOf(recommendDto))
    }

    // 여러 과목 추천 (새로 추가)
    fun postMultipleRecommendLectures(recommendDtos: List<RecommendDto>) {
        viewModelScope.launch {
            loadingStateManager.show()

            val allRecommendations = mutableListOf<RecommendResponse>()

            try {
                // 각 과목별로 순차적으로 API 호출
                recommendDtos.forEachIndexed { index, recommendDto ->
                    Log.d("RecommendViewModel", "과목 ${index + 1}/${recommendDtos.size} 추천 요청: ${recommendDto.subject.label}")

                    postRecommendLecturesUseCase(recommendDto)
                        .catch { e ->
                            Log.e("RecommendViewModel", "과목 ${recommendDto.subject.label} 추천 실패: ${e.message}")
                            // 개별 과목 실패는 무시하고 계속 진행
                        }
                        .collect { responses ->
                            Log.d("RecommendViewModel", "과목 ${recommendDto.subject.label}: ${responses.size}개 추천 받음")
                            allRecommendations.addAll(responses)

                            // 중간 결과도 UI에 반영 (사용자 경험 향상)
                            _postRecommendLectures.value = allRecommendations.toList()
                        }
                }

                // 최종 결과를 점수순으로 정렬
                val sortedRecommendations = allRecommendations
                    .sortedByDescending { it.recommendScore }
                    .take(10) // 최대 10개 추천만 표시

                _postRecommendLectures.value = sortedRecommendations

                Log.d("RecommendViewModel", "전체 추천 완료: ${sortedRecommendations.size}개")

            } catch (e: Exception) {
                Log.e("RecommendViewModel", "추천 과정 중 에러: ${e.message}")
                _postRecommendLectures.value = allRecommendations.toList() // 지금까지 받은 것이라도 표시
            } finally {
                loadingStateManager.hide()
            }
        }
    }

    // 추천 결과 초기화
    fun clearRecommendations() {
        _postRecommendLectures.value = emptyList()
    }
}