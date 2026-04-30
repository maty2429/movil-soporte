package com.example.soporte.features.tickets.domain.usecase

import com.example.soporte.features.tickets.domain.repository.TicketsRepository

class MarkTicketAsSeenUseCase(
    private val repository: TicketsRepository,
) {
    suspend operator fun invoke(
        ticketId: Int,
        technicianId: Int,
    ): Result<Unit> =
        repository.markTicketAsSeen(
            ticketId = ticketId,
            technicianId = technicianId,
        )
}
