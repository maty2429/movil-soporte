package com.example.soporte.core.navigation

import kotlinx.serialization.Serializable

@Serializable
object LoginRoute

@Serializable
object MainRoute

@Serializable
object AutoAssignedTicketRoute

@Serializable
data class TicketDetailRoute(
    val idTicket: Int,
    val idTraspaso: Int? = null,
    val isReceivedTransfer: Boolean = false,
)
