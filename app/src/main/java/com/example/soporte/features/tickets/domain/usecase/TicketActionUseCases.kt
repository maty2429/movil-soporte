package com.example.soporte.features.tickets.domain.usecase

import com.example.soporte.features.tickets.domain.repository.TicketsRepository

class StartTicketUseCase(
    private val repository: TicketsRepository,
) {
    suspend operator fun invoke(
        ticketId: Int,
        technicianId: Int,
    ): Result<Unit> =
        repository.startTicket(
            ticketId = ticketId,
            technicianId = technicianId,
        )
}

class PauseTicketUseCase {
    suspend operator fun invoke(ticketId: Int): Result<Unit> =
        notImplementedYet(ticketId)
}

class FinishTicketUseCase {
    suspend operator fun invoke(ticketId: Int): Result<Unit> =
        notImplementedYet(ticketId)
}

class TransferTicketUseCase {
    suspend operator fun invoke(
        ticketId: Int,
        targetTechnicianId: Int,
    ): Result<Unit> =
        notImplementedYet(ticketId, targetTechnicianId)
}

@Suppress("UNUSED_PARAMETER")
private fun notImplementedYet(vararg ignoredIds: Int): Result<Unit> =
    Result.failure(UnsupportedOperationException("Accion de ticket pendiente de integrar con backend"))
