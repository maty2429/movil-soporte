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

class CreateTicketMilestoneUseCase(
    private val repository: TicketsRepository,
) {
    suspend operator fun invoke(
        ticketId: Int,
        technicianId: Int,
        observation: String,
    ): Result<Unit> =
        repository.createTicketMilestone(
            ticketId = ticketId,
            technicianId = technicianId,
            milestoneCode = TECHNICIAN_MILESTONE_CODE,
            observation = observation,
        )

    private companion object {
        const val TECHNICIAN_MILESTONE_CODE = "TEC"
    }
}

class GetTicketMilestonesUseCase(
    private val repository: TicketsRepository,
) {
    suspend operator fun invoke(ticketId: Int) =
        repository.getTicketMilestones(ticketId)
}

class GetTransferTechniciansUseCase(
    private val repository: TicketsRepository,
) {
    suspend operator fun invoke(originTechnicianId: Int) =
        repository.getAllTechnicians()
            .map { technicians ->
                technicians.filter { technician ->
                    technician.id != null &&
                        technician.id != originTechnicianId
                }
            }
}

class GetPauseReasonsUseCase(
    private val repository: TicketsRepository,
) {
    suspend operator fun invoke() =
        repository.getPauseReasons()
}

class PauseTicketUseCase(
    private val repository: TicketsRepository,
) {
    suspend operator fun invoke(
        ticketId: Int,
        technicianId: Int,
        pauseReasonId: Int,
    ): Result<Unit> =
        repository.createTicketPause(
            ticketId = ticketId,
            technicianId = technicianId,
            pauseReasonId = pauseReasonId,
        )
}

class FinishPauseUseCase(
    private val repository: TicketsRepository,
) {
    suspend operator fun invoke(ticketId: Int): Result<Unit> =
        repository.finishTicketPause(ticketId)
}

class FinishTicketUseCase {
    suspend operator fun invoke(ticketId: Int): Result<Unit> =
        notImplementedYet(ticketId)
}

class TransferTicketUseCase(
    private val repository: TicketsRepository,
) {
    suspend operator fun invoke(
        ticketId: Int,
        originTechnicianId: Int,
        destinationTechnicianId: Int,
        reason: String,
    ): Result<Unit> =
        repository.createTicketTransfer(
            ticketId = ticketId,
            originTechnicianId = originTechnicianId,
            destinationTechnicianId = destinationTechnicianId,
            statusDescription = TRANSFER_STATUS_REQUESTED,
            milestoneCode = TRANSFER_MILESTONE_CODE,
            reason = reason,
        )

    private companion object {
        const val TRANSFER_STATUS_REQUESTED = "SOL"
        const val TRANSFER_MILESTONE_CODE = "COM"
    }
}

class RespondTransferUseCase(
    private val repository: TicketsRepository,
) {
    suspend fun accept(
        transferId: Int,
        destinationTechnicianId: Int,
    ): Result<Unit> =
        repository.respondTicketTransfer(
            transferId = transferId,
            statusCode = TRANSFER_ACCEPTED,
            destinationTechnicianId = destinationTechnicianId,
        )

    suspend fun reject(
        transferId: Int,
        destinationTechnicianId: Int,
    ): Result<Unit> =
        repository.respondTicketTransfer(
            transferId = transferId,
            statusCode = TRANSFER_REJECTED,
            destinationTechnicianId = destinationTechnicianId,
        )

    private companion object {
        const val TRANSFER_ACCEPTED = "ACE"
        const val TRANSFER_REJECTED = "REC"
    }
}

@Suppress("UNUSED_PARAMETER")
private fun notImplementedYet(vararg ignoredIds: Int): Result<Unit> =
    Result.failure(UnsupportedOperationException("Accion de ticket pendiente de integrar con backend"))
