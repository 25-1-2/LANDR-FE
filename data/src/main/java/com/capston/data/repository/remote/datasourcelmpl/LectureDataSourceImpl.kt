package com.capston.data.repository.remote.datasourcelmpl

import android.util.Log
import com.capston.data.repository.remote.api.LectureApi
import com.capston.domain.datasource.LectureDataSource
import com.capston.domain.request.LectureDto
import com.capston.domain.response.lecture.DistinctLectureResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class LectureDataSourceImpl @Inject constructor(
    private val lectureApi: LectureApi
) : LectureDataSource {

    override suspend fun getDistinctLecture(lectureDto: LectureDto): Flow<DistinctLectureResponse> = flow {
        // API 호출 전에 로그 추가
        Log.d("LectureDataSourceImpl", "API 호출: search=${lectureDto.search}, " +
                "cursorLectureId=${lectureDto.cursorLectureId}, " +
                "cursorCreatedAt=${lectureDto.cursorCreatedAt}, " +
                "offset=${lectureDto.offset}")

        // cursorCreatedAt이 null이면 빈 문자열로 처리
        val createdAt = if (lectureDto.cursorCreatedAt.isNullOrEmpty()) {
            // 빈 값일 때 대체값 생성 - 현재 시간보다 먼 미래 날짜로 설정하여 영향이 없도록
            "2099-12-31T23:59:59.999999"
        } else {
            lectureDto.cursorCreatedAt
        }

        var result = lectureApi.getDistinctLecture(
            search = lectureDto.search,
            cursorLectureId = lectureDto.cursorLectureId,
            cursorCreatedAt = createdAt, // 안전한 값 사용
            offset = lectureDto.offset
        )

        Log.d("LectureDataSourceImpl", "서버 응답: $result")

        // 응답 후처리 - nextCreateAt이 null인 경우 처리
        if (result != null && result.nextCreatedAt == null && result.data?.isNotEmpty() == true) {
            // 가장 최근 항목의 createdAt 값을 가져와 nextCreateAt 대체
            val lastItemCreatedAt = result.data?.lastOrNull()?.createdAt

            // 응답 객체 복사하면서 값 수정 (DistinctLectureResponse가 data class인 경우)
            result = result.copy(
                nextCreatedAt = lastItemCreatedAt ?: "2025-01-01T00:00:00.000000"
            )

            Log.d("LectureDataSourceImpl", "nextCreateAt null 수정: $lastItemCreatedAt")
        }

        if (result == null) {
            val defaultPayload = DistinctLectureResponse(
                data = emptyList(),
            )
            result = defaultPayload
        }

        emit(result)
    }.catch { e ->
        val errorMessage = e.message ?: "알 수 없는 오류 발생"
        Log.e("getDistinctLecture", "예외 발생: $errorMessage")
        // 오류 처리
    }

    override suspend fun getAllLecture(lectureDto: LectureDto): Flow<DistinctLectureResponse> = flow {
        // API 호출 전에 로그 추가
        Log.d("LectureDataSourceImpl", "전체 목록 API 호출: " +
                "cursorLectureId=${lectureDto.cursorLectureId}, " +
                "cursorCreatedAt=${lectureDto.cursorCreatedAt}, " +
                "offset=${lectureDto.offset}")

        // cursorCreatedAt이 null이면 빈 문자열로 처리
        val createdAt = if (lectureDto.cursorCreatedAt.isNullOrEmpty()) {
            // 빈 값일 때 대체값 생성 - 현재 시간보다 먼 미래 날짜로 설정하여 영향이 없도록
            "2099-12-31T23:59:59.999999"
        } else {
            lectureDto.cursorCreatedAt
        }

        var result = lectureApi.getDistinctLecture(
            search = lectureDto.search,
            cursorLectureId = lectureDto.cursorLectureId,
            cursorCreatedAt = createdAt, // 안전한 값 사용
            offset = lectureDto.offset
        )

        Log.d("LectureDataSourceImpl", "전체 목록 서버 응답: $result")

        // 응답 후처리 - nextCreateAt이 null인 경우 처리
        if (result != null && result.nextCreatedAt == null && result.data?.isNotEmpty() == true) {
            // 가장 최근 항목의 createdAt 값을 가져와 nextCreateAt 대체
            val lastItemCreatedAt = result.data?.lastOrNull()?.createdAt

            // 응답 객체 복사하면서 값 수정 (DistinctLectureResponse가 data class인 경우)
            result = result.copy(
                nextCreatedAt = lastItemCreatedAt ?: "2025-01-01T00:00:00.000000"
            )

            Log.d("LectureDataSourceImpl", "전체 목록 nextCreateAt null 수정: $lastItemCreatedAt")
        }

        if (result == null) {
            val defaultPayload = DistinctLectureResponse(
                data = emptyList(),
            )
            result = defaultPayload
        }

        emit(result)
    }.catch { e ->
        val errorMessage = e.message ?: "알 수 없는 오류 발생"
        Log.e("getAllLecture", "예외 발생: $errorMessage")
        // 오류 처리
    }
}