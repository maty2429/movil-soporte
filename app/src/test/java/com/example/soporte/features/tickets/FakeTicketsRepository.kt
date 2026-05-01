package com.example.soporte.features.tickets

import com.example.soporte.features.tickets.domain.model.Technician
import com.example.soporte.features.tickets.domain.model.Ticket
import com.example.soporte.features.tickets.domain.model.TicketMilestone
import com.example.soporte.features.tickets.domain.model.PauseReason
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
    var techniciansResult: Result<List<Technician>> = Result.success(
        listOf(
            technicianResult.getOrThrow(),
            Technician(
                id = 3,
                rut = "2",
                dv = "7",
                fullName = "Tecnico Destino",
                typeDescription = "Tecnico de campo",
                supportDepartmentDescription = "Informatica",
            ),
        ),
    )
    var detailResult: Result<Ticket> = Result.success(TicketFixtures.ticket(id = 23, number = "NA6OI8-26"))
    var milestonesResult: Result<List<TicketMilestone>> = Result.success(emptyList())
    var markSeenResult: Result<Unit> = Result.success(Unit)
    var startTicketResult: Result<Unit> = Result.success(Unit)
    var createMilestoneResult: Result<Unit> = Result.success(Unit)
    var createTransferResult: Result<Unit> = Result.success(Unit)
    var respondTransferResult: Result<Unit> = Result.success(Unit)
    var pauseReasonsResult: Result<List<PauseReason>> = Result.success(
        listOf(
            PauseReason(
                id = 4,
                reason = "Esperando repuesto",
                requiresAuthorization = true,
            ),
        ),
    )
    var createPauseResult: Result<Unit> = Result.success(Unit)
    var finishPauseResult: Result<Unit> = Result.success(Unit)
    val ticketsByStatus = mutableMapOf<String, Result<List<Ticket>>>()
    var allTechniciansRequests = 0
    val requestedRut = mutableListOf<String>()
    val ticketRequests = mutableListOf<Pair<Int, String>>()
    val detailRequests = mutableListOf<Int>()
    val milestoneRequests = mutableListOf<Int>()
    val markSeenRequests = mutableListOf<Pair<Int, Int>>()
    val startTicketRequests = mutableListOf<Pair<Int, Int>>()
    val createMilestoneRequests = mutableListOf<CreateMilestoneRequest>()
    val createTransferRequests = mutableListOf<CreateTransferRequest>()
    val respondTransferRequests = mutableListOf<RespondTransferRequest>()
    val createPauseRequests = mutableListOf<CreatePauseRequest>()
    val finishPauseRequests = mutableListOf<Int>()

    override suspend fun getAllTechnicians(): Result<List<Technician>> {
        allTechniciansRequests += 1
        return techniciansResult
    }

    override suspend fun getPauseReasons(): Result<List<PauseReason>> =
        pauseReasonsResult

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

    override suspend fun getTicketMilestones(ticketId: Int): Result<List<TicketMilestone>> {
        milestoneRequests += ticketId
        return milestonesResult
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

    override suspend fun createTicketMilestone(
        ticketId: Int,
        technicianId: Int,
        milestoneCode: String,
        observation: String,
    ): Result<Unit> {
        createMilestoneRequests += CreateMilestoneRequest(
            ticketId = ticketId,
            technicianId = technicianId,
            milestoneCode = milestoneCode,
            observation = observation,
        )
        return createMilestoneResult
    }

    override suspend fun createTicketTransfer(
        ticketId: Int,
        originTechnicianId: Int,
        destinationTechnicianId: Int,
        statusDescription: String,
        milestoneCode: String,
        reason: String,
    ): Result<Unit> {
        createTransferRequests += CreateTransferRequest(
            ticketId = ticketId,
            originTechnicianId = originTechnicianId,
            destinationTechnicianId = destinationTechnicianId,
            statusDescription = statusDescription,
            milestoneCode = milestoneCode,
            reason = reason,
        )
        return createTransferResult
    }

    override suspend fun respondTicketTransfer(
        transferId: Int,
        statusCode: String,
        destinationTechnicianId: Int,
    ): Result<Unit> {
        respondTransferRequests += RespondTransferRequest(
            transferId = transferId,
            statusCode = statusCode,
            destinationTechnicianId = destinationTechnicianId,
        )
        return respondTransferResult
    }

    override suspend fun createTicketPause(
        ticketId: Int,
        technicianId: Int,
        pauseReasonId: Int,
    ): Result<Unit> {
        createPauseRequests += CreatePauseRequest(
            ticketId = ticketId,
            technicianId = technicianId,
            pauseReasonId = pauseReasonId,
        )
        return createPauseResult
    }

    override suspend fun finishTicketPause(ticketId: Int): Result<Unit> {
        finishPauseRequests += ticketId
        return finishPauseResult
    }

    data class CreateMilestoneRequest(
        val ticketId: Int,
        val technicianId: Int,
        val milestoneCode: String,
        val observation: String,
    )

    data class CreateTransferRequest(
        val ticketId: Int,
        val originTechnicianId: Int,
        val destinationTechnicianId: Int,
        val statusDescription: String,
        val milestoneCode: String,
        val reason: String,
    )

    data class RespondTransferRequest(
        val transferId: Int,
        val statusCode: String,
        val destinationTechnicianId: Int,
    )

    data class CreatePauseRequest(
        val ticketId: Int,
        val technicianId: Int,
        val pauseReasonId: Int,
    )
}
