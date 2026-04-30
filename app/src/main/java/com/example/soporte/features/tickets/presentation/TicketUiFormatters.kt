package com.example.soporte.features.tickets.presentation

import com.example.soporte.features.tickets.domain.model.Ticket

fun Ticket.titleText(): String =
    failureCatalog.description
        ?: reportedFailure
        ?: "Ticket sin detalle"

fun Ticket.requesterText(): String =
    requester.fullName ?: "Solicitante no informado"

fun Ticket.locationText(): String {
    val parts = listOfNotNull(
        service.building,
        service.floor?.let { "Piso $it" },
        service.location,
        locationObservation?.takeIf { it.isNotBlank() && it != "SIN OBSERVACION" },
    )

    return parts.joinToString(" - ").ifBlank { "Ubicacion no informada" }
}

fun Ticket.statusCode(): String =
    status.code

fun Ticket.statusLabel(): String =
    status.description ?: statusCode()

fun Ticket.priorityText(): String =
    priority ?: "Sin prioridad"

fun Ticket.serviceText(): String =
    service.serviceName ?: "No informado"

fun Ticket.unitText(): String =
    service.unitName ?: "No informada"

fun Ticket.buildingText(): String =
    service.building ?: "No informado"

fun Ticket.floorText(): String =
    service.floor?.let { "Piso $it" } ?: "No informado"

fun Ticket.locationDetailText(): String =
    service.location ?: "No informado"

fun Ticket.locationObservationText(): String =
    locationObservation?.takeIf { it.isNotBlank() } ?: "No informado"

fun Ticket.failureCatalogText(): String =
    failureCatalog.description ?: "No informado"

fun Ticket.categoryText(): String {
    val parts = listOfNotNull(
        failureCatalog.category,
        failureCatalog.subcategory,
    )
    return parts.joinToString(" / ").ifBlank { "No informado" }
}

fun Ticket.complexityText(): String =
    failureCatalog.complexity?.let { "Nivel $it" } ?: "No informado"

fun Ticket.requiresPhysicalVisitText(): String =
    when (failureCatalog.requiresPhysicalVisit) {
        true -> "Si"
        false -> "No"
        null -> "No informado"
    }

fun Ticket.criticalText(): String =
    if (isCritical) "Critico" else "No critico"

fun String.toDisplayDateTime(): String =
    if (length >= 16) {
        val date = take(10).replace("-", "/")
        val time = substring(11, 16)
        "$date $time"
    } else {
        this
    }
