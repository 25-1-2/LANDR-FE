package com.capston.data.repository.remote.datasourcelmpl

import android.util.Log
import com.capston.domain.base.BaseLoadingState
import com.capston.data.repository.remote.api.HomeApi
import com.capston.domain.datasource.HomeDataSource
import com.capston.domain.response.BaseResponse
import com.capston.domain.response.CheckResponse
import com.capston.domain.response.home.DistinctHomeIdResponse
import com.capston.domain.response.BaseResult
import com.capston.domain.response.home.TodayScheduleResponse
import com.capston.domain.response.home.UserProgressResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import org.json.JSONException
import org.json.JSONObject
import retrofit2.HttpException
import javax.inject.Inject

class HomeDataSourceImpl @Inject constructor(
    private val homeApi: HomeApi
) : HomeDataSource {

    override suspend fun getDistinctHome(): Flow<DistinctHomeIdResponse> = flow {
        try {
            val response = homeApi.getDistinctHome()
            Log.d("HomeDataSourceImpl", "서버 응답: $response")

            // response가 null인 경우 기본값 생성
            val result = response ?: DistinctHomeIdResponse(
                userProgress = UserProgressResponse(),
                todaySchedule = TodayScheduleResponse()
            )

            // 중요: response가 null이 아니지만 todaySchedule이 null일 경우
            if (response != null && response.todaySchedule == null) {
                // 빈 TodayScheduleResponse를 설정하되 UserProgress는 유지
                emit(DistinctHomeIdResponse(
                    userProgress = response.userProgress,
                    todaySchedule = TodayScheduleResponse()  // 기본 빈 객체
                ))
            } else {
                emit(result)
            }
        } catch (e: Exception) {
            // 예외 발생 시 로그 출력 후 기본값 emit
            val errorMessage = e.message ?: "알 수 없는 오류 발생"
            Log.e("getDistinctHome", "예외 발생: $errorMessage", e)

            // 기본 응답 객체 생성하여 emit
            emit(DistinctHomeIdResponse(
                userProgress = UserProgressResponse(),
                todaySchedule = TodayScheduleResponse()
            ))
        }
    }

    override suspend fun patchLessonSchedulesCheckToggle(lessonScheduleId: Int): Flow<CheckResponse>
            = flow {
        val result = homeApi.patchLessonSchedulesCheckToggle(lessonScheduleId)
        emit(result)
    }.catch { e ->
        Log.e("patchLessonSchedulesCheckToggle 에러", e.message.toString())
    }

    private fun parseErrorMessage(json: String): String {
        return try {
            val jsonObject = JSONObject(json)
            if (jsonObject.has("result")) {
                val resultObject = jsonObject.getJSONObject("result")
                resultObject.optString("message", "서버에서 메시지를 제공하지 않았습니다.")
            } else {
                "서버 응답 형식이 다릅니다."
            }
        } catch (ex: JSONException) {
            "JSON 파싱 오류: ${ex.message}"
        }
    }
}