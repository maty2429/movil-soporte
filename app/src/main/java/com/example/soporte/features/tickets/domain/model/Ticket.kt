package com.example.soporte.features.tickets.domain.model

data class Ticket(
    val id: Int?,
    val number: String?,
    val reportedFailure: String?,
    val isCritical: Boolean,
    val locationObservation: String?,
    val createdAt: String?,
    val updatedAt: String?,
    val workStartedAt: String?,
    val workFinishedAt: String?,
    val service: TicketService,
    val requester: TicketRequester,
    val status: TicketStatusInfo,
    val priority: String?,
    val failureCatalog: FailureCatalog,
)

data class TicketService(
    val building: String?,
    val floor: Int?,
    val location: String?,
    val serviceName: String?,
    val unitName: String?,
)

data class TicketRequester(
    val fullName: String?,
    val extension: Int?,
)

data class TicketStatusInfo(
    val code: String,
    val description: String?,
) {
    val type: TicketStatus = ticketStatusFromCode(code)
}

data class FailureCatalog(
    val description: String?,
    val category: String?,
    val subcategory: String?,
    val complexity: Int?,
    val requiresPhysicalVisit: Boolean?,
)
