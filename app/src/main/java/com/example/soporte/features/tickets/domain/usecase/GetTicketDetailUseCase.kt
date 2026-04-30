package com.example.soporte.features.tickets.domain.usecase

import com.example.soporte.features.tickets.domain.repository.TicketsRepository

class GetTicketDetailUseCase(
    private val repository: TicketsRepository,
) {
    suspend operator fun invoke(ticketId: Int) =
        repository.getTicketById(ticketId)
}
