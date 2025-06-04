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
import kotlinx.coroutines.flow.first
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

    // 여러 과목 추천 (수정된 버전)
    fun postMultipleRecommendLectures(recommendDtos: List<RecommendDto>) {
        viewModelScope.launch {
            loadingStateManager.show()

            Log.d("RecommendViewModel", "=== 다중 과목 추천 시작 ===")
            Log.d("RecommendViewModel", "총 ${recommendDtos.size}개 과목 추천 요청")

            val allRecommendations = mutableListOf<RecommendResponse>()

            try {
                // 각 과목별로 순차적으로 API 호출 및 완료 대기
                recommendDtos.forEachIndexed { index, recommendDto ->
                    Log.d("RecommendViewModel", "과목 ${index + 1}/${recommendDtos.size} 추천 요청 시작: ${recommendDto.subject.label}")

                    try {
                        // first()를 사용하여 각 요청이 완료될 때까지 대기
                        val responses = postRecommendLecturesUseCase(recommendDto)
                            .catch { e ->
                                Log.e("RecommendViewModel", "과목 ${recommendDto.subject.label} 추천 실패: ${e.message}")
                                emit(emptyList()) // 실패 시 빈 리스트 emit
                            }
                            .first() // 첫 번째 결과를 받을 때까지 대기

                        Log.d("RecommendViewModel", "과목 ${recommendDto.subject.label}: ${responses.size}개 추천 받음")

                        allRecommendations.addAll(responses)

                        // 중간 결과를 UI에 반영
                        _postRecommendLectures.value = allRecommendations.toList()

                    } catch (e: Exception) {
                        Log.e("RecommendViewModel", "과목 ${recommendDto.subject.label} 처리 중 예외: ${e.message}")
                    }
                }

                // 최종 결과를 점수순으로 정렬
                val sortedRecommendations = allRecommendations
                    .sortedByDescending { it.recommendScore }
                    .take(20) // 최대 20개 추천만 표시

                _postRecommendLectures.value = sortedRecommendations

                Log.d("RecommendViewModel", "=== 모든 과목 추천 완료 ===")
                Log.d("RecommendViewModel", "최종 추천 결과: ${sortedRecommendations.size}개")

                // 과목별 결과 요약 로그
                val subjectSummary = sortedRecommendations.groupBy { it.subject.label }
                subjectSummary.forEach { (subject, recommendations) ->
                    Log.d("RecommendViewModel", "$subject: ${recommendations.size}개 추천")
                }

            } catch (e: Exception) {
                Log.e("RecommendViewModel", "추천 과정 중 전체 에러: ${e.message}")
                _postRecommendLectures.value = allRecommendations.toList() // 지금까지 받은 것이라도 표시
            } finally {
                loadingStateManager.hide()
            }
        }
    }

    // 추천 결과 초기화
    fun clearRecommendations() {
        Log.d("RecommendViewModel", "추천 결과 초기화")
        _postRecommendLectures.value = emptyList()
    }
}