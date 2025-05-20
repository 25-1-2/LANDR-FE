package com.capston.data.repository.remote.datasourcelmpl

import android.util.Log
import com.capston.data.repository.remote.api.LectureApi
import com.capston.domain.datasource.LectureDataSource
import com.capston.domain.model.NewPlanLesson
import com.capston.domain.request.LectureDto
import com.capston.domain.response.lecture.DistinctLectureResponse
import com.capston.domain.response.lecture.GetLessonsByLectureIdResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class LectureDataSourceImpl @Inject constructor(
    private val lectureApi: LectureApi
) : LectureDataSource {

    override suspend fun getDistinctLecture(lectureDto: LectureDto): Flow<DistinctLectureResponse> = flow {
        // API 호출 전에 로그 추가 - 더 자세한 정보 포함
        Log.d("LectureDataSourceImpl", "API 호출: search='${lectureDto.search}', " +
                "cursorLectureId=${lectureDto.cursorLectureId}, " +
                "cursorCreatedAt=${lectureDto.cursorCreatedAt}, " +
                "offset=${lectureDto.offset}, " +
                "platform=${lectureDto.platform?.label ?: "없음"}, " +
                "subject=${lectureDto.subject?.label ?: "없음"}")

        // cursorCreatedAt이 null이면 빈 문자열로 처리
        val createdAt = if (lectureDto.cursorCreatedAt.isNullOrEmpty()) {
            // 빈 값일 때 대체값 생성 - 현재 시간보다 먼 미래 날짜로 설정하여 영향이 없도록
            "2099-12-31T23:59:59.999999"
        } else {
            lectureDto.cursorCreatedAt
        }

        // 수정된 부분: filter 조건을 개선하고 API 호출 부분을 직접 사용
        var result: DistinctLectureResponse? = null

        try {
            // 직접 API 호출 - platform과 subject가 모두 null인 경우도 처리
            result = lectureApi.getDistinctLecture(
                search = lectureDto.search,
                cursorLectureId = lectureDto.cursorLectureId,
                cursorCreatedAt = createdAt,
                offset = lectureDto.offset,
                platform = lectureDto.platform,  // 이제 nullable 값 직접 전달
                subject = lectureDto.subject     // 이제 nullable 값 직접 전달
            )

            Log.d("LectureDataSourceImpl", "API 호출 성공: ${result.data?.size ?: 0}개 항목")
        } catch (e: Exception) {
            Log.e("LectureDataSourceImpl", "API 호출 오류: ${e.message}", e)
            // 오류 발생시 빈 응답 생성
            result = DistinctLectureResponse(data = emptyList())
        }

        // 이하 기존 코드와 동일...
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
    }

    override suspend fun getAllLecture(lectureDto: LectureDto): Flow<DistinctLectureResponse> = flow {
        // API 호출 전에 로그 추가 - 더 자세한 정보 포함
        Log.d("LectureDataSourceImpl", "전체 목록 API 호출: " +
                "search='${lectureDto.search}', " +
                "cursorLectureId=${lectureDto.cursorLectureId}, " +
                "cursorCreatedAt=${lectureDto.cursorCreatedAt}, " +
                "offset=${lectureDto.offset}, " +
                "platform=${lectureDto.platform?.label ?: "없음"}, " +
                "subject=${lectureDto.subject?.label ?: "없음"}")

        // cursorCreatedAt이 null이면 빈 문자열로 처리
        val createdAt = if (lectureDto.cursorCreatedAt.isNullOrEmpty()) {
            // 빈 값일 때 대체값 생성
            "2099-12-31T23:59:59.999999"
        } else {
            lectureDto.cursorCreatedAt
        }

        // 직접 API 호출 - platform과 subject가 모두 null인 경우도 처리
        var result: DistinctLectureResponse? = null

        try {
            result = lectureApi.getDistinctLecture(
                search = lectureDto.search,
                cursorLectureId = lectureDto.cursorLectureId,
                cursorCreatedAt = createdAt,
                offset = lectureDto.offset,
                platform = lectureDto.platform,  // 이제 nullable 값 직접 전달
                subject = lectureDto.subject     // 이제 nullable 값 직접 전달
            )

            Log.d("LectureDataSourceImpl", "전체 목록 API 호출 성공: ${result.data?.size ?: 0}개 항목")
        } catch (e: Exception) {
            Log.e("LectureDataSourceImpl", "전체 목록 API 호출 오류: ${e.message}", e)
            // 오류 발생시 빈 응답 생성
            result = DistinctLectureResponse(data = emptyList())
        }

        // 이하 기존 코드와 동일...
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
    }

    override suspend fun getLessonsByLectureId(lectureId: Int): GetLessonsByLectureIdResponse {
        return lectureApi.getLessonsByLectureId(lectureId)
    }
}