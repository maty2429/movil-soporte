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
}
