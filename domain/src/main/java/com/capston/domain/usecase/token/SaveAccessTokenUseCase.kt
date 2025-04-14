package com.capston.domain.usecase.token

import com.capston.domain.repository.TokenRepository
import javax.inject.Inject

class SaveAccessTokenUseCase @Inject constructor(
    private val tokenRepository: TokenRepository
) {
    suspend operator fun invoke(accessToken: String) {
        tokenRepository.saveAccessToken(accessToken)
    }
}