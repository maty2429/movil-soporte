package com.example.soporte.features.tickets

import com.example.soporte.features.tickets.domain.model.Technician
import com.example.soporte.features.tickets.domain.model.Ticket
import com.example.soporte.features.tickets.domain.repository.TicketsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class FakeTicketsRepository : TicketsRepository {
    private val _tickets = MutableStateFlow<List<Ticket>>(emptyList())
    override val tickets = _tickets.asStateFlow()

    var technicianResult: Result<Technician> = Result.success(
        Technician(
            id = 2,
            rut = "1",
            dv = "9",
            fullName = "Tecnico Test",
            typeDescription = "Tecnico de campo",
            supportDepartmentDescription = "Informatica",
        ),
    )
    var detailResult: Result<Ticket> = Result.success(TicketFixtures.ticket(id = 23, number = "NA6OI8-26"))
    var markSeenResult: Result<Unit> = Result.success(Unit)
    var startTicketResult: Result<Unit> = Result.success(Unit)
    val ticketsByStatus = mutableMapOf<String, Result<List<Ticket>>>()
    val requestedRut = mutableListOf<String>()
    val ticketRequests = mutableListOf<Pair<Int, String>>()
    val detailRequests = mutableListOf<Int>()
    val markSeenRequests = mutableListOf<Pair<Int, Int>>()
    val startTicketRequests = mutableListOf<Pair<Int, Int>>()

    override suspend fun getTechnicianByRut(rut: String): Result<Technician> {
        requestedRut += rut
        return technicianResult
    }

    override suspend fun getTickets(
        technicianId: Int,
        statusCode: String,
    ): Result<List<Ticket>> {
        ticketRequests += technicianId to statusCode
        return (ticketsByStatus[statusCode] ?: Result.success(emptyList()))
            .onSuccess { _tickets.value = it }
    }

    override suspend fun getTicketById(ticketId: Int): Result<Ticket> {
        detailRequests += ticketId
        return detailResult.onSuccess { updatedTicket ->
            _tickets.update { currentTickets ->
                currentTickets.map { if (it.id == updatedTicket.id) updatedTicket else it }
            }
        }
    }

    override suspend fun markTicketAsSeen(
        ticketId: Int,
        technicianId: Int,
    ): Result<Unit> {
        markSeenRequests += ticketId to technicianId
        return markSeenResult
    }

    override suspend fun startTicket(
        ticketId: Int,
        technicianId: Int,
    ): Result<Unit> {
        startTicketRequests += ticketId to technicianId
        return startTicketResult.onSuccess {
            _tickets.update { currentTickets ->
                currentTickets.map { ticket ->
                    if (ticket.id == ticketId) {
                        ticket.copy(status = ticket.status.copy(code = "PRO", description = "PRO"))
                    } else {
                        ticket
                    }
                }
            }
        }
    }
}
