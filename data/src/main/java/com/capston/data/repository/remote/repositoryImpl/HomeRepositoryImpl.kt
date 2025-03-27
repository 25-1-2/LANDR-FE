package com.capston.data.repository.remote.repositoryImpl

import com.capston.domain.datasource.HomeDataSource
import com.capston.domain.repository.HomeRepository
import com.capston.domain.response.DistinctHomeIdResponse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

//class HomeRepositoryImpl @Inject constructor(
//    private val homeDataSource: HomeDataSource
//) : HomeRepository {
//    override suspend fun getDistinctHome(): Flow<BaseResponse<DistinctHomeIdResponse>> = homeDataSource.getDistinctHome()
//}

class HomeRepositoryImpl @Inject constructor(
    private val homeDataSource: HomeDataSource
) : HomeRepository {

    override suspend fun getDistinctHome(): Flow<DistinctHomeIdResponse> {
        return homeDataSource.getDistinctHome()
    }
}
