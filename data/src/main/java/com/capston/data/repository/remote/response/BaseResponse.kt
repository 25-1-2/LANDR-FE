package com.capston.data.repository.remote.response

import com.capston.data.base.BaseLoadingState

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