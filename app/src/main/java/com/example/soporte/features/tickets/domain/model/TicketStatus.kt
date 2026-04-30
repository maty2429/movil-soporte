package com.example.soporte.features.tickets.domain.model

enum class TicketStatus(
    val code: String,
    val label: String,
) {
    Assigned("ASI", "Asignado"),
    SeenByTechnician("VITEC", "Visto"),
    InProgress("PRO", "En progreso"),
    Paused("PAU", "Pausado"),
    WorkFinished("TER", "Trabajo terminado"),
    Closed("CER", "Cerrado"),
    Unknown("SIN", "Sin estado"),
}

val ticketFilterStatuses = listOf(
    TicketStatus.Assigned,
    TicketStatus.SeenByTechnician,
    TicketStatus.InProgress,
    TicketStatus.Paused,
    TicketStatus.WorkFinished,
)

fun ticketStatusFromCode(code: String?): TicketStatus =
    TicketStatus.entries.firstOrNull { it.code == code } ?: TicketStatus.Unknown
