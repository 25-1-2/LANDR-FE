package com.capston.data.repository.remote.repositoryImpl

import com.capston.domain.datasource.MyPageDataSource
import com.capston.domain.repository.HomeRepository
import com.capston.domain.repository.MyPageRepository
import com.capston.domain.response.CheckResponse
import com.capston.domain.response.home.DistinctHomeIdResponse
import com.capston.domain.response.mypage.GetDistinctMyPageResponse
import com.capston.domain.response.mypage.GetMyPageStatisticsResponse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MyPageRepositoryImpl @Inject constructor(
    private val myPageDataSource: MyPageDataSource
) : MyPageRepository {

    override suspend fun getDistinctMyPage(): GetDistinctMyPageResponse {
        return myPageDataSource.getDistinctMyPage()
    }

    override suspend fun getMonthlyStatistics(date: String): GetMyPageStatisticsResponse {
        return myPageDataSource.getMonthlyStatistics(date)
    }
}