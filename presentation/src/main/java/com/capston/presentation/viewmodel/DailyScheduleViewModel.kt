package com.capston.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capston.domain.response.daily_schedule.DailyScheduleResponse
import com.capston.domain.response.home.DistinctHomeIdResponse
import com.capston.domain.usecase.daily_schedule.GetDailyScheduleUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DailyScheduleViewModel @Inject constructor(
    private val getDailyScheduleUseCase: GetDailyScheduleUseCase
) : ViewModel() {
    private val _getDailySchedule = MutableStateFlow(DailyScheduleResponse())
    val getDailySchedule: StateFlow<DailyScheduleResponse> = _getDailySchedule

    fun getDailySchedule(date: String) {
        viewModelScope.launch {
            try {
                getDailyScheduleUseCase(date).collect{ response ->
                    _getDailySchedule.value = response
                }
            } catch (e: Exception) {
                Log.e("getDailySchedule", e.toString())
            }
        }
    }
}