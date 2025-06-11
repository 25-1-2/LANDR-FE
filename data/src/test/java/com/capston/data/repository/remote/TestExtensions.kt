package com.capston.data.repository.remote

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest

/**
 * Flow를 테스트하기 위한 확장 함수들
 */
suspend fun <T> Flow<T>.test(): List<T> = this.toList()

/**
 * 코루틴 테스트를 위한 확장 함수
 */
fun runTestWithScope(testBody: suspend TestScope.() -> Unit) = runTest {
    testBody()
}
