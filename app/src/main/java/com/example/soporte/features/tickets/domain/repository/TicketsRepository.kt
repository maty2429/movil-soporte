package com.example.soporte.features.tickets.domain.repository

import com.example.soporte.features.tickets.domain.model.Technician
import com.example.soporte.features.tickets.domain.model.Ticket
import com.example.soporte.features.tickets.domain.model.TicketMilestone
import com.example.soporte.features.tickets.domain.model.PauseReason

import kotlinx.coroutines.flow.Flow

interface TicketsRepository {
    val tickets: Flow<List<Ticket>>
    suspend fun getAllTechnicians(): Result<List<Technician>>
    suspend fun getPauseReasons(): Result<List<PauseReason>>
    suspend fun getTechnicianByRut(rut: String): Result<Technician>
    suspend fun getTickets(technicianId: Int, statusCode: String): Result<List<Ticket>>
    suspend fun getTicketById(ticketId: Int): Result<Ticket>
    suspend fun getTicketMilestones(ticketId: Int): Result<List<TicketMilestone>>
    suspend fun markTicketAsSeen(ticketId: Int, technicianId: Int): Result<Unit>
    suspend fun startTicket(ticketId: Int, technicianId: Int): Result<Unit>
    suspend fun createTicketMilestone(
        ticketId: Int,
        technicianId: Int,
        milestoneCode: String,
        observation: String,
    ): Result<Unit>
    suspend fun createTicketTransfer(
        ticketId: Int,
        originTechnicianId: Int,
        destinationTechnicianId: Int,
        statusDescription: String,
        milestoneCode: String,
        reason: String,
    ): Result<Unit>
    suspend fun respondTicketTransfer(
        transferId: Int,
        statusCode: String,
        destinationTechnicianId: Int,
    ): Result<Unit>
    suspend fun createTicketPause(
        ticketId: Int,
        technicianId: Int,
        pauseReasonId: Int,
    ): Result<Unit>
    suspend fun finishTicketPause(ticketId: Int): Result<Unit>
}
