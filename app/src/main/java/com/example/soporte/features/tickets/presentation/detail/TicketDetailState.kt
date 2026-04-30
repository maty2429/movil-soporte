package com.example.soporte.features.tickets.presentation.detail

import com.example.soporte.features.tickets.domain.model.Ticket

data class TicketDetailState(
    val ticket: Ticket? = null,
    val isLoading: Boolean = false,
    val isStartingTicket: Boolean = false,
    val error: String? = null,
)
