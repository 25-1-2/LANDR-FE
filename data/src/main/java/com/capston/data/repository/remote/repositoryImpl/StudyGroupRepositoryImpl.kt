package com.capston.data.repository.remote.repositoryImpl

import com.capston.domain.datasource.StudyGroupDataSource
import com.capston.domain.repository.StudyGroupRepository
import com.capston.domain.request.JoinStudyGroupDto
import com.capston.domain.response.MessageResponse
import com.capston.domain.response.study_group.NewStudyGroupResponse
import com.capston.domain.response.study_group.OneStudyGroupResponse
import javax.inject.Inject

class StudyGroupRepositoryImpl @Inject constructor(
    private val studyGroupDataSource: StudyGroupDataSource
) : StudyGroupRepository {
    override suspend fun postNewStudyGroup(
        planId: Int
    ): NewStudyGroupResponse = studyGroupDataSource.postNewStudyGroup(planId)

    override suspend fun getOneStudyGroup(
        studyGroupId: Int
    ): OneStudyGroupResponse = studyGroupDataSource.getOneStudyGroup(studyGroupId)

    override suspend fun postJoinStudyGroup(
        joinStudyGroupDto: JoinStudyGroupDto
    ): MessageResponse = studyGroupDataSource.postJoinStudyGroup(joinStudyGroupDto)

    override suspend fun deleteOneStudyGroup(
        studyGroupId: Int
    ): MessageResponse = studyGroupDataSource.deleteOneStudyGroup(studyGroupId)
}