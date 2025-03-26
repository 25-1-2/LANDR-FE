package com.capston.domain.response

import com.capston.domain.base.BaseLoadingState

data class BaseResponse<T>(
    var result: Result = Result(),
    var payload: T? = null,
    var status: BaseLoadingState = BaseLoadingState.IDLE
)

// Result 클래스
data class Result(
    var code: Int = 0,
    var message: String = ""
)