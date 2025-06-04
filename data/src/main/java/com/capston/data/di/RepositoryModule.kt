package com.capston.data.di

import com.capston.data.repository.remote.api.DailyScheduleApi
import com.capston.data.repository.remote.api.ErrorApi
import com.capston.data.repository.remote.api.HomeApi
import com.capston.data.repository.remote.api.LectureApi
import com.capston.data.repository.remote.api.LoginApi
import com.capston.data.repository.remote.api.MyPageApi
import com.capston.data.repository.remote.api.PlanApi
import com.capston.data.repository.remote.api.RecommendApi
import com.capston.data.repository.remote.api.StudyGroupApi
import com.capston.data.repository.remote.datasourcelmpl.DailyScheduleDataSourceImpl
import com.capston.data.repository.remote.datasourcelmpl.ErrorDataSourceImpl
import com.capston.data.repository.remote.datasourcelmpl.HomeDataSourceImpl
import com.capston.data.repository.remote.datasourcelmpl.LectureDataSourceImpl
import com.capston.data.repository.remote.datasourcelmpl.LoginDataSourceImpl
import com.capston.data.repository.remote.datasourcelmpl.MyPageDataSourceImpl
import com.capston.data.repository.remote.datasourcelmpl.PlanDataSourceImpl
import com.capston.data.repository.remote.datasourcelmpl.RecommendDataSourceImpl
import com.capston.data.repository.remote.datasourcelmpl.StudyGroupDataSourceImpl
import com.capston.data.repository.remote.repositoryImpl.DailyScheduleRepositoryImpl
import com.capston.data.repository.remote.repositoryImpl.ErrorRepositoryImpl
import com.capston.data.repository.remote.repositoryImpl.HomeRepositoryImpl
import com.capston.data.repository.remote.repositoryImpl.LectureRepositoryImpl
import com.capston.data.repository.remote.repositoryImpl.LoginRepositoryImpl
import com.capston.data.repository.remote.repositoryImpl.MyPageRepositoryImpl
import com.capston.data.repository.remote.repositoryImpl.PlanRepositoryImpl
import com.capston.data.repository.remote.repositoryImpl.RecommendRepositoryImpl
import com.capston.data.repository.remote.repositoryImpl.StudyGroupRepositoryImpl
import com.capston.domain.datasource.DailyScheduleDataSource
import com.capston.domain.datasource.ErrorDataSource
import com.capston.domain.datasource.HomeDataSource
import com.capston.domain.datasource.LectureDataSource
import com.capston.domain.datasource.LoginDataSource
import com.capston.domain.datasource.MyPageDataSource
import com.capston.domain.datasource.PlanDataSource
import com.capston.domain.datasource.StudyGroupDataSource
import com.capston.domain.datasource.RecommendDataSource
import com.capston.domain.repository.DailyScheduleRepository
import com.capston.domain.repository.ErrorRepository
import com.capston.domain.repository.HomeRepository
import com.capston.domain.repository.LectureRepository
import com.capston.domain.repository.LoginRepository
import com.capston.domain.repository.MyPageRepository
import com.capston.domain.repository.PlanRepository
import com.capston.domain.repository.RecommendRepository
import com.capston.domain.repository.StudyGroupRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    // 로그인
    @Provides
    @Singleton
    fun provideLoginDataSource(
        loginApi: LoginApi
    ): LoginDataSource {
        return LoginDataSourceImpl(loginApi)
    }

    @Singleton
    @Provides
    fun provideLoginRepository(loginDataSource: LoginDataSource): LoginRepository =
        LoginRepositoryImpl(loginDataSource)

    // 홈
    @Provides
    @Singleton
    fun provideHomeDataSource(
        homeApi: HomeApi
    ): HomeDataSource {
        return HomeDataSourceImpl(homeApi)
    }

    @Singleton
    @Provides
    fun provideHomeRepository(homeDataSource: HomeDataSource): HomeRepository =
        HomeRepositoryImpl(homeDataSource)

    // 에러
    @Provides
    @Singleton
    fun provideErrorDataSource(
        errorApi: ErrorApi
    ): ErrorDataSource {
        return ErrorDataSourceImpl(errorApi)
    }

    @Singleton
    @Provides
    fun provideErrorRepository(errorDataSource: ErrorDataSource): ErrorRepository =
        ErrorRepositoryImpl(errorDataSource)

    // 계획
    @Provides
    @Singleton
    fun providePlanDataSource(
        planApi: PlanApi
    ): PlanDataSource {
        return PlanDataSourceImpl(planApi)
    }

    @Provides
    @Singleton
    fun providePlanRepository(planDataSource: PlanDataSource): PlanRepository =
        PlanRepositoryImpl(planDataSource)

    // 데일리 스케줄
    @Provides
    @Singleton
    fun provideDailyScheduleDataSource(
        dailyScheduleApi: DailyScheduleApi
    ): DailyScheduleDataSource {
        return DailyScheduleDataSourceImpl(dailyScheduleApi)
    }

    @Provides
    @Singleton
    fun provideDailyScheduleRepository(dailyScheduleDataSource: DailyScheduleDataSource): DailyScheduleRepository =
        DailyScheduleRepositoryImpl(dailyScheduleDataSource)

    // 강의 검색
    @Provides
    @Singleton
    fun provideLectureDataSource(
        lectureApi: LectureApi
    ): LectureDataSource {
        return LectureDataSourceImpl(lectureApi)
    }

    @Provides
    @Singleton
    fun provideLectureRepository(lectureDataSource: LectureDataSource): LectureRepository =
        LectureRepositoryImpl(lectureDataSource)

    // 마이페이지
    @Provides
    @Singleton
    fun provideMyPageDataSource(
        myPageApi: MyPageApi
    ): MyPageDataSource {
        return MyPageDataSourceImpl(myPageApi)
    }

    @Provides
    @Singleton
    fun provideMyPageRepository(myPageDataSource: MyPageDataSource): MyPageRepository =
        MyPageRepositoryImpl(myPageDataSource)

    // 스터디그룹
    @Provides
    @Singleton
    fun provideStudyGroupDataSource(
        studyGroupApi: StudyGroupApi
    ): StudyGroupDataSource {
        return StudyGroupDataSourceImpl(studyGroupApi)
    }

    @Provides
    @Singleton
    fun provideStudyGroupRepository(studyGroupDataSource: StudyGroupDataSource): StudyGroupRepository =
        StudyGroupRepositoryImpl(studyGroupDataSource)

    // 강의 추천
    @Provides
    @Singleton
    fun provideRecommendDataSource(
        recommandApi: RecommendApi
    ): RecommendDataSource {
       return RecommendDataSourceImpl(recommandApi)
    }

    @Provides
    @Singleton
    fun provideRecommendRepository(recommendDataSource: RecommendDataSource): RecommendRepository =
        RecommendRepositoryImpl(recommendDataSource)
}