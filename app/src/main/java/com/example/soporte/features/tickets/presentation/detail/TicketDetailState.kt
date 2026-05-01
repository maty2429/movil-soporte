package com.example.soporte.features.tickets.presentation.detail

import com.example.soporte.features.tickets.domain.model.Ticket
import com.example.soporte.features.tickets.domain.model.TicketMilestone
import com.example.soporte.features.tickets.domain.model.Technician
import com.example.soporte.features.tickets.domain.model.PauseReason

data class TicketDetailState(
    val ticket: Ticket? = null,
    val isLoading: Boolean = false,
    val isStartingTicket: Boolean = false,
    val isMilestoneDialogVisible: Boolean = false,
    val milestoneObservation: String = "",
    val isCreatingMilestone: Boolean = false,
    val milestoneError: String? = null,
    val isMilestonesDialogVisible: Boolean = false,
    val milestones: List<TicketMilestone> = emptyList(),
    val isLoadingMilestones: Boolean = false,
    val milestonesError: String? = null,
    val isTransferDialogVisible: Boolean = false,
    val transferTechnicians: List<Technician> = emptyList(),
    val isLoadingTransferTechnicians: Boolean = false,
    val selectedTransferTechnicianId: Int? = null,
    val transferReason: String = "",
    val isCreatingTransfer: Boolean = false,
    val transferError: String? = null,
    val isPauseDialogVisible: Boolean = false,
    val pauseReasons: List<PauseReason> = emptyList(),
    val isLoadingPauseReasons: Boolean = false,
    val selectedPauseReasonId: Int? = null,
    val isCreatingPause: Boolean = false,
    val pauseError: String? = null,
    val isFinishingPause: Boolean = false,
    val finishPauseError: String? = null,
    val transferResponseId: Int? = null,
    val canRespondToTransfer: Boolean = false,
    val isRespondingTransfer: Boolean = false,
    val respondingTransferAction: TransferResponseAction? = null,
    val transferResponseError: String? = null,
    val error: String? = null,
)

enum class TransferResponseAction {
    Accept,
    Reject,
}
