package com.capston.data.repository.remote.datasourcelmpl

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.capston.data.repository.remote.api.DailyScheduleApi
import com.capston.domain.base.BaseLoadingState
import com.capston.domain.datasource.DailyScheduleDataSource
import com.capston.domain.response.BaseResponse
import com.capston.domain.response.Result
import com.capston.domain.response.daily_schedule.DailyScheduleResponse
import com.capston.domain.response.enum_class.Day
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class DailyScheduleDataSourceImpl @Inject constructor(
    private val dailyScheduleApi: DailyScheduleApi
) : DailyScheduleDataSource {

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getDailySchedule(date: String): Flow<DailyScheduleResponse> = flow {
        try {
            val response: DailyScheduleResponse = dailyScheduleApi.getDailySchedule(date)
            emit(response)

        } catch (e: Exception) {
            Log.e("DailyScheduleDataSourceImpl", "예외 발생: ${e.message}")
            throw e
        }
    }.catch { e ->
        val errorMessage = e.message ?: "알 수 없는 오류 발생"
        Log.e("DailyScheduleDataSourceImpl", "에러: $errorMessage")

        val errorResponse = Result(code = 5000, message = errorMessage)
        val response = BaseResponse(result = errorResponse, payload = null, status = BaseLoadingState.ERROR)

        Log.e("DailyScheduleDataSourceImpl", response.toString())
    }

    /**
     * 서버 응답이 null이거나 오류가 발생했을 때 기본값을 반환하는 함수
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun getDefaultDailySchedule(dateString: String): DailyScheduleResponse {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val localDate = try {
            LocalDate.parse(dateString, formatter)
        } catch (e: Exception) {
            LocalDate.now() // 날짜 파싱 실패 시 오늘 날짜 사용
        }

        val dayOfWeek = Day.MON

        return DailyScheduleResponse(
            date = dateString,
            dayOfWeek = dayOfWeek,
            totalLessons = 0,
            totalDuration = 0,
            lessonSchedules = null // 기본값을 null로 설정
        )
    }
}