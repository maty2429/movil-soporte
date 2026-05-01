package com.example.soporte.features.tickets.domain.model

data class TicketMilestone(
    val id: Int?,
    val ticketId: Int?,
    val technicianId: Int?,
    val milestoneTypeId: Int?,
    val date: String?,
    val observation: String?,
    val technicianName: String?,
    val milestoneTypeCode: String?,
    val milestoneTypeDescription: String?,
)
