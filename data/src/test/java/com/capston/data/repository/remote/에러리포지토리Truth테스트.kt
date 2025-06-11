package com.capston.data.repository.remote

import com.capston.data.repository.remote.repositoryImpl.ErrorRepositoryImpl
import com.capston.domain.base.Result
import com.capston.domain.datasource.ErrorDataSource
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class 에러리포지토리Truth테스트 {

    private val 에러데이터소스: ErrorDataSource = mockk()
    private lateinit var 에러리포지토리: ErrorRepositoryImpl

    @Before
    fun 설정() {
        에러리포지토리 = ErrorRepositoryImpl(에러데이터소스)
    }

    @Test
    fun `Truth 라이브러리 사용 API 예외 발생 테스트`() = runTest {
        // Given - 주어진 조건
        val 예상응답 = Result(
            code = 5000,
            message = "API 오류가 발생했습니다."
        )

        coEvery { 에러데이터소스.getExceptionApi() } returns flowOf(예상응답)

        // When - 실행
        에러리포지토리.getExceptionApi().collect { 결과 ->
            // Then - Truth 사용 검증
            assertThat(결과).isNotNull()
            assertThat(결과.code).isEqualTo(5000)
            assertThat(결과.message).isEqualTo("API 오류가 발생했습니다.")
            assertThat(결과.message).contains("API")
            assertThat(결과.message).contains("오류")
            assertThat(결과.message).isNotEmpty()
            assertThat(결과.code).isGreaterThan(0)
        }

        coVerify { 에러데이터소스.getExceptionApi() }
    }

    @Test
    fun `Truth 라이브러리 사용 네트워크 오류 테스트`() = runTest {
        // Given - 주어진 조건
        val 예상응답 = Result(
            code = 5001,
            message = "네트워크 연결이 불안정합니다."
        )

        coEvery { 에러데이터소스.getExceptionApi() } returns flowOf(예상응답)

        // When - 실행
        에러리포지토리.getExceptionApi().collect { 결과 ->
            // Then - Truth 사용 검증
            assertThat(결과).isNotNull()
            assertThat(결과.code).isEqualTo(5001)
            assertThat(결과.message).isEqualTo("네트워크 연결이 불안정합니다.")
            assertThat(결과.message).contains("네트워크")
            assertThat(결과.message).contains("불안정")
            assertThat(결과.message).endsWith(".")
        }

        coVerify { 에러데이터소스.getExceptionApi() }
    }

    @Test
    fun `Truth 라이브러리 사용 서버 오류 테스트`() = runTest {
        // Given - 주어진 조건
        val 예상응답 = Result(
            code = 5002,
            message = "서버에서 오류가 발생했습니다."
        )

        coEvery { 에러데이터소스.getExceptionApi() } returns flowOf(예상응답)

        // When - 실행
        에러리포지토리.getExceptionApi().collect { 결과 ->
            // Then - Truth 사용 검증
            assertThat(결과).isNotNull()
            assertThat(결과.code).isEqualTo(5002)
            assertThat(결과.message).isEqualTo("서버에서 오류가 발생했습니다.")
            assertThat(결과.message).contains("서버")
            assertThat(결과.message).contains("오류")
            assertThat(결과.message).endsWith("다.")
        }

        coVerify { 에러데이터소스.getExceptionApi() }
    }

    @Test
    fun `Truth 라이브러리 사용 인증 오류 테스트`() = runTest {
        // Given - 주어진 조건
        val 예상응답 = Result(
            code = 4001,
            message = "인증에 실패했습니다."
        )

        coEvery { 에러데이터소스.getExceptionApi() } returns flowOf(예상응답)

        // When - 실행
        에러리포지토리.getExceptionApi().collect { 결과 ->
            // Then - Truth 사용 검증
            assertThat(결과).isNotNull()
            assertThat(결과.code).isEqualTo(4001)
            assertThat(결과.message).isEqualTo("인증에 실패했습니다.")
            assertThat(결과.message).contains("인증")
            assertThat(결과.message).contains("실패")
            assertThat(결과.code).isLessThan(5000)
        }

        coVerify { 에러데이터소스.getExceptionApi() }
    }

    @Test
    fun `Truth 라이브러리 사용 권한 오류 테스트`() = runTest {
        // Given - 주어진 조건
        val 예상응답 = Result(
            code = 4003,
            message = "해당 리소스에 접근할 권한이 없습니다."
        )

        coEvery { 에러데이터소스.getExceptionApi() } returns flowOf(예상응답)

        // When - 실행
        에러리포지토리.getExceptionApi().collect { 결과 ->
            // Then - Truth 사용 검증
            assertThat(결과).isNotNull()
            assertThat(결과.code).isEqualTo(4003)
            assertThat(결과.message).isEqualTo("해당 리소스에 접근할 권한이 없습니다.")
            assertThat(결과.message).contains("리소스")
            assertThat(결과.message).contains("권한")
            assertThat(결과.message).contains("접근")
            assertThat(결과.message).endsWith("다.")
        }

        coVerify { 에러데이터소스.getExceptionApi() }
    }

    @Test
    fun `Truth 라이브러리 사용 성공 응답 테스트`() = runTest {
        // Given - 주어진 조건
        val 예상응답 = Result(
            code = 2000,
            message = "요청이 성공적으로 처리되었습니다."
        )

        coEvery { 에러데이터소스.getExceptionApi() } returns flowOf(예상응답)

        // When - 실행
        에러리포지토리.getExceptionApi().collect { 결과 ->
            // Then - Truth 사용 검증
            assertThat(결과).isNotNull()
            assertThat(결과.code).isEqualTo(2000)
            assertThat(결과.message).isEqualTo("요청이 성공적으로 처리되었습니다.")
            assertThat(결과.message).contains("성공적")
            assertThat(결과.message).contains("처리")
            assertThat(결과.code).isLessThan(3000)
            assertThat(결과.code).isGreaterThan(1000)
        }

        coVerify { 에러데이터소스.getExceptionApi() }
    }

    @Test
    fun `Truth 라이브러리 사용 비어 있는 응답 테스트`() = runTest {
        // Given - 주어진 조건
        val 빈응답 = Result(
            code = 0,
            message = ""
        )

        coEvery { 에러데이터소스.getExceptionApi() } returns flowOf(빈응답)

        // When - 실행
        에러리포지토리.getExceptionApi().collect { 결과 ->
            // Then - Truth로 빈 값 검증
            assertThat(결과).isNotNull()
            assertThat(결과.code).isEqualTo(0)
            assertThat(결과.message).isEmpty()
            assertThat(결과.message).hasLength(0)
        }

        coVerify { 에러데이터소스.getExceptionApi() }
    }

    @Test
    fun `Truth 라이브러리 사용 오류 발생 예외 테스트`() = runTest {
        // Given - 주어진 조건
        val 예외 = RuntimeException("API 호출 중 예외가 발생했습니다.")

        coEvery { 에러데이터소스.getExceptionApi() } throws 예외

        // When & Then - Truth 사용
        try {
            에러리포지토리.getExceptionApi().collect { }
            assertThat(true).isFalse() // 예외가 발생해야 하므로 여기 도달하면 안됨
        } catch (e: RuntimeException) {
            assertThat(e.message).isEqualTo("API 호출 중 예외가 발생했습니다.")
            assertThat(e.message).contains("API")
            assertThat(e.message).contains("예외")
            assertThat(e).isInstanceOf(RuntimeException::class.java)
        }

        coVerify { 에러데이터소스.getExceptionApi() }
    }
}