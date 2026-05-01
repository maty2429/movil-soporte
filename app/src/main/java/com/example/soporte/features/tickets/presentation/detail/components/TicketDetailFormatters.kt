package com.example.soporte.features.tickets.presentation.detail.components

import com.example.soporte.features.tickets.domain.model.TicketMilestone
import com.example.soporte.features.tickets.presentation.toDisplayDateTime

fun String?.dateOrUnavailable(): String =
    this?.toDisplayDateTime() ?: "No disponible"

fun TicketMilestone.timelineTitle(): String =
    milestoneTypeDescription
        ?: milestoneTypeCode
        ?: "Hito #${id ?: "-"}"

fun TicketMilestone.timelineDescription(): String {
    val observationText = observation?.takeIf { it.isNotBlank() } ?: "Sin observación"
    val technicianText = technicianName ?: technicianId?.let { "Técnico #$it" }

    return listOfNotNull(observationText, technicianText)
        .joinToString("\n")
}
