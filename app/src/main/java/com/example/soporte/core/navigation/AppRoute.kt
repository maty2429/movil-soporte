package com.example.soporte.core.navigation

import kotlinx.serialization.Serializable

@Serializable
object LoginRoute

@Serializable
object MainRoute

@Serializable
data class TicketDetailRoute(
    val idTicket: Int,
)
