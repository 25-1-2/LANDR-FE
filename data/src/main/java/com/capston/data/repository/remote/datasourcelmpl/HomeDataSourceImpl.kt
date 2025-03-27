package com.capston.data.repository.remote.datasourcelmpl

import android.util.Log
import com.capston.domain.base.BaseLoadingState
import com.capston.data.repository.remote.api.HomeApi
import com.capston.domain.datasource.HomeDataSource
import com.capston.domain.response.BaseResponse
import com.capston.domain.response.home.DistinctHomeIdResponse
import com.capston.domain.response.Result
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
        var result = homeApi.getDistinctHome()
        Log.d("HomeDataSourceImpl", "서버 응답: $result")
        if (result == null) {
            // payload가 null일 때 기본값을 제공
            val defaultPayload = DistinctHomeIdResponse(
                userProgress = UserProgressResponse(), // 빈 데이터로 기본값 설정
                todaySchedule = TodayScheduleResponse() // 빈 객체로 기본값 설정
            )
            result = defaultPayload
        }

        emit(result) // 정상적으로 응답이 왔을 때 emit
    }.catch { e ->
        // 예외 발생 시 메시지를 출력하도록 처리
        val errorMessage = e.message ?: "알 수 없는 오류 발생"
        Log.e("getDistinctHome", "예외 발생: $errorMessage")

        // JSON 파싱 함수 호출
        val parsedMessage = parseErrorMessage(errorMessage)
        val errorResponse = Result(code = 5000, message = parsedMessage)

        // BaseResponse로 감싸서 오류 상태를 emit
        val response = BaseResponse<DistinctHomeIdResponse>(
            result = errorResponse,
            payload = null,
            status = BaseLoadingState.ERROR
        )

//        emit(response)

        if (e is HttpException && e.code() == 404) {
            val errorResponse = BaseResponse<DistinctHomeIdResponse>(
                result = Result(
                    code = 3000,
                    message = "홈 정보가 존재하지 않습니다."
                ),
                payload = null,
                status = BaseLoadingState.ERROR // 상태를 ERROR로 설정
            )
            Log.e("getDistinctHome", "HTTP 404: ${errorResponse.result.message}")
//            emit(errorResponse) // 404 응답을 Flow로 emit
        } else {
            Log.e("getDistinctHome", "예외 발생: ${e.message}")
            throw e // 기타 예외는 재throw
        }
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

//    // JSON 파싱 함수
//    private fun parseErrorMessage(json: String): String {
//        return try {
//            val jsonObject = JSONObject(json)
//            jsonObject.getJSONObject("result").getString("message")
//        } catch (ex: JSONException) {
//            "파싱 오류"
//        }
//    }
}