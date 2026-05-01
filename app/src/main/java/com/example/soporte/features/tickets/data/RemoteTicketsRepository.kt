package com.example.soporte.features.tickets.data

import com.example.soporte.core.network.SoporteApiClient
import com.example.soporte.features.tickets.data.mapper.toDomain
import com.example.soporte.features.tickets.domain.model.Technician
import com.example.soporte.features.tickets.domain.model.Ticket
import com.example.soporte.features.tickets.domain.repository.TicketsRepository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class RemoteTicketsRepository(
    private val apiClient: SoporteApiClient,
) : TicketsRepository {

    private val _tickets = MutableStateFlow<List<Ticket>>(emptyList())
    override val tickets = _tickets.asStateFlow()

    override suspend fun getAllTechnicians(): Result<List<Technician>> =
        runCatching { apiClient.getAllTecnicos().map { it.toDomain() } }

    override suspend fun getPauseReasons() =
        runCatching {
            apiClient.getPauseReasons()
                .map { it.toDomain() }
                .filter { it.id > 0 && it.reason.isNotBlank() }
        }

    override suspend fun getTechnicianByRut(rut: String): Result<Technician> =
        runCatching { apiClient.getTecnicoByRut(rut).toDomain() }

    override suspend fun getTickets(
        technicianId: Int,
        statusCode: String,
    ): Result<List<Ticket>> =
        runCatching {
            apiClient.getTicketsByTechnicianAndStatus(
                technicianId = technicianId,
                statusCode = statusCode,
            ).map { it.toDomain() }
        }.onSuccess { fetchedTickets ->
            _tickets.value = fetchedTickets
        }

    override suspend fun getTicketById(ticketId: Int): Result<Ticket> =
        runCatching { apiClient.getTicketById(ticketId).toDomain() }
            .onSuccess { updatedTicket ->
                _tickets.update { currentTickets ->
                    currentTickets.map { 
                        if (it.id == updatedTicket.id) updatedTicket else it 
                    }
                }
            }

    override suspend fun getTicketMilestones(ticketId: Int) =
        runCatching {
            apiClient.getTicketMilestones(ticketId).map { it.toDomain() }
        }

    override suspend fun markTicketAsSeen(
        ticketId: Int,
        technicianId: Int,
    ): Result<Unit> =
        runCatching {
            apiClient.markTicketAsSeen(
                ticketId = ticketId,
                technicianId = technicianId,
            )
        }.onSuccess {
            _tickets.update { currentTickets ->
                currentTickets.map { ticket ->
                    if (ticket.id == ticketId) {
                        ticket.copy(
                            status = ticket.status.copy(
                                code = "VITEC",
                                description = "Visto por técnico"
                            )
                        )
                    } else {
                        ticket
                    }
                }
            }
        }

    override suspend fun startTicket(
        ticketId: Int,
        technicianId: Int,
    ): Result<Unit> =
        runCatching {
            apiClient.startTicket(
                ticketId = ticketId,
                technicianId = technicianId,
            )
        }.onSuccess {
            _tickets.update { currentTickets ->
                currentTickets.map { ticket ->
                    if (ticket.id == ticketId) {
                        ticket.copy(
                            status = ticket.status.copy(
                                code = "PRO",
                                description = "En progreso"
                            )
                        )
                    } else {
                        ticket
                    }
                }
            }
        }

    override suspend fun createTicketMilestone(
        ticketId: Int,
        technicianId: Int,
        milestoneCode: String,
        observation: String,
    ): Result<Unit> =
        runCatching {
            apiClient.createTicketMilestone(
                ticketId = ticketId,
                technicianId = technicianId,
                milestoneCode = milestoneCode,
                observation = observation,
            )
        }

    override suspend fun createTicketTransfer(
        ticketId: Int,
        originTechnicianId: Int,
        destinationTechnicianId: Int,
        statusDescription: String,
        milestoneCode: String,
        reason: String,
    ): Result<Unit> =
        runCatching {
            apiClient.createTicketTransfer(
                ticketId = ticketId,
                originTechnicianId = originTechnicianId,
                destinationTechnicianId = destinationTechnicianId,
                statusDescription = statusDescription,
                milestoneCode = milestoneCode,
                reason = reason,
            )
        }

    override suspend fun respondTicketTransfer(
        transferId: Int,
        statusCode: String,
        destinationTechnicianId: Int,
    ): Result<Unit> =
        runCatching {
            apiClient.respondTicketTransfer(
                transferId = transferId,
                statusCode = statusCode,
                destinationTechnicianId = destinationTechnicianId,
            )
        }

    override suspend fun createTicketPause(
        ticketId: Int,
        technicianId: Int,
        pauseReasonId: Int,
    ): Result<Unit> =
        runCatching {
            apiClient.createTicketPause(
                ticketId = ticketId,
                technicianId = technicianId,
                pauseReasonId = pauseReasonId,
            )
        }

    override suspend fun finishTicketPause(ticketId: Int): Result<Unit> =
        runCatching {
            apiClient.finishTicketPause(ticketId)
        }
}
