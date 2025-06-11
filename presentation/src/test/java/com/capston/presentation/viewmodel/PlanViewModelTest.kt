package com.capston.presentation.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.capston.domain.manager.LoadingStateManager
import com.capston.domain.model.LectureItemDto
import com.capston.domain.model.Lesson
import com.capston.domain.model.NewPlanLesson
import com.capston.domain.request.LectureDto
import com.capston.domain.response.enum_class.Platform
import com.capston.domain.response.enum_class.Subject
import com.capston.domain.response.lecture.DistinctLectureResponse
import com.capston.domain.response.lecture.GetLessonsByLectureIdResponse
import com.capston.domain.response.lecture.LectureResponseDto
import com.capston.domain.usecase.lecture.GetAllLectureUseCase
import com.capston.domain.usecase.lecture.GetDistinctLectureUseCase
import com.capston.domain.usecase.lecture.GetLessonsByLectureIdUseCase
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
class PlanViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    private val getDistinctLectureUseCase: GetDistinctLectureUseCase = mockk()
    private val getAllLectureUseCase: GetAllLectureUseCase = mockk()
    private val getLessonsByLectureIdUseCase: GetLessonsByLectureIdUseCase = mockk()
    private val loadingStateManager: LoadingStateManager = mockk(relaxed = true)

    private lateinit var viewModel: LectureViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        every { loadingStateManager.show() } returns Unit
        every { loadingStateManager.hide() } returns Unit

        viewModel = LectureViewModel(
            getDistinctLectureUseCase,
            getAllLectureUseCase,
            getLessonsByLectureIdUseCase,
            loadingStateManager
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `getDistinctLecture should return search results`() = runTest {
        // Given
        val lectureDto = LectureDto(
            search = "수학",
            platform = Platform.MEGA,
            subject = Subject.MATH
        )
        val lectures = listOf(
            LectureResponseDto(
                id = 1,
                title = "수학 기초",
                teacher = "김선생",
                platform = Platform.MEGA,
                subject = Subject.MATH,
                totalLessons = 30
            )
        )
        val expectedResponse = DistinctLectureResponse(data = lectures)

        coEvery { getDistinctLectureUseCase(lectureDto) } returns flowOf(expectedResponse)

        // When
        viewModel.getDistinctLecture(lectureDto)
        advanceUntilIdle()

        // Then
        assertThat(viewModel.distinctLecture.value).isEqualTo(expectedResponse)
        assertThat(viewModel.distinctLecture.value.data).hasSize(1)
        assertThat(viewModel.distinctLecture.value.data?.first()?.title).isEqualTo("수학 기초")

        coVerify(exactly = 1) { getDistinctLectureUseCase(lectureDto) }
    }

    @Test
    fun `getAllLecture should return all lectures`() = runTest {
        // Given
        val lectureDto = LectureDto(offset = "10")
        val lectures = listOf(
            LectureResponseDto(
                id = 1,
                title = "국어 문법",
                teacher = "이선생",
                platform = Platform.ETOOS,
                subject = Subject.KOR,
                totalLessons = 25
            ),
            LectureResponseDto(
                id = 2,
                title = "영어 독해",
                teacher = "박선생",
                platform = Platform.DAESANG,
                subject = Subject.ENG,
                totalLessons = 40
            )
        )
        val expectedResponse = DistinctLectureResponse(data = lectures)

        coEvery { getAllLectureUseCase(lectureDto) } returns flowOf(expectedResponse)

        // When
        viewModel.getAllLecture(lectureDto)
        advanceUntilIdle()

        // Then
        assertThat(viewModel.allLectureList.value).hasSize(2)
        assertThat(viewModel.allLectureList.value.first().title).isEqualTo("국어 문법")

        coVerify(exactly = 1) { getAllLectureUseCase(lectureDto) }
    }

    @Test
    fun `selectLecture should update selected lecture state`() = runTest {
        // Given
        val lecture = LectureItemDto(
            id = 1,
            title = "물리학 기초",
            teacher = "최선생",
            platform = Platform.EBSI,
            subject = Subject.SCI,
            totalLessons = 20,
            tag = "개념완성",
            createdAt = "2024-01-01"
        )

        // When
        viewModel.selectLecture(lecture)
        advanceUntilIdle()

        // Then
        assertThat(viewModel.selectedLecture.value).isEqualTo(lecture)
        assertThat(viewModel.selectedLecture.value?.title).isEqualTo("물리학 기초")
    }

    @Test
    fun `getLessonsByLectureId should return lessons list`() = runTest {
        // Given
        val lectureId = 1
        val lessons = listOf(
            NewPlanLesson(id = 1, title = "1강. 기초 개념"),
            NewPlanLesson(id = 2, title = "2강. 응용 문제")
        )
        val expectedResponse = GetLessonsByLectureIdResponse(lessons = lessons)

        coEvery { getLessonsByLectureIdUseCase(lectureId) } returns flowOf(expectedResponse)

        // When
        viewModel.getLessonsByLectureId(lectureId)
        advanceUntilIdle()

        // Then
        assertThat(viewModel.lessonsByLectureId.value).hasSize(2)
        assertThat(viewModel.lessonsByLectureId.value.first().title).isEqualTo("1강. 기초 개념")

        verify { loadingStateManager.show() }
        verify { loadingStateManager.hide() }
        coVerify(exactly = 1) { getLessonsByLectureIdUseCase(lectureId) }
    }

    @Test
    fun `getLessonsByLectureId with error should set empty list`() = runTest {
        // Given
        val lectureId = 1
        coEvery { getLessonsByLectureIdUseCase(lectureId) } throws RuntimeException("Network error")

        // When
        viewModel.getLessonsByLectureId(lectureId)
        advanceUntilIdle()

        // Then
        assertThat(viewModel.lessonsByLectureId.value).isEmpty()

        verify { loadingStateManager.show() }
        verify { loadingStateManager.hide() }
    }

    @Test
    fun `updateSearchLectureItems should update search items state`() = runTest {
        // Given
        val items = listOf(
            LectureItemDto(
                id = 1,
                title = "화학 기초",
                teacher = "정선생",
                platform = Platform.MEGA,
                subject = Subject.SCI,
                totalLessons = 35,
                tag = "기초완성",
                createdAt = "2024-01-01"
            )
        )

        // When
        viewModel.updateSearchLectureItems(items)
        advanceUntilIdle()

        // Then
        assertThat(viewModel.searchLectureItems.value).hasSize(1)
        assertThat(viewModel.searchLectureItems.value.first().title).isEqualTo("화학 기초")
    }

    @Test
    fun `getDistinctLecture with empty search should handle gracefully`() = runTest {
        // Given
        val lectureDto = LectureDto(search = "")
        val expectedResponse = DistinctLectureResponse(data = emptyList())

        coEvery { getDistinctLectureUseCase(lectureDto) } returns flowOf(expectedResponse)

        // When
        viewModel.getDistinctLecture(lectureDto)
        advanceUntilIdle()

        // Then
        assertThat(viewModel.distinctLecture.value.data).isEmpty()
        coVerify(exactly = 1) { getDistinctLectureUseCase(lectureDto) }
    }

    @Test
    fun `getAllLecture with filters should apply correctly`() = runTest {
        // Given
        val lectureDto = LectureDto(
            platform = Platform.ETOOS,
            subject = Subject.ENG,
            offset = "20"
        )
        val lectures = listOf(
            LectureResponseDto(
                id = 1,
                title = "영어 문법",
                teacher = "김영어",
                platform = Platform.ETOOS,
                subject = Subject.ENG,
                totalLessons = 30
            )
        )
        val expectedResponse = DistinctLectureResponse(data = lectures)

        coEvery { getAllLectureUseCase(lectureDto) } returns flowOf(expectedResponse)

        // When
        viewModel.getAllLecture(lectureDto)
        advanceUntilIdle()

        // Then
        assertThat(viewModel.allLectureList.value).hasSize(1)
        assertThat(viewModel.allLectureList.value.first().platform).isEqualTo(Platform.ETOOS)
        assertThat(viewModel.allLectureList.value.first().subject).isEqualTo(Subject.ENG)

        coVerify(exactly = 1) { getAllLectureUseCase(lectureDto) }
    }

    @Test
    fun `multiple search calls should update state correctly`() = runTest {
        // Given
        val firstDto = LectureDto(search = "수학")
        val secondDto = LectureDto(search = "영어")

        val firstResponse = DistinctLectureResponse(
            data = listOf(
                LectureResponseDto(id = 1, title = "수학 기초", teacher = "수학선생", platform = Platform.MEGA, subject = Subject.MATH)
            )
        )
        val secondResponse = DistinctLectureResponse(
            data = listOf(
                LectureResponseDto(id = 2, title = "영어 기초", teacher = "영어선생", platform = Platform.ETOOS, subject = Subject.ENG)
            )
        )

        coEvery { getDistinctLectureUseCase(firstDto) } returns flowOf(firstResponse)
        coEvery { getDistinctLectureUseCase(secondDto) } returns flowOf(secondResponse)

        // When
        viewModel.getDistinctLecture(firstDto)
        advanceUntilIdle()

        // Then - First search
        assertThat(viewModel.distinctLecture.value.data?.first()?.title).isEqualTo("수학 기초")

        // When - Second search
        viewModel.getDistinctLecture(secondDto)
        advanceUntilIdle()

        // Then - Second search should override first
        assertThat(viewModel.distinctLecture.value.data?.first()?.title).isEqualTo("영어 기초")

        coVerify(exactly = 1) { getDistinctLectureUseCase(firstDto) }
        coVerify(exactly = 1) { getDistinctLectureUseCase(secondDto) }
    }
}