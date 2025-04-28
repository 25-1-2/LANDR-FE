package com.capston.data.repository.remote.datasourcelmpl

import android.util.Log
import com.capston.data.repository.remote.api.LectureApi
import com.capston.domain.datasource.LectureDataSource
import com.capston.domain.response.lecture.DistinctLectureResponse
import com.capston.domain.response.lecture.LectureResponseDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class LectureDataSourceImpl @Inject constructor(
    private val lectureApi: LectureApi
) : LectureDataSource {

    override suspend fun getDistinctLecture(): Flow<DistinctLectureResponse> = flow {
        var result = lectureApi.getDistinctLecture()
        Log.d("LectureDataSourceImpl", "서버 응답: $result")
        if (result == null) {
            val defaultPayload = DistinctLectureResponse(
                data = LectureResponseDto(),
            )
            result = defaultPayload
        }
        emit(result)
    }.catch { e ->
        val errorMessage = e.message ?: "알 수 없는 오류 발생"
        Log.e("getDistinctLecture", "예외 발생: $errorMessage")

        // 오류 처리
    }

    override suspend fun getAllLecture(): Flow<DistinctLectureResponse> = flow {
        var result = lectureApi.getAllLecture()
        Log.d("LectureDataSourceImpl", "서버 응답: $result")
        if (result == null) {
            val defaultPayload = DistinctLectureResponse(
                data = LectureResponseDto(),
            )
            result = defaultPayload
        }
        emit(result)
    }.catch { e ->
        val errorMessage = e.message ?: "알 수 없는 오류 발생"
        Log.e("getAllLecture", "예외 발생: $errorMessage")

        // 오류 처리
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