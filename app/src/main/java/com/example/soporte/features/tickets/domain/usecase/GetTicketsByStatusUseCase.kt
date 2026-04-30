package com.example.soporte.features.tickets.domain.usecase

import com.example.soporte.features.tickets.domain.repository.TicketsRepository

class GetTicketsByStatusUseCase(
    private val repository: TicketsRepository,
) {
    suspend operator fun invoke(
        technicianId: Int,
        statusCode: String,
    ) = repository.getTickets(
        technicianId = technicianId,
        statusCode = statusCode,
    )
}
