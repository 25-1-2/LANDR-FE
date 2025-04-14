package com.capston.domain.base

enum class BaseLoadingState {
    IDLE,       // 초기 상태 또는 로딩 전
    LOADING,    // 로딩 중
    SUCCESS,    // 성공적으로 데이터 로드 완료
    ERROR
}