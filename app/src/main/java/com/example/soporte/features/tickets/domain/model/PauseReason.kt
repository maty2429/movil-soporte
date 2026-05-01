package com.example.soporte.features.tickets.domain.model

data class PauseReason(
    val id: Int,
    val reason: String,
    val requiresAuthorization: Boolean,
)
