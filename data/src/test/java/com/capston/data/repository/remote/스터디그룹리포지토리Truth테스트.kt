package com.capston.data.repository.remote

import com.capston.data.repository.remote.repositoryImpl.StudyGroupRepositoryImpl
import com.capston.domain.datasource.StudyGroupDataSource
import com.capston.domain.request.JoinStudyGroupDto
import com.capston.domain.response.MessageResponse
import com.capston.domain.response.study_group.NewStudyGroupResponse
import com.capston.domain.response.study_group.OneStudyGroupResponse
import com.capston.domain.response.study_group.StudyGroupMember
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class 스터디그룹리포지토리Truth테스트 {

    private val 스터디그룹데이터소스: StudyGroupDataSource = mockk()
    private lateinit var 스터디그룹리포지토리: StudyGroupRepositoryImpl

    @Before
    fun 설정() {
        스터디그룹리포지토리 = StudyGroupRepositoryImpl(스터디그룹데이터소스)
    }

    @Test
    fun `Truth 라이브러리 사용 새 스터디그룹 생성 테스트`() = runTest {
        // Given - 주어진 조건
        val 계획아이디 = 100
        val 예상응답 = NewStudyGroupResponse(
            studyGroupId = 50,
            inviteCode = "STUDY2024",
            name = "수학 정복 스터디"
        )

        coEvery { 스터디그룹데이터소스.postNewStudyGroup(계획아이디) } returns 예상응답

        // When - 실행
        val 결과 = 스터디그룹리포지토리.postNewStudyGroup(계획아이디)

        // Then - Truth 사용 검증
        assertThat(결과.studyGroupId).isEqualTo(50)
        assertThat(결과.studyGroupId).isAtLeast(1)
        assertThat(결과.inviteCode).isEqualTo("STUDY2024")
        assertThat(결과.inviteCode).isNotEmpty()
        assertThat(결과.inviteCode).hasLength(9)
        assertThat(결과.inviteCode).startsWith("STUDY")
        assertThat(결과.name).isEqualTo("수학 정복 스터디")
        assertThat(결과.name).contains("수학")
        assertThat(결과.name).contains("스터디")
        assertThat(결과.name).endsWith("스터디")

        coVerify { 스터디그룹데이터소스.postNewStudyGroup(계획아이디) }
    }

    @Test
    fun `Truth 라이브러리 사용 스터디그룹 단건 조회 테스트`() = runTest {
        // Given - 주어진 조건
        val 스터디그룹아이디 = 25
        val 멤버목록 = listOf(
            StudyGroupMember(
                userId = 1,
                userName = "스터디리더",
                planId = 100
            ),
            StudyGroupMember(
                userId = 2,
                userName = "열정멤버A",
                planId = 101
            ),
            StudyGroupMember(
                userId = 3,
                userName = "열정멤버B",
                planId = 102
            ),
            StudyGroupMember(
                userId = 4,
                userName = "신입멤버",
                planId = 103
            )
        )

        val 예상응답 = OneStudyGroupResponse(
            studyGroupId = 스터디그룹아이디,
            name = "영어 독해 마스터 그룹",
            inviteCode = "ENG2024A",
            leaderId = 1,
            leaderName = "스터디리더",
            lectureName = "영어 독해 완전 정복",
            members = 멤버목록
        )

        coEvery { 스터디그룹데이터소스.getOneStudyGroup(스터디그룹아이디) } returns 예상응답

        // When - 실행
        val 결과 = 스터디그룹리포지토리.getOneStudyGroup(스터디그룹아이디)

        // Then - Truth 사용 검증
        assertThat(결과.studyGroupId).isEqualTo(스터디그룹아이디)
        assertThat(결과.name).isEqualTo("영어 독해 마스터 그룹")
        assertThat(결과.name).contains("영어")
        assertThat(결과.name).contains("독해")
        assertThat(결과.name).contains("마스터")
        assertThat(결과.inviteCode).isEqualTo("ENG2024A")
        assertThat(결과.inviteCode).startsWith("ENG")
        assertThat(결과.inviteCode).endsWith("A")
        assertThat(결과.leaderId).isEqualTo(1)
        assertThat(결과.leaderName).isEqualTo("스터디리더")
        assertThat(결과.leaderName).contains("리더")
        assertThat(결과.lectureName).isEqualTo("영어 독해 완전 정복")
        assertThat(결과.lectureName).contains("영어")
        assertThat(결과.lectureName).contains("독해")
        assertThat(결과.members).hasSize(4)
        assertThat(결과.members).isNotEmpty()

        // 리더 검증
        val 리더 = 결과.members.find { it.userId == 결과.leaderId }!!
        assertThat(리더.userName).isEqualTo("스터디리더")
        assertThat(리더.planId).isEqualTo(100)

        // 열정멤버들 검증
        val 열정멤버들 = 결과.members.filter { it.userName.contains("열정") }
        assertThat(열정멤버들).hasSize(2)
        assertThat(열정멤버들.map { it.userName }).containsExactly("열정멤버A", "열정멤버B")

        // 신입멤버 검증
        val 신입멤버 = 결과.members.find { it.userName.contains("신입") }!!
        assertThat(신입멤버.userName).isEqualTo("신입멤버")
        assertThat(신입멤버.planId).isEqualTo(103)
        assertThat(신입멤버.userId).isEqualTo(4)

        // 멤버 ID 고유성 검증
        val 멤버아이디목록 = 결과.members.map { it.userId }
        assertThat(멤버아이디목록.distinct()).hasSize(멤버아이디목록.size)

        // 계획 ID 고유성 검증
        val 계획아이디목록 = 결과.members.map { it.planId }
        assertThat(계획아이디목록.distinct()).hasSize(계획아이디목록.size)

        coVerify { 스터디그룹데이터소스.getOneStudyGroup(스터디그룹아이디) }
    }

    @Test
    fun `Truth 라이브러리 사용 스터디그룹 가입 테스트`() = runTest {
        // Given - 주어진 조건
        val 가입요청 = JoinStudyGroupDto(inviteCode = "MATH2024")
        val 예상응답 = MessageResponse(message = "스터디그룹에 성공적으로 가입되었습니다.")

        coEvery { 스터디그룹데이터소스.postJoinStudyGroup(가입요청) } returns 예상응답

        // When - 실행
        val 결과 = 스터디그룹리포지토리.postJoinStudyGroup(가입요청)

        // Then - Truth 사용 검증
        assertThat(결과.message).isEqualTo("스터디그룹에 성공적으로 가입되었습니다.")
        assertThat(결과.message).contains("스터디그룹")
        assertThat(결과.message).contains("성공적으로")
        assertThat(결과.message).contains("가입")
        assertThat(결과.message).isNotEmpty()
        assertThat(결과.message).endsWith("습니다.")

        coVerify { 스터디그룹데이터소스.postJoinStudyGroup(가입요청) }
    }

    @Test
    fun `Truth 라이브러리 사용 스터디그룹 삭제 테스트`() = runTest {
        // Given - 주어진 조건
        val 스터디그룹아이디 = 77
        val 예상응답 = MessageResponse(message = "스터디그룹이 삭제되었습니다.")

        coEvery { 스터디그룹데이터소스.deleteOneStudyGroup(스터디그룹아이디) } returns 예상응답

        // When - 실행
        val 결과 = 스터디그룹리포지토리.deleteOneStudyGroup(스터디그룹아이디)

        // Then - Truth 사용 검증
        assertThat(결과.message).isEqualTo("스터디그룹이 삭제되었습니다.")
        assertThat(결과.message).contains("스터디그룹")
        assertThat(결과.message).contains("삭제")
        assertThat(결과.message).isNotEmpty()

        coVerify { 스터디그룹데이터소스.deleteOneStudyGroup(스터디그룹아이디) }
    }

    @Test
    fun `Truth 라이브러리 사용 대규모 스터디그룹 조회 테스트`() = runTest {
        // Given - 주어진 조건
        val 대규모그룹아이디 = 999
        val 대규모멤버목록 = (1..20).map { index ->
            StudyGroupMember(
                userId = index,
                userName = "멤버${index}",
                planId = 200 + index
            )
        }

        val 대규모그룹응답 = OneStudyGroupResponse(
            studyGroupId = 대규모그룹아이디,
            name = "메가 수학 정복단",
            inviteCode = "MEGA2024",
            leaderId = 1,
            leaderName = "멤버1",
            lectureName = "수학 완전 정복 - 심화과정",
            members = 대규모멤버목록
        )

        coEvery { 스터디그룹데이터소스.getOneStudyGroup(대규모그룹아이디) } returns 대규모그룹응답

        // When - 실행
        val 결과 = 스터디그룹리포지토리.getOneStudyGroup(대규모그룹아이디)

        // Then - Truth 사용 검증
        assertThat(결과.members).hasSize(20)
        assertThat(결과.name).contains("메가")
        assertThat(결과.name).contains("정복단")
        assertThat(결과.inviteCode).isEqualTo("MEGA2024")
        assertThat(결과.lectureName).contains("심화과정")

        // 모든 멤버가 올바르게 생성되었는지 확인
        결과.members.forEachIndexed { index, 멤버 ->
            val 예상번호 = index + 1
            assertThat(멤버.userId).isEqualTo(예상번호)
            assertThat(멤버.userName).isEqualTo("멤버${예상번호}")
            assertThat(멤버.planId).isEqualTo(200 + 예상번호)
        }

        // 리더가 첫 번째 멤버인지 확인
        val 리더 = 결과.members.find { it.userId == 결과.leaderId }!!
        assertThat(리더.userName).isEqualTo("멤버1")
        assertThat(리더.planId).isEqualTo(201)

        // 마지막 멤버 확인
        val 마지막멤버 = 결과.members.last()
        assertThat(마지막멤버.userId).isEqualTo(20)
        assertThat(마지막멤버.userName).isEqualTo("멤버20")
        assertThat(마지막멤버.planId).isEqualTo(220)

        coVerify { 스터디그룹데이터소스.getOneStudyGroup(대규모그룹아이디) }
    }

    @Test
    fun `Truth 라이브러리 사용 1인 스터디그룹 조회 테스트`() = runTest {
        // Given - 주어진 조건 (혼자만 있는 스터디그룹)
        val 개인그룹아이디 = 1
        val 개인멤버목록 = listOf(
            StudyGroupMember(
                userId = 100,
                userName = "혼자공부하는사람",
                planId = 500
            )
        )

        val 개인그룹응답 = OneStudyGroupResponse(
            studyGroupId = 개인그룹아이디,
            name = "나홀로 영어 정복",
            inviteCode = "SOLO2024",
            leaderId = 100,
            leaderName = "혼자공부하는사람",
            lectureName = "기초 영어 회화",
            members = 개인멤버목록
        )

        coEvery { 스터디그룹데이터소스.getOneStudyGroup(개인그룹아이디) } returns 개인그룹응답

        // When - 실행
        val 결과 = 스터디그룹리포지토리.getOneStudyGroup(개인그룹아이디)

        // Then - Truth 사용 검증
        assertThat(결과.members).hasSize(1)
        assertThat(결과.name).contains("나홀로")
        assertThat(결과.inviteCode).isEqualTo("SOLO2024")
        assertThat(결과.leaderId).isEqualTo(100)
        assertThat(결과.leaderName).isEqualTo("혼자공부하는사람")
        assertThat(결과.leaderName).contains("혼자")

        // 유일한 멤버가 리더인지 확인
        val 유일한멤버 = 결과.members.first()
        assertThat(유일한멤버.userId).isEqualTo(결과.leaderId)
        assertThat(유일한멤버.userName).isEqualTo(결과.leaderName)
        assertThat(유일한멤버.planId).isEqualTo(500)

        coVerify { 스터디그룹데이터소스.getOneStudyGroup(개인그룹아이디) }
    }

    @Test
    fun `Truth 라이브러리 사용 잘못된 초대코드로 가입 실패 테스트`() = runTest {
        // Given - 주어진 조건
        val 잘못된가입요청 = JoinStudyGroupDto(inviteCode = "INVALID")
        val 예외 = RuntimeException("존재하지 않는 초대코드입니다.")

        coEvery { 스터디그룹데이터소스.postJoinStudyGroup(잘못된가입요청) } throws 예외

        // When & Then - Truth 사용
        try {
            스터디그룹리포지토리.postJoinStudyGroup(잘못된가입요청)
            assertThat(true).isFalse() // 예외가 발생해야 하므로 여기 도달하면 안됨
        } catch (e: RuntimeException) {
            assertThat(e.message).isEqualTo("존재하지 않는 초대코드입니다.")
            assertThat(e.message).contains("존재하지 않는")
            assertThat(e.message).contains("초대코드")
            assertThat(e).isInstanceOf(RuntimeException::class.java)
        }

        coVerify { 스터디그룹데이터소스.postJoinStudyGroup(잘못된가입요청) }
    }

    @Test
    fun `Truth 라이브러리 사용 존재하지 않는 그룹 삭제 실패 테스트`() = runTest {
        // Given - 주어진 조건
        val 존재하지않는그룹아이디 = -999
        val 예외 = RuntimeException("존재하지 않는 스터디그룹입니다.")

        coEvery { 스터디그룹데이터소스.deleteOneStudyGroup(존재하지않는그룹아이디) } throws 예외

        // When & Then - Truth 사용
        try {
            스터디그룹리포지토리.deleteOneStudyGroup(존재하지않는그룹아이디)
            assertThat(true).isFalse() // 예외가 발생해야 하므로 여기 도달하면 안됨
        } catch (e: RuntimeException) {
            assertThat(e.message).isEqualTo("존재하지 않는 스터디그룹입니다.")
            assertThat(e.message).contains("존재하지 않는")
            assertThat(e.message).contains("스터디그룹")
            assertThat(e).isInstanceOf(RuntimeException::class.java)
        }

        coVerify { 스터디그룹데이터소스.deleteOneStudyGroup(존재하지않는그룹아이디) }
    }

    @Test
    fun `Truth 라이브러리 사용 다양한 과목 스터디그룹들 테스트`() = runTest {
        // Given - 주어진 조건들 (여러 그룹을 순차적으로 테스트)
        val 과목별그룹정보 = listOf(
            Triple(1, "수학 마스터즈", "MATH2024"),
            Triple(2, "영어 정복단", "ENG2024"),
            Triple(3, "과학 탐구반", "SCI2024"),
            Triple(4, "국어 문학 동아리", "KOR2024")
        )

        과목별그룹정보.forEach { (그룹아이디, 그룹명, 초대코드) ->
            val 그룹응답 = OneStudyGroupResponse(
                studyGroupId = 그룹아이디,
                name = 그룹명,
                inviteCode = 초대코드,
                leaderId = 1,
                leaderName = "리더${그룹아이디}",
                lectureName = "${그룹명} 강의",
                members = listOf(
                    StudyGroupMember(1, "리더${그룹아이디}", 100 + 그룹아이디),
                    StudyGroupMember(2, "멤버${그룹아이디}A", 200 + 그룹아이디),
                    StudyGroupMember(3, "멤버${그룹아이디}B", 300 + 그룹아이디)
                )
            )

            coEvery { 스터디그룹데이터소스.getOneStudyGroup(그룹아이디) } returns 그룹응답

            // When - 실행
            val 결과 = 스터디그룹리포지토리.getOneStudyGroup(그룹아이디)

            // Then - Truth 사용 검증
            assertThat(결과.studyGroupId).isEqualTo(그룹아이디)
            assertThat(결과.name).isEqualTo(그룹명)
            assertThat(결과.inviteCode).isEqualTo(초대코드)
            assertThat(결과.members).hasSize(3)

            // 과목별 특성 검증
            when (그룹아이디) {
                1 -> {
                    assertThat(결과.name).contains("수학")
                    assertThat(결과.inviteCode).startsWith("MATH")
                }
                2 -> {
                    assertThat(결과.name).contains("영어")
                    assertThat(결과.inviteCode).startsWith("ENG")
                }
                3 -> {
                    assertThat(결과.name).contains("과학")
                    assertThat(결과.inviteCode).startsWith("SCI")
                }
                4 -> {
                    assertThat(결과.name).contains("국어")
                    assertThat(결과.inviteCode).startsWith("KOR")
                    assertThat(결과.name).contains("문학")
                }
            }

            coVerify { 스터디그룹데이터소스.getOneStudyGroup(그룹아이디) }
        }
    }

    @Test
    fun `Truth 라이브러리 사용 빈 초대코드로 가입 실패 테스트`() = runTest {
        // Given - 주어진 조건
        val 빈초대코드요청 = JoinStudyGroupDto(inviteCode = "")
        val 예외 = RuntimeException("초대코드를 입력해주세요.")

        coEvery { 스터디그룹데이터소스.postJoinStudyGroup(빈초대코드요청) } throws 예외

        // When & Then - Truth 사용
        try {
            스터디그룹리포지토리.postJoinStudyGroup(빈초대코드요청)
            assertThat(true).isFalse() // 예외가 발생해야 하므로 여기 도달하면 안됨
        } catch (e: RuntimeException) {
            assertThat(e.message).isEqualTo("초대코드를 입력해주세요.")
            assertThat(e.message).contains("초대코드")
            assertThat(e.message).contains("입력")
            assertThat(e).isInstanceOf(RuntimeException::class.java)
        }

        coVerify { 스터디그룹데이터소스.postJoinStudyGroup(빈초대코드요청) }
    }
}