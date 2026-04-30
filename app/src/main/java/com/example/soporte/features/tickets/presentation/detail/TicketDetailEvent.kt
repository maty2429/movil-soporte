package com.example.soporte.features.tickets.presentation.detail

sealed interface TicketDetailEvent {
    data object StartTicket : TicketDetailEvent
    data object CreateMilestone : TicketDetailEvent
    data object ViewMilestones : TicketDetailEvent
    data object TransferTicket : TicketDetailEvent
    data object PauseTicket : TicketDetailEvent
    data object FinishTicket : TicketDetailEvent
    data object Retry : TicketDetailEvent
}
