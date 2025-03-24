package com.capston.data.repository.remote.datasourcelmpl

import android.util.Log
import com.capston.domain.base.BaseLoadingState
import com.capston.data.repository.remote.api.HomeApi
import com.capston.domain.datasource.HomeDataSource
import com.capston.domain.response.BaseResponse
import com.capston.domain.response.DistinctHomeIdResponse
import com.capston.domain.response.Result
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

    override suspend fun getDistinctHome(): Flow<BaseResponse<DistinctHomeIdResponse>> = flow {
        val result = homeApi.getDistinctHome()
        emit(result) // 정상적으로 응답이 왔을 때 emit
    }.catch { e ->
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
            emit(errorResponse) // 404 응답을 Flow로 emit
        } else {
            Log.e("getDistinctHome", "예외 발생: ${e.message}")
            throw e // 기타 예외는 재throw
        }
    }

    // JSON 파싱 함수
    private fun parseErrorMessage(json: String): String {
        return try {
            val jsonObject = JSONObject(json)
            jsonObject.getJSONObject("result").getString("message")
        } catch (ex: JSONException) {
            "파싱 오류"
        }
    }
}