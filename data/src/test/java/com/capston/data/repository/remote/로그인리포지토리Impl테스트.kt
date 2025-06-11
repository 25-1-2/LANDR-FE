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

class 로그인리포지토리Impl테스트 {

    private val 로그인데이터소스: LoginDataSource = mockk()
    private lateinit var 로그인리포지토리: LoginRepositoryImpl

    @Before
    fun setUp() {
        로그인리포지토리 = LoginRepositoryImpl(로그인데이터소스)
    }

    @Test
    fun `로그인 성공 시 토큰이 반환되는지 테스트`() = runTest {
        // Given
        val loginDto = LoginDto(
            email = "test@example.com",
            name = "Test User",
            fcmToken = "test_fcm_token"
        )
        val 예상결과 = LoginResponse(token = "test_access_token")

        coEvery { 로그인데이터소스.postLoginInfo(loginDto) } returns 예상결과

        // When
        val result = 로그인리포지토리.postLoginInfo(loginDto)

        // Then
        assertEquals(예상결과.token, result.token)
        coVerify { 로그인데이터소스.postLoginInfo(loginDto) }
    }

    @Test
    fun `사용자 프로필 조회 테스트`() = runTest {
        // Given
        val expectedProfile = UserProfileResponse(
            id = 1,
            email = "test@example.com",
            name = "Test User"
        )

        coEvery { 로그인데이터소스.getUserProfile() } returns expectedProfile

        // When
        val result = 로그인리포지토리.getUserProfile()

        // Then
        assertNotNull(result)
        assertEquals(expectedProfile.email, result.email)
        assertEquals(expectedProfile.name, result.name)
        coVerify { 로그인데이터소스.getUserProfile() }
    }

    @Test
    fun `사용자 이름 수정 테스트`() = runTest {
        // Given
        val 유저이름 = UserNameDto(name = "Updated Name")
        val 예상결과 = UserProfileResponse(
            id = 1,
            email = "test@example.com",
            name = "Updated Name"
        )

        coEvery { 로그인데이터소스.patchUserName(유저이름) } returns 예상결과

        // When
        val result = 로그인리포지토리.patchUserName(유저이름)

        // Then
        assertEquals("Updated Name", result.name)
        coVerify { 로그인데이터소스.patchUserName(유저이름) }
    }

    @Test
    fun `로그인 실패 시 예외가 발생하는지 테스트`() = runTest {
        // Given
        val 로그인정보 = LoginDto(
            email = "invalid@example.com",
            name = "Test User",
            fcmToken = "test_fcm_token"
        )

        coEvery { 로그인데이터소스.postLoginInfo(로그인정보) } throws RuntimeException("로그인 실패")

        // When & Then
        try {
            로그인리포지토리.postLoginInfo(로그인정보)
            fail("예상결과가 아닙니다")
        } catch (e: RuntimeException) {
            assertEquals("로그인 실패", e.message)
        }

        coVerify { 로그인데이터소스.postLoginInfo(로그인정보) }
    }
}