package com.capston.domain.datasource

interface OnboardingPreferenceStorage {
    fun isFirstLogin(): Boolean
    fun setFirstLoginDone()
}

