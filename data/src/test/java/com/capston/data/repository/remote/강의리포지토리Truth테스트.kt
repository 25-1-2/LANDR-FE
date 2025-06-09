package com.capston.data.repository.remote

import com.capston.data.repository.remote.repositoryImpl.LectureRepositoryImpl
import com.capston.domain.datasource.LectureDataSource
import com.capston.domain.model.Lesson
import com.capston.domain.request.LectureDto
import com.capston.domain.response.enum_class.Platform
import com.capston.domain.response.enum_class.Subject
import com.capston.domain.response.lecture.DistinctLectureResponse
import com.capston.domain.response.lecture.GetLessonsByLectureIdResponse
import com.capston.domain.response.lecture.LectureResponseDto
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class 강의리포지토리Truth테스트 {

    private val 강의데이터소스: LectureDataSource = mockk()
    private lateinit var 강의리포지토리: LectureRepositoryImpl

    @Before
    fun 설정() {
        강의리포지토리 = LectureRepositoryImpl(강의데이터소스)
    }

    @Test
    fun `Truth 라이브러리 사용 강의 단건 조회 테스트`() = runTest {
        // Given - 주어진 조건
        val 강의요청 = LectureDto(
            search = "수학",
            cursorLectureId = "",
            cursorCreatedAt = null,
            offset = "10",
            platform = Platform.MEGA,
            subject = Subject.MATH
        )

        val 강의목록 = listOf(
            LectureResponseDto(
                id = 1,
                title = "기초 수학 완전 정복",
                teacher = "김수학",
                platform = Platform.MEGA,
                subject = Subject.MATH,
                tag = "",
                createdAt = "2024-01-15T09:00:00.000000",
                totalLessons = 0
            ),
            LectureResponseDto(
                id = 2,
                title = "고등 수학 마스터",
                teacher = "박수학",
                platform = Platform.MEGA,
                subject = Subject.MATH,
                tag = "",
                createdAt = "2024-01-10T10:00:00.000000",
                totalLessons = 10
            )
        )

        val 예상응답 = DistinctLectureResponse(
            data = 강의목록,
            nextCursor = 2,
            nextCreatedAt = "2024-01-10T10:00:00.000000"
        )

        coEvery { 강의데이터소스.getDistinctLecture(강의요청) } returns flowOf(예상응답)

        // When - 실행
        강의리포지토리.getDistinctLecture(강의요청).collect { 결과 ->
            // Then - Truth 사용 검증
            assertThat(결과).isNotNull()
            assertThat(결과.data).hasSize(2)
            assertThat(결과.nextCursor).isEqualTo(2)
            assertThat(결과.nextCreatedAt).isEqualTo("2024-01-10T10:00:00.000000")

            // 첫 번째 강의 검증
            val 첫번째강의 = 결과.data!![0]
            assertThat(첫번째강의.id).isEqualTo(1)
            assertThat(첫번째강의.title).isEqualTo("기초 수학 완전 정복")
            assertThat(첫번째강의.title).contains("수학")
            assertThat(첫번째강의.teacher).isEqualTo("김수학")
            assertThat(첫번째강의.platform).isEqualTo(Platform.MEGA)
            assertThat(첫번째강의.subject).isEqualTo(Subject.MATH)

            // 두 번째 강의 검증
            val 두번째강의 = 결과.data!![1]
            assertThat(두번째강의.id).isEqualTo(2)
            assertThat(두번째강의.title).contains("고등")
            assertThat(두번째강의.teacher).isEqualTo("박수학")
        }

        coVerify { 강의데이터소스.getDistinctLecture(강의요청) }
    }

    @Test
    fun `Truth 라이브러리 사용 강의 전체 조회 테스트`() = runTest {
        // Given - 주어진 조건
        val 강의요청 = LectureDto(
            search = "",
            cursorLectureId = "",
            cursorCreatedAt = null,
            offset = "10",
            platform = null,
            subject = null
        )

        val 강의목록 = listOf(
            LectureResponseDto(
                id = 1,
                title = "기초 수학 완전 정복",
                teacher = "김수학",
                platform = Platform.MEGA,
                subject = Subject.MATH,
                tag = "",
                createdAt = "2024-01-15T09:00:00.000000",
                totalLessons = 0
            ),
            LectureResponseDto(
                id = 2,
                title = "고등 수학 마스터",
                teacher = "박수학",
                platform = Platform.MEGA,
                subject = Subject.MATH,
                tag = "",
                createdAt = "2024-01-10T10:00:00.000000",
                totalLessons = 10
            ),
            LectureResponseDto(
                id = 3,
                title = "고등 국어 마스터",
                teacher = "박국어",
                platform = Platform.MEGA,
                subject = Subject.KOR,
                tag = "",
                createdAt = "2024-01-10T10:00:00.000000",
                totalLessons = 10
            )
        )

        val 예상응답 = DistinctLectureResponse(
            data = 강의목록,
            nextCursor = 3,
            nextCreatedAt = "2024-01-05T11:00:00.000000"
        )

        coEvery { 강의데이터소스.getAllLecture(강의요청) } returns flowOf(예상응답)

        // When - 실행
        강의리포지토리.getAllLecture(강의요청).collect { 결과 ->
            // Then - Truth 사용 검증
            assertThat(결과).isNotNull()
            assertThat(결과.data).hasSize(3)
            assertThat(결과.nextCursor).isEqualTo(3)
            assertThat(결과.nextCreatedAt).isEqualTo("2024-01-05T11:00:00.000000")

            // 다양한 플랫폼과 과목 확인
            val 메가강의 = 결과.data!!.filter { it.platform == Platform.MEGA }
            assertThat(메가강의).hasSize(2)

            val 이투스강의 = 결과.data!!.filter { it.platform == Platform.ETOOS }
            assertThat(이투스강의).hasSize(1)

            val 수학강의 = 결과.data!!.filter { it.subject == Subject.MATH }
            assertThat(수학강의).hasSize(2)

            val 영어강의 = 결과.data!!.filter { it.subject == Subject.ENG }
            assertThat(영어강의).hasSize(1)

            // 영어 강의 세부 검증
            val 영어강의상세 = 영어강의.first()
            assertThat(영어강의상세.title).isEqualTo("영어 독해 비법")
            assertThat(영어강의상세.teacher).isEqualTo("이영어")
        }

        coVerify { 강의데이터소스.getAllLecture(강의요청) }
    }

    @Test
    fun `Truth 라이브러리 사용 특정 강의의 레슨 조회 테스트`() = runTest {
        // Given - 주어진 조건
        val 강의아이디 = 1
        val 예상응답 = GetLessonsByLectureIdResponse(
            lessons = listOf(
                Lesson(
                    id = 1,
                    title = "1강. 기초 개념 이해하기",
                    duration = 45,
                    order = 1
                ),
                Lesson(
                    id = 2,
                    title = "2강. 응용 문제 풀이",
                    duration = 50,
                    order = 2
                ),
                Lesson(
                    id = 3,
                    title = "3강. 심화 문제 접근법",
                    duration = 55,
                    order = 3
                )
            )
        )

        coEvery { 강의데이터소스.getLessonsByLectureId(강의아이디) } returns 예상응답

        // When - 실행
        val 결과 = 강의리포지토리.getLessonsByLectureId(강의아이디)

        // Then - Truth 사용 검증
        assertThat(결과).isNotNull()
        assertThat(결과.lessons).hasSize(3)

        // 레슨 순서 확인
        // assertThat(결과.lessons).isInOrder { l1, l2 -> l1.order - l2.order }

        // 첫 번째 레슨 검증
        val 첫번째레슨 = 결과.lessons[0]
        assertThat(첫번째레슨.id).isEqualTo(1)
        assertThat(첫번째레슨.title).isEqualTo("1강. 기초 개념 이해하기")
        assertThat(첫번째레슨.title).startsWith("1강")
        assertThat(첫번째레슨.duration).isEqualTo(45)
        assertThat(첫번째레슨.order).isEqualTo(1)

        // 세 번째 레슨 검증
        val 세번째레슨 = 결과.lessons[2]
        assertThat(세번째레슨.id).isEqualTo(3)
        assertThat(세번째레슨.title).contains("심화")
        assertThat(세번째레슨.duration).isEqualTo(55)
        assertThat(세번째레슨.order).isEqualTo(3)

        // 레슨 길이의 증가 패턴 확인
        for (i in 0 until 결과.lessons.size - 1) {
            assertThat(결과.lessons[i].duration).isLessThan(결과.lessons[i+1].duration)
        }

        coVerify { 강의데이터소스.getLessonsByLectureId(강의아이디) }
    }

    @Test
    fun `Truth 라이브러리 사용 빈 강의 목록 테스트`() = runTest {
        // Given - 주어진 조건
        val 강의요청 = LectureDto(
            search = "존재하지않는강의",
            cursorLectureId = "",
            cursorCreatedAt = null,
            offset = "10",
            platform = null,
            subject = null
        )

        val 빈응답 = DistinctLectureResponse(
            data = emptyList(),
            nextCursor = 0,
            nextCreatedAt = ""
        )

        coEvery { 강의데이터소스.getDistinctLecture(강의요청) } returns flowOf(빈응답)

        // When - 실행
        강의리포지토리.getDistinctLecture(강의요청).collect { 결과 ->
            // Then - Truth로 빈 값 검증
            assertThat(결과.data).isEmpty()
            assertThat(결과.data).hasSize(0)
            assertThat(결과.nextCursor).isNull()
            assertThat(결과.nextCreatedAt).isNull()
        }

        coVerify { 강의데이터소스.getDistinctLecture(강의요청) }
    }

    @Test
    fun `Truth 라이브러리 사용 검색어로 강의 필터링 테스트`() = runTest {
        // Given - 주어진 조건
        val 검색어 = "프로그래밍"
        val 강의요청 = LectureDto(
            search = 검색어,
            cursorLectureId = "",
            cursorCreatedAt = null,
            offset = "10",
            platform = null,
            subject = null
        )

        val 강의목록 = listOf(
            LectureResponseDto(
                id = 1,
                title = "기초 수학 완전 정복",
                teacher = "김수학",
                platform = Platform.MEGA,
                subject = Subject.MATH,
                tag = "",
                createdAt = "2024-01-15T09:00:00.000000",
                totalLessons = 0
            ),
            LectureResponseDto(
                id = 2,
                title = "고등 수학 마스터",
                teacher = "박수학",
                platform = Platform.MEGA,
                subject = Subject.MATH,
                tag = "",
                createdAt = "2024-01-10T10:00:00.000000",
                totalLessons = 10
            ),
            LectureResponseDto(
                id = 3,
                title = "고등 국어 마스터",
                teacher = "박국어",
                platform = Platform.MEGA,
                subject = Subject.MATH,
                tag = "",
                createdAt = "2024-01-10T10:00:00.000000",
                totalLessons = 10
            )
        )

        val 예상응답 = DistinctLectureResponse(
            data = 강의목록,
            nextCursor = 6,
            nextCreatedAt = "2024-01-18T10:00:00.000000"
        )

        coEvery { 강의데이터소스.getDistinctLecture(강의요청) } returns flowOf(예상응답)

        // When - 실행
        강의리포지토리.getDistinctLecture(강의요청).collect { 결과 ->
            // Then - Truth 사용 검증
            assertThat(결과.data).hasSize(2)

            // 모든 강의가 검색어를 포함하는지 확인
            결과.data!!.forEach { 강의 ->
                assertThat(강의.title).contains(검색어)
                assertThat(강의.subject).isEqualTo(Subject.MATH)
            }

            // 첫 번째 강의 검증
            val 첫번째강의 = 결과.data!![0]
            assertThat(첫번째강의.title).isEqualTo("자바 프로그래밍 기초")
            assertThat(첫번째강의.title).contains("자바")
            assertThat(첫번째강의.teacher).isEqualTo("조자바")

            // 두 번째 강의 검증
            val 두번째강의 = 결과.data!![1]
            assertThat(두번째강의.title).isEqualTo("파이썬 프로그래밍 마스터")
            assertThat(두번째강의.title).contains("파이썬")
            assertThat(두번째강의.teacher).isEqualTo("김파이썬")
        }

        coVerify { 강의데이터소스.getDistinctLecture(강의요청) }
    }

    @Test
    fun `Truth 라이브러리 사용 플랫폼 필터링 테스트`() = runTest {
        // Given - 주어진 조건
        val 강의요청 = LectureDto(
            search = "",
            cursorLectureId = "",
            cursorCreatedAt = null,
            offset = "10",
            platform = Platform.ETOOS,
            subject = null
        )

        val 강의목록 = listOf(
            LectureResponseDto(
                id = 3,
                title = "영어 독해 비법",
                teacher = "이영어",
                platform = Platform.ETOOS,
                subject = Subject.ENG,
                tag = "",
                createdAt = "2024-01-05T11:00:00.000000"
            ),
            LectureResponseDto(
                id = 6,
                title = "과학과학",
                teacher = "김과학",
                platform = Platform.ETOOS,
                subject = Subject.SCI,
                createdAt = "2024-01-18T10:00:00.000000"
            )
        )

        val 예상응답 = DistinctLectureResponse(
            data = 강의목록,
            nextCursor = 6,
            nextCreatedAt = "2024-01-18T10:00:00.000000"
        )

        coEvery { 강의데이터소스.getDistinctLecture(강의요청) } returns flowOf(예상응답)

        // When - 실행
        강의리포지토리.getDistinctLecture(강의요청).collect { 결과 ->
            // Then - Truth 사용 검증
            assertThat(결과.data).hasSize(2)

            // 모든 강의가 이투스 플랫폼인지 확인
            결과.data!!.forEach { 강의 ->
                assertThat(강의.platform).isEqualTo(Platform.ETOOS)
            }

            // 첫 번째 강의 검증
            val 첫번째강의 = 결과.data!![0]
            assertThat(첫번째강의.subject).isEqualTo(Subject.ENG)

            // 두 번째 강의 검증
            val 두번째강의 = 결과.data!![1]
            assertThat(두번째강의.subject).isEqualTo(Subject.SCI)
        }

        coVerify { 강의데이터소스.getDistinctLecture(강의요청) }
    }

    @Test
    fun `Truth 라이브러리 사용 레슨이 없는 강의 테스트`() = runTest {
        // Given - 주어진 조건
        val 강의아이디 = 99
        val 빈레슨응답 = GetLessonsByLectureIdResponse(
            lessons = emptyList()
        )

        coEvery { 강의데이터소스.getLessonsByLectureId(강의아이디) } returns 빈레슨응답

        // When - 실행
        val 결과 = 강의리포지토리.getLessonsByLectureId(강의아이디)

        // Then - Truth 사용 검증
        assertThat(결과.lessons).isEmpty()
        assertThat(결과.lessons).hasSize(0)

        coVerify { 강의데이터소스.getLessonsByLectureId(강의아이디) }
    }

    @Test
    fun `Truth 라이브러리 사용 잘못된 강의 ID 예외 테스트`() = runTest {
        // Given - 주어진 조건
        val 잘못된강의아이디 = -1
        val 예외 = RuntimeException("존재하지 않는 강의입니다")

        coEvery { 강의데이터소스.getLessonsByLectureId(잘못된강의아이디) } throws 예외

        // When & Then - Truth 사용
        try {
            강의리포지토리.getLessonsByLectureId(잘못된강의아이디)
            assertThat(true).isFalse() // 예외가 발생해야 하므로 여기 도달하면 안됨
        } catch (e: RuntimeException) {
            assertThat(e.message).isEqualTo("존재하지 않는 강의입니다")
            assertThat(e.message).contains("존재하지 않는")
            assertThat(e.message).contains("강의")
            assertThat(e).isInstanceOf(RuntimeException::class.java)
        }

        coVerify { 강의데이터소스.getLessonsByLectureId(잘못된강의아이디) }
    }
}