package com.example.soporte.features.tickets

import com.example.soporte.features.tickets.domain.model.FailureCatalog
import com.example.soporte.features.tickets.domain.model.Ticket
import com.example.soporte.features.tickets.domain.model.TicketRequester
import com.example.soporte.features.tickets.domain.model.TicketService
import com.example.soporte.features.tickets.domain.model.TicketStatusInfo

object TicketFixtures {
    fun ticket(
        id: Int,
        number: String,
        status: String = "ASI",
        reportedFailure: String? = "No funciona",
    ) = Ticket(
        id = id,
        number = number,
        reportedFailure = reportedFailure,
        isCritical = true,
        locationObservation = "Sin observacion",
        createdAt = "2026-04-24T12:05:59.990206-04:00",
        updatedAt = "2026-04-24T12:05:59.990206-04:00",
        workStartedAt = null,
        workFinishedAt = null,
        service = TicketService(
            building = "TORRE",
            floor = 1,
            location = "SUBIENDO LAS ESCALERAS",
            serviceName = "INFORMATICA",
            unitName = "DESARROLLO",
        ),
        requester = TicketRequester(
            fullName = "MATIAS GODOY",
            extension = 1234,
        ),
        status = TicketStatusInfo(
            code = status,
            description = status,
        ),
        priority = "ALTA",
        failureCatalog = FailureCatalog(
            description = "No funciona",
            category = "Hardware",
            subcategory = "Teclado",
            complexity = 1,
            requiresPhysicalVisit = true,
        ),
    )
}
