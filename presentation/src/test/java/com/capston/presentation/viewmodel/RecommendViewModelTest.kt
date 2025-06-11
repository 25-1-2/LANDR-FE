package com.capston.presentation.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.capston.domain.manager.LoadingStateManager
import com.capston.domain.repository.RecommendationRepository
import com.capston.domain.request.RecommendDto
import com.capston.domain.response.enum_class.Platform
import com.capston.domain.response.enum_class.Subject
import com.capston.domain.response.recommend.RecommendResponse
import com.capston.domain.usecase.recommend.PostRecommendLecturesUseCase
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
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
class RecommendViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    private val postRecommendLecturesUseCase: PostRecommendLecturesUseCase = mockk()
    private val recommendationRepository: RecommendationRepository = mockk()
    private val loadingStateManager: LoadingStateManager = mockk(relaxed = true)

    private lateinit var viewModel: RecommendViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        // Android Log 모킹
        mockkStatic(android.util.Log::class)
        every { android.util.Log.d(any(), any()) } returns 0
        every { android.util.Log.e(any(), any()) } returns 0
        every { android.util.Log.e(any(), any(), any()) } returns 0
        //every { android.util.Log.w(any(), any()) } returns 0
        every { android.util.Log.i(any(), any()) } returns 0
        every { android.util.Log.v(any(), any()) } returns 0

        every { loadingStateManager.show() } returns Unit
        every { loadingStateManager.hide() } returns Unit

        // 기본적으로 빈 리스트 반환하도록 설정
        coEvery { recommendationRepository.getRecommendations() } returns flowOf(emptyList())

        viewModel = RecommendViewModel(
            postRecommendLecturesUseCase,
            recommendationRepository,
            loadingStateManager
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkStatic(android.util.Log::class)
    }

    @Test
    fun `postRecommendLectures should return single subject recommendations`() = runTest {
        // Given
        val recommendDto = RecommendDto(
            grade = "3",
            schoolRank = 2,
            mockRank = 3,
            focus = "수능 중심",
            goal = "기출 분석",
            styles = listOf("체계적 학습", "문제풀이 중심"),
            subject = Subject.MATH
        )
        val recommendations = listOf(
            RecommendResponse(
                id = 1,
                platform = Platform.MEGA,
                title = "수학 기출 완전정복",
                teacher = "김수학",
                recommendScore = 95,
                recommendReason = "기출 분석에 특화된 강의",
                difficulty = "중상",
                subject = Subject.MATH
            ),
            RecommendResponse(
                id = 2,
                platform = Platform.ETOOS,
                title = "수학 킬러 문제집",
                teacher = "이수학",
                recommendScore = 88,
                recommendReason = "고난도 문제 해결 능력 향상",
                difficulty = "상",
                subject = Subject.MATH
            )
        )

        coEvery { postRecommendLecturesUseCase(recommendDto) } returns flowOf(recommendations)
        coEvery { recommendationRepository.saveRecommendations(any()) } returns Unit

        // When
        viewModel.postRecommendLectures(recommendDto)
        advanceUntilIdle()

        // Then
        assertThat(viewModel.postRecommendLectures.value).hasSize(2)
        assertThat(viewModel.postRecommendLectures.value.first().title).isEqualTo("수학 기출 완전정복")
        assertThat(viewModel.postRecommendLectures.value.first().recommendScore).isEqualTo(95)

        verify { loadingStateManager.show() }
        verify { loadingStateManager.hide() }
        coVerify(exactly = 1) { postRecommendLecturesUseCase(recommendDto) }
        coVerify(exactly = 1) { recommendationRepository.saveRecommendations(any()) }
    }

    @Test
    fun `postMultipleRecommendLectures should handle multiple subjects`() = runTest {
        // Given
        val mathDto = RecommendDto(
            grade = "3",
            schoolRank = 2,
            mockRank = 3,
            focus = "수능 중심",
            goal = "기출 분석",
            styles = listOf("체계적 학습"),
            subject = Subject.MATH
        )
        val engDto = RecommendDto(
            grade = "3",
            schoolRank = 3,
            mockRank = 2,
            focus = "내신 중심",
            goal = "개념 정리",
            styles = listOf("반복 학습"),
            subject = Subject.ENG
        )

        val mathRecommendations = listOf(
            RecommendResponse(
                id = 1,
                title = "수학 기출 완전정복",
                teacher = "김수학",
                recommendScore = 95,
                subject = Subject.MATH,
                platform = Platform.MEGA
            )
        )
        val engRecommendations = listOf(
            RecommendResponse(
                id = 2,
                title = "영어 문법 마스터",
                teacher = "박영어",
                recommendScore = 90,
                subject = Subject.ENG,
                platform = Platform.ETOOS
            )
        )

        coEvery { postRecommendLecturesUseCase(mathDto) } returns flowOf(mathRecommendations)
        coEvery { postRecommendLecturesUseCase(engDto) } returns flowOf(engRecommendations)
        coEvery { recommendationRepository.saveRecommendations(any()) } returns Unit

        // When
        viewModel.postMultipleRecommendLectures(listOf(mathDto, engDto))
        advanceUntilIdle()

        // Then
        assertThat(viewModel.postRecommendLectures.value).hasSize(2)

        // 점수순으로 정렬되어야 함 (수학 95점 > 영어 90점)
        assertThat(viewModel.postRecommendLectures.value.first().recommendScore).isEqualTo(95)
        assertThat(viewModel.postRecommendLectures.value.first().subject).isEqualTo(Subject.MATH)
        assertThat(viewModel.postRecommendLectures.value.last().recommendScore).isEqualTo(90)
        assertThat(viewModel.postRecommendLectures.value.last().subject).isEqualTo(Subject.ENG)

        verify { loadingStateManager.show() }
        verify { loadingStateManager.hide() }
        coVerify(exactly = 1) { postRecommendLecturesUseCase(mathDto) }
        coVerify(exactly = 1) { postRecommendLecturesUseCase(engDto) }
    }

    @Test
    fun `clearRecommendations should clear all data`() = runTest {
        // Given
        coEvery { recommendationRepository.clearRecommendations() } returns Unit

        // When
        viewModel.clearRecommendations()
        advanceUntilIdle()

        // Then
        assertThat(viewModel.postRecommendLectures.value).isEmpty()
        coVerify(exactly = 1) { recommendationRepository.clearRecommendations() }
    }

    @Test
    fun `loadSavedRecommendations should load from repository`() = runTest {
        // Given
        val savedRecommendations = listOf(
            RecommendResponse(
                id = 1,
                title = "저장된 수학 강의",
                teacher = "김선생",
                recommendScore = 85,
                subject = Subject.MATH,
                platform = Platform.MEGA
            )
        )

        coEvery { recommendationRepository.getRecommendations() } returns flowOf(savedRecommendations)

        // When - 새로운 ViewModel 인스턴스 생성 (init에서 loadSavedRecommendations 호출됨)
        val newViewModel = RecommendViewModel(
            postRecommendLecturesUseCase,
            recommendationRepository,
            loadingStateManager
        )
        advanceUntilIdle()

        // Then
        assertThat(newViewModel.postRecommendLectures.value).hasSize(1)
        assertThat(newViewModel.postRecommendLectures.value.first().title).isEqualTo("저장된 수학 강의")
    }

    @Test
    fun `postMultipleRecommendLectures with error should handle gracefully`() = runTest {
        // Given
        val mathDto = RecommendDto(subject = Subject.MATH)
        val engDto = RecommendDto(subject = Subject.ENG)

        val mathRecommendations = listOf(
            RecommendResponse(id = 1, title = "수학 강의", recommendScore = 90, subject = Subject.MATH, platform = Platform.MEGA)
        )

        coEvery { postRecommendLecturesUseCase(mathDto) } returns flowOf(mathRecommendations)
        coEvery { postRecommendLecturesUseCase(engDto) } throws RuntimeException("Network error")
        coEvery { recommendationRepository.saveRecommendations(any()) } returns Unit

        // When
        viewModel.postMultipleRecommendLectures(listOf(mathDto, engDto))
        advanceUntilIdle()

        // Then
        // 수학 추천은 성공했으므로 1개의 결과가 있어야 함
        assertThat(viewModel.postRecommendLectures.value).hasSize(1)
        assertThat(viewModel.postRecommendLectures.value.first().subject).isEqualTo(Subject.MATH)

        verify { loadingStateManager.show() }
        verify { loadingStateManager.hide() }
    }

    @Test
    fun `postRecommendLectures should limit to 20 results`() = runTest {
        // Given
        val recommendDto = RecommendDto(subject = Subject.MATH)
        val manyRecommendations = (1..25).map { index ->
            RecommendResponse(
                id = index,
                title = "수학 강의 $index",
                teacher = "선생님 $index",
                recommendScore = 100 - index, // 점수는 내림차순
                subject = Subject.MATH,
                platform = Platform.MEGA
            )
        }

        coEvery { postRecommendLecturesUseCase(recommendDto) } returns flowOf(manyRecommendations)
        coEvery { recommendationRepository.saveRecommendations(any()) } returns Unit

        // When
        viewModel.postRecommendLectures(recommendDto)
        advanceUntilIdle()

        // Then
        assertThat(viewModel.postRecommendLectures.value).hasSize(20) // 최대 20개로 제한
        assertThat(viewModel.postRecommendLectures.value.first().recommendScore).isEqualTo(99) // 가장 높은 점수
        assertThat(viewModel.postRecommendLectures.value.last().recommendScore).isEqualTo(80) // 20번째 점수
    }

    @Test
    fun `refreshSavedRecommendations should reload from repository`() = runTest {
        // Given
        val newRecommendations = listOf(
            RecommendResponse(
                id = 999,
                title = "새로 고침된 강의",
                teacher = "새선생",
                recommendScore = 95,
                subject = Subject.KOR,
                platform = Platform.EBSI
            )
        )

        coEvery { recommendationRepository.getRecommendations() } returns flowOf(newRecommendations)

        // When
        viewModel.refreshSavedRecommendations()
        advanceUntilIdle()

        // Then
        assertThat(viewModel.postRecommendLectures.value).hasSize(1)
        assertThat(viewModel.postRecommendLectures.value.first().title).isEqualTo("새로 고침된 강의")
    }

    @Test
    fun `postMultipleRecommendLectures should handle empty input`() = runTest {
        // Given
        val emptyList = emptyList<RecommendDto>()

        // When
        viewModel.postMultipleRecommendLectures(emptyList)
        advanceUntilIdle()

        // Then
        assertThat(viewModel.postRecommendLectures.value).isEmpty()

        verify { loadingStateManager.show() }
        verify { loadingStateManager.hide() }
    }

    @Test
    fun `saveRecommendationsToStorage error should not crash`() = runTest {
        // Given
        val recommendDto = RecommendDto(subject = Subject.MATH)
        val recommendations = listOf(
            RecommendResponse(id = 1, title = "수학", recommendScore = 90, subject = Subject.MATH, platform = Platform.MEGA)
        )

        coEvery { postRecommendLecturesUseCase(recommendDto) } returns flowOf(recommendations)
        coEvery { recommendationRepository.saveRecommendations(any()) } throws RuntimeException("Save failed")

        // When
        viewModel.postRecommendLectures(recommendDto)
        advanceUntilIdle()

        // Then
        // 저장 실패해도 상태는 업데이트되어야 함
        assertThat(viewModel.postRecommendLectures.value).hasSize(1)
        assertThat(viewModel.postRecommendLectures.value.first().title).isEqualTo("수학")
    }
}