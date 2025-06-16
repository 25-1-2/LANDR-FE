package com.capston.presentation.ui.common

data class UserSubscriptionStatus(
    val isPremium: Boolean = false,
    val subscriptionType: SubscriptionType = SubscriptionType.FREE,
    val expiryDate: String? = null
)