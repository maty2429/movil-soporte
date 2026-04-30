package com.example.soporte.features.tickets.presentation.list

import com.example.soporte.features.tickets.domain.model.Technician
import com.example.soporte.features.tickets.domain.model.Ticket
import com.example.soporte.features.tickets.domain.model.TicketStatus

data class TicketsState(
    val technician: Technician? = null,
    val selectedStatus: TicketStatus = TicketStatus.Assigned,
    val tickets: List<Ticket> = emptyList(),
    val isLoadingTechnician: Boolean = false,
    val isLoadingTickets: Boolean = false,
    val error: String? = null,
)
