package com.capston.data.repository.remote.datasourcelmpl

import android.util.Log
import com.capston.data.repository.remote.api.MyPageApi
import com.capston.domain.datasource.MyPageDataSource
import com.capston.domain.response.mypage.GetDistinctMyPageResponse
import com.capston.domain.response.mypage.GetMyPageStatisticsResponse
import javax.inject.Inject

class MyPageDataSourceImpl @Inject constructor(
    private val myPageApi: MyPageApi
): MyPageDataSource {
    override suspend fun getDistinctMyPage(): GetDistinctMyPageResponse {
        val response = myPageApi.getDistinctMyPage()
        Log.d("MyPageDataSourceImpl", response.toString())
        return response
    }

    override suspend fun getMonthlyStatistics(date: String): GetMyPageStatisticsResponse {
        val response = myPageApi.getMonthlyStatistics(date)
        Log.d("MyPageDataSourceImpl", response.toString())
        return response
    }
}