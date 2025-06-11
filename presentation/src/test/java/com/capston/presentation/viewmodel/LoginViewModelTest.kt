package com.capston.presentation.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.capston.domain.manager.LoadingStateManager
import com.capston.domain.request.LoginDto
import com.capston.domain.request.UserNameDto
import com.capston.domain.response.user.LoginResponse
import com.capston.domain.response.user.UserProfileResponse
import com.capston.domain.usecase.login.GetUserProfileUseCase
import com.capston.domain.usecase.login.PatchUserNameUseCase
import com.capston.domain.usecase.login.PostLoginInfoUseCase
import com.capston.domain.usecase.token.ClearTokensUseCase
import com.capston.domain.usecase.token.GetAccessTokenUseCase
import com.capston.domain.usecase.token.SaveAccessTokenUseCase
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    private val postLoginInfoUseCase: PostLoginInfoUseCase = mockk()
    private val getUserProfileUseCase: GetUserProfileUseCase = mockk()
    private val patchUserNameUseCase: PatchUserNameUseCase = mockk()
    private val saveAccessTokenUseCase: SaveAccessTokenUseCase = mockk()
    private val getAccessTokenUseCase: GetAccessTokenUseCase = mockk()
    private val clearTokensUseCase: ClearTokensUseCase = mockk()
    private val loadingStateManager: LoadingStateManager = mockk(relaxed = true)

    private lateinit var viewModel: LoginViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        every { loadingStateManager.show() } returns Unit
        every { loadingStateManager.hide() } returns Unit

        viewModel = LoginViewModel(
            postLoginInfoUseCase,
            getUserProfileUseCase,
            patchUserNameUseCase,
            saveAccessTokenUseCase,
            getAccessTokenUseCase,
            clearTokensUseCase,
            loadingStateManager
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `postLogin should save token and update login state`() = runTest {
        // Given
        val loginDto = LoginDto(email = "test@example.com", name = "Test User", fcmToken = "fcm_token")
        val loginResponse = LoginResponse(token = "access_token_123")

        coEvery { postLoginInfoUseCase(loginDto) } returns flowOf(loginResponse)
        coEvery { saveAccessTokenUseCase(loginResponse.token) } returns Unit
        coEvery { getAccessTokenUseCase() } returns flowOf(loginResponse.token)

        // When
        viewModel.postLogin(loginDto)
        advanceUntilIdle()

        // Then
        assertThat(viewModel.loginResponse.value.token).isEqualTo("access_token_123")
        assertThat(viewModel.loginSuccess.value).isTrue()
        assertThat(viewModel.isTokenSaved.value).isTrue()

        verify { loadingStateManager.show() }
        verify { loadingStateManager.hide() }
        coVerify(exactly = 1) { postLoginInfoUseCase(loginDto) }
        coVerify(exactly = 1) { saveAccessTokenUseCase(loginResponse.token) }
    }

    @Test
    fun `getUserProfile should update user profile state`() = runTest {
        // Given
        val expectedProfile = UserProfileResponse(
            id = 1,
            email = "test@example.com",
            name = "Test User"
        )
        coEvery { getUserProfileUseCase() } returns flowOf(expectedProfile)

        // When
        viewModel.getUserProfile()
        advanceUntilIdle()

        // Then
        assertThat(viewModel.getUserProfile.value).isEqualTo(expectedProfile)
        assertThat(viewModel.getUserProfile.value.name).isEqualTo("Test User")

        verify { loadingStateManager.show() }
        verify { loadingStateManager.hide() }
        coVerify(exactly = 1) { getUserProfileUseCase() }
    }

    @Test
    fun `patchUserName should update user profile with new name`() = runTest {
        // Given
        val userNameDto = UserNameDto(name = "Updated Name")
        val updatedProfile = UserProfileResponse(
            id = 1,
            email = "test@example.com",
            name = "Updated Name"
        )
        coEvery { patchUserNameUseCase(userNameDto) } returns flowOf(updatedProfile)

        // When
        viewModel.patchUserName(userNameDto)
        advanceUntilIdle()

        // Then
        assertThat(viewModel.getUserProfile.value.name).isEqualTo("Updated Name")

        verify { loadingStateManager.show() }
        verify { loadingStateManager.hide() }
        coVerify(exactly = 1) { patchUserNameUseCase(userNameDto) }
    }

    @Test
    fun `checkAccessToken should retrieve stored token`() = runTest {
        // Given
        val storedToken = "stored_token_123"
        coEvery { getAccessTokenUseCase() } returns flowOf(storedToken)

        // When
        viewModel.checkAccessToken()
        advanceUntilIdle()

        // Then
        verify { loadingStateManager.show() }
        verify { loadingStateManager.hide() }
        coVerify(exactly = 1) { getAccessTokenUseCase() }
    }
}