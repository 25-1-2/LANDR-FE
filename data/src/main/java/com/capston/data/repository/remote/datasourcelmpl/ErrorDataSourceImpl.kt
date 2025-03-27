package com.capston.data.repository.remote.datasourcelmpl

import android.util.Log
import com.capston.data.repository.remote.api.ErrorApi
import com.capston.data.repository.remote.api.HomeApi
import com.capston.domain.base.BaseLoadingState
import com.capston.domain.datasource.ErrorDataSource
import com.capston.domain.datasource.HomeDataSource
import com.capston.domain.response.BaseResponse
import com.capston.domain.response.DistinctHomeIdResponse
import com.capston.domain.response.Result
import com.capston.domain.response.TodayScheduleResponse
import com.capston.domain.response.UserProgressResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import org.json.JSONException
import org.json.JSONObject
import retrofit2.HttpException
import javax.inject.Inject

class ErrorDataSourceImpl @Inject constructor(
    private val errorApi: ErrorApi
) : ErrorDataSource {

    override suspend fun getExceptionApi(): Flow<com.capston.domain.base.Result> = flow {
        var result = errorApi.getExceptionApi()
        emit(result) // 정상적으로 응답이 왔을 때 emit
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