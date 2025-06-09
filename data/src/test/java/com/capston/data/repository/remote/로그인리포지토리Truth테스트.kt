package com.capston.data.repository.remote

import com.capston.data.repository.remote.repositoryImpl.LoginRepositoryImpl
import com.capston.domain.datasource.LoginDataSource
import com.capston.domain.request.LoginDto
import com.capston.domain.request.UserNameDto
import com.capston.domain.response.user.LoginResponse
import com.capston.domain.response.user.UserProfileResponse
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class 로그인리포지토리Truth테스트 {

    private val 로그인데이터소스: LoginDataSource = mockk()
    private lateinit var 로그인리포지토리: LoginRepositoryImpl

    @Before
    fun setUp() {
        로그인리포지토리 = LoginRepositoryImpl(로그인데이터소스)
    }

    @Test
    fun `Truth 라이브러리 사용 로그인 테스트`() = runTest {
        // Given
        val 로그인정보 = LoginDto("test@email.com", "Test User", "fcm_token")
        val expectedResponse = LoginResponse("access_token")

        coEvery { 로그인데이터소스.postLoginInfo(로그인정보) } returns expectedResponse

        // When
        val result = 로그인리포지토리.postLoginInfo(로그인정보)

        // Then - Truth 사용 (더 읽기 쉬움)
        assertThat(result.token).isEqualTo("access_token")
        assertThat(result.token).isNotEmpty()
        assertThat(result.token).startsWith("access")
        assertThat(result.token).hasLength(12)

        coVerify { 로그인데이터소스.postLoginInfo(로그인정보) }
    }

    @Test
    fun `Truth 라이브러리 사용 사용자 프로필 조회 테스트`() = runTest {
        // Given
        val expectedProfile = UserProfileResponse(
            id = 1,
            email = "test@example.com",
            name = "Test User"
        )

        coEvery { 로그인데이터소스.getUserProfile() } returns expectedProfile

        // When
        val result = 로그인리포지토리.getUserProfile()

        // Then - Truth 사용
        assertThat(result).isNotNull()
        assertThat(result.email).isEqualTo("test@example.com")
        assertThat(result.name).isEqualTo("Test User")
        assertThat(result.id).isEqualTo(1)
        assertThat(result.id).isAtLeast(1)
        assertThat(result.email).contains("@")
        assertThat(result.email).endsWith(".com")

        coVerify { 로그인데이터소스.getUserProfile() }
    }

    @Test
    fun `Truth 라이브러리 사용 사용자 이름 수정 테스트`() = runTest {
        // Given
        val 유저이름 = UserNameDto(name = "업데이트된 이름")
        val 예상결과 = UserProfileResponse(
            id = 1,
            email = "test@example.com",
            name = "업데이트된 이름"
        )

        coEvery { 로그인데이터소스.patchUserName(유저이름) } returns 예상결과

        // When
        val result = 로그인리포지토리.patchUserName(유저이름)

        // Then - Truth 사용
        assertThat(result.name).isEqualTo("업데이트된 이름")
        assertThat(result.name).isNotEqualTo("테스트 유저")
        assertThat(result.name).isNotEmpty()
        assertThat(result.name).contains("업데이트")

        coVerify { 로그인데이터소스.patchUserName(유저이름) }
    }

    @Test
    fun `Truth 라이브러리 사용 로그인 실패 예외 테스트`() = runTest {
        // Given
        val 로그인정보 = LoginDto("invalid@email.com", "Test User", "fcm_token")
        val 예외 = RuntimeException("로그인 실패")

        coEvery { 로그인데이터소스.postLoginInfo(로그인정보) } throws 예외

        // When & Then - Truth 사용
        try {
            로그인리포지토리.postLoginInfo(로그인정보)
            assertThat(true).isFalse() // 예외가 발생해야 하므로 여기 도달하면 안됨
        } catch (e: RuntimeException) {
            assertThat(e.message).isEqualTo("로그인 실패")
            assertThat(e.message).contains("실패")
            assertThat(e).isInstanceOf(RuntimeException::class.java)
        }

        coVerify { 로그인데이터소스.postLoginInfo(로그인정보) }
    }

    @Test
    fun `Truth 라이브러리로 빈 토큰 검증 테스트`() = runTest {
        // Given
        val 로그인정보 = LoginDto("test@email.com", "테스트 유저", "fcm_token")
        val 빈토큰결과 = LoginResponse("")

        coEvery { 로그인데이터소스.postLoginInfo(로그인정보) } returns 빈토큰결과

        // When
        val result = 로그인리포지토리.postLoginInfo(로그인정보)

        // Then - Truth로 빈 값 검증
        assertThat(result.token).isEmpty()
        assertThat(result.token).isEqualTo("")
        assertThat(result.token).hasLength(0)

        coVerify { 로그인데이터소스.postLoginInfo(로그인정보) }
    }
}