package com.capston.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capston.domain.base.BaseLoadingState
import com.capston.domain.response.BaseResponse
import com.capston.domain.response.DistinctHomeIdResponse
import com.capston.domain.usecase.home.GetDistinctHomeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getDistinctHomeUseCase: GetDistinctHomeUseCase,
) : ViewModel() {

    private val _getDistinctHome = MutableStateFlow(BaseResponse<DistinctHomeIdResponse>())
    val getDistinctHome: StateFlow<BaseResponse<DistinctHomeIdResponse>> = _getDistinctHome

    fun getDistinctHome() {
        viewModelScope.launch {
            // 로딩 상태로 초기화
            _getDistinctHome.value = _getDistinctHome.value.copy(status = BaseLoadingState.LOADING)
            try {
                getDistinctHomeUseCase().collect { response ->
                    // 성공 상태로 업데이트
                    _getDistinctHome.value = _getDistinctHome.value.copy(
                        result = response.result,
                        payload = response.payload,
                        status = BaseLoadingState.SUCCESS
                    )
                }
            } catch (e: Exception) {
                Log.e("getDistinctHome", "에러: ${e.message}", e)
                // 에러 상태로 업데이트
                _getDistinctHome.value =
                    _getDistinctHome.value.copy(status = BaseLoadingState.ERROR)
            }
        }
    }
}