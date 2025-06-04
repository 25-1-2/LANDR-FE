package com.capston.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capston.domain.manager.LoadingStateManager
import com.capston.domain.request.RecommendDto
import com.capston.domain.response.mypage.GetDistinctMyPageResponse
import com.capston.domain.response.mypage.GetMyPageStatisticsResponse
import com.capston.domain.response.recommend.RecommendResponse
import com.capston.domain.usecase.mypage.GetDistinctMyPageUseCase
import com.capston.domain.usecase.mypage.GetMonthlyStatisticsUseCase
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

    fun postRecommendLectures(recommendDto: RecommendDto) {
        viewModelScope.launch {
            loadingStateManager.show()
            postRecommendLecturesUseCase(recommendDto)
                .catch { e ->
                    Log.e("RecommendViewModel", "RecommendViewModel 에러: ${e.message}")
                }
                .collect { response ->  // 값 저장
                    _postRecommendLectures.value = response // 공백 제거 후 저장
                    Log.d("RecommendViewModel", "RecommendViewModel 업데이트됨: $response")
                }
            loadingStateManager.hide()
        }
    }
}