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

class LoginRepositoryTruthTest {

    private val loginDataSource: LoginDataSource = mockk()
    private lateinit var loginRepository: LoginRepositoryImpl

    @Before
    fun setUp() {
        loginRepository = LoginRepositoryImpl(loginDataSource)
    }

    @Test
    fun `Truth 라이브러리 사용 로그인 테스트`() = runTest {
        // Given
        val loginDto = LoginDto("test@email.com", "Test User", "fcm_token")
        val expectedResponse = LoginResponse("access_token")

        coEvery { loginDataSource.postLoginInfo(loginDto) } returns expectedResponse

        // When
        val result = loginRepository.postLoginInfo(loginDto)

        // Then - Truth 사용 (더 읽기 쉬움)
        assertThat(result.token).isEqualTo("access_token")
        assertThat(result.token).isNotEmpty()
        assertThat(result.token).startsWith("access")
        assertThat(result.token).hasLength(12)

        coVerify { loginDataSource.postLoginInfo(loginDto) }
    }

    @Test
    fun `Truth 라이브러리 사용 사용자 프로필 조회 테스트`() = runTest {
        // Given
        val expectedProfile = UserProfileResponse(
            id = 1,
            email = "test@example.com",
            name = "Test User"
        )

        coEvery { loginDataSource.getUserProfile() } returns expectedProfile

        // When
        val result = loginRepository.getUserProfile()

        // Then - Truth 사용
        assertThat(result).isNotNull()
        assertThat(result.email).isEqualTo("test@example.com")
        assertThat(result.name).isEqualTo("Test User")
        assertThat(result.id).isEqualTo(1)
        assertThat(result.id).isAtLeast(1)
        assertThat(result.email).contains("@")
        assertThat(result.email).endsWith(".com")

        coVerify { loginDataSource.getUserProfile() }
    }

    @Test
    fun `Truth 라이브러리 사용 사용자 이름 수정 테스트`() = runTest {
        // Given
        val userNameDto = UserNameDto(name = "Updated Name")
        val expectedResponse = UserProfileResponse(
            id = 1,
            email = "test@example.com",
            name = "Updated Name"
        )

        coEvery { loginDataSource.patchUserName(userNameDto) } returns expectedResponse

        // When
        val result = loginRepository.patchUserName(userNameDto)

        // Then - Truth 사용
        assertThat(result.name).isEqualTo("Updated Name")
        assertThat(result.name).isNotEqualTo("Test User")
        assertThat(result.name).isNotEmpty()
        assertThat(result.name).contains("Updated")

        coVerify { loginDataSource.patchUserName(userNameDto) }
    }

    @Test
    fun `Truth 라이브러리 사용 로그인 실패 예외 테스트`() = runTest {
        // Given
        val loginDto = LoginDto("invalid@email.com", "Test User", "fcm_token")
        val exception = RuntimeException("Login failed")

        coEvery { loginDataSource.postLoginInfo(loginDto) } throws exception

        // When & Then - Truth 사용
        try {
            loginRepository.postLoginInfo(loginDto)
            assertThat(true).isFalse() // 예외가 발생해야 하므로 여기 도달하면 안됨
        } catch (e: RuntimeException) {
            assertThat(e.message).isEqualTo("Login failed")
            assertThat(e.message).contains("failed")
            assertThat(e).isInstanceOf(RuntimeException::class.java)
        }

        coVerify { loginDataSource.postLoginInfo(loginDto) }
    }

    @Test
    fun `Truth 라이브러리로 빈 토큰 검증 테스트`() = runTest {
        // Given
        val loginDto = LoginDto("test@email.com", "Test User", "fcm_token")
        val emptyTokenResponse = LoginResponse("")

        coEvery { loginDataSource.postLoginInfo(loginDto) } returns emptyTokenResponse

        // When
        val result = loginRepository.postLoginInfo(loginDto)

        // Then - Truth로 빈 값 검증
        assertThat(result.token).isEmpty()
        assertThat(result.token).isEqualTo("")
        assertThat(result.token).hasLength(0)

        coVerify { loginDataSource.postLoginInfo(loginDto) }
    }
}