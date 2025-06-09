package com.capston.data.repository.remote

import com.capston.data.repository.remote.repositoryImpl.LoginRepositoryImpl
import com.capston.domain.datasource.LoginDataSource
import com.capston.domain.request.LoginDto
import com.capston.domain.request.UserNameDto
import com.capston.domain.response.user.LoginResponse
import com.capston.domain.response.user.UserProfileResponse
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

class LoginRepositoryImplTest {

    private val loginDataSource: LoginDataSource = mockk()
    private lateinit var loginRepository: LoginRepositoryImpl

    @Before
    fun setUp() {
        loginRepository = LoginRepositoryImpl(loginDataSource)
    }

    @Test
    fun `로그인 성공 시 토큰이 반환되는지 테스트`() = runTest {
        // Given
        val loginDto = LoginDto(
            email = "test@example.com",
            name = "Test User",
            fcmToken = "test_fcm_token"
        )
        val expectedResponse = LoginResponse(token = "test_access_token")

        coEvery { loginDataSource.postLoginInfo(loginDto) } returns expectedResponse

        // When
        val result = loginRepository.postLoginInfo(loginDto)

        // Then
        assertEquals(expectedResponse.token, result.token)
        coVerify { loginDataSource.postLoginInfo(loginDto) }
    }

    @Test
    fun `사용자 프로필 조회 테스트`() = runTest {
        // Given
        val expectedProfile = UserProfileResponse(
            id = 1,
            email = "test@example.com",
            name = "Test User"
        )

        coEvery { loginDataSource.getUserProfile() } returns expectedProfile

        // When
        val result = loginRepository.getUserProfile()

        // Then
        assertNotNull(result)
        assertEquals(expectedProfile.email, result.email)
        assertEquals(expectedProfile.name, result.name)
        coVerify { loginDataSource.getUserProfile() }
    }

    @Test
    fun `사용자 이름 수정 테스트`() = runTest {
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

        // Then
        assertEquals("Updated Name", result.name)
        coVerify { loginDataSource.patchUserName(userNameDto) }
    }

    @Test
    fun `로그인 실패 시 예외가 발생하는지 테스트`() = runTest {
        // Given
        val loginDto = LoginDto(
            email = "invalid@example.com",
            name = "Test User",
            fcmToken = "test_fcm_token"
        )

        coEvery { loginDataSource.postLoginInfo(loginDto) } throws RuntimeException("Login failed")

        // When & Then
        try {
            loginRepository.postLoginInfo(loginDto)
            fail("Expected exception was not thrown")
        } catch (e: RuntimeException) {
            assertEquals("Login failed", e.message)
        }

        coVerify { loginDataSource.postLoginInfo(loginDto) }
    }
}