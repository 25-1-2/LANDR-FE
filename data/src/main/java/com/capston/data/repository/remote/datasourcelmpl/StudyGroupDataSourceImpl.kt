package com.capston.data.repository.remote.datasourcelmpl

import com.capston.data.repository.remote.api.StudyGroupApi
import com.capston.domain.datasource.StudyGroupDataSource
import com.capston.domain.response.study_group.NewStudyGroupResponse
import javax.inject.Inject

class StudyGroupDataSourceImpl @Inject constructor(
    private val studyGroupApi: StudyGroupApi
) : StudyGroupDataSource {
    override suspend fun postNewStudyGroup(planId: Int): NewStudyGroupResponse {
        return studyGroupApi.postNewStudyGroup(planId)
    }
}