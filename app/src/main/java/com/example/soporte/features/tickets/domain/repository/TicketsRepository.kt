package com.example.soporte.features.tickets.domain.repository

import com.example.soporte.features.tickets.domain.model.Technician
import com.example.soporte.features.tickets.domain.model.Ticket

import kotlinx.coroutines.flow.Flow

interface TicketsRepository {
    val tickets: Flow<List<Ticket>>
    suspend fun getTechnicianByRut(rut: String): Result<Technician>
    suspend fun getTickets(technicianId: Int, statusCode: String): Result<List<Ticket>>
    suspend fun getTicketById(ticketId: Int): Result<Ticket>
    suspend fun markTicketAsSeen(ticketId: Int, technicianId: Int): Result<Unit>
    suspend fun startTicket(ticketId: Int, technicianId: Int): Result<Unit>
}
