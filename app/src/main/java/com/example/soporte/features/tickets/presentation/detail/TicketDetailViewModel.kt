package com.example.soporte.features.tickets.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.soporte.core.session.SessionManager
import com.example.soporte.features.tickets.domain.model.TicketStatus
import com.example.soporte.features.tickets.domain.repository.TicketsRepository
import com.example.soporte.features.tickets.domain.usecase.CreateTicketMilestoneUseCase
import com.example.soporte.features.tickets.domain.usecase.FinishPauseUseCase
import com.example.soporte.features.tickets.domain.usecase.GetPauseReasonsUseCase
import com.example.soporte.features.tickets.domain.usecase.GetTicketDetailUseCase
import com.example.soporte.features.tickets.domain.usecase.GetTicketMilestonesUseCase
import com.example.soporte.features.tickets.domain.usecase.GetTransferTechniciansUseCase
import com.example.soporte.features.tickets.domain.usecase.PauseTicketUseCase
import com.example.soporte.features.tickets.domain.usecase.RespondTransferUseCase
import com.example.soporte.features.tickets.domain.usecase.StartTicketUseCase
import com.example.soporte.features.tickets.domain.usecase.TransferTicketUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TicketDetailViewModel(
    private val getTicketDetail: GetTicketDetailUseCase,
    private val getTicketMilestones: GetTicketMilestonesUseCase,
    private val getTransferTechnicians: GetTransferTechniciansUseCase,
    private val getPauseReasons: GetPauseReasonsUseCase,
    private val startTicket: StartTicketUseCase,
    private val createTicketMilestone: CreateTicketMilestoneUseCase,
    private val transferTicket: TransferTicketUseCase,
    private val respondTransfer: RespondTransferUseCase,
    private val pauseTicket: PauseTicketUseCase,
    private val finishPause: FinishPauseUseCase,
    private val repository: TicketsRepository,
    private val sessionManager: SessionManager,
) : ViewModel() {

    private val _state = MutableStateFlow(TicketDetailState())
    val state = _state.asStateFlow()
    private var currentTicketId: Int? = null
    private var startInFlight = false
    private var milestoneInFlight = false
    private var transferInFlight = false
    private var transferResponseInFlight = false
    private var pauseInFlight = false
    private var finishPauseInFlight = false

    init {
        observeRepositoryTickets()
    }

    private fun observeRepositoryTickets() {
        repository.tickets
            .onEach { tickets ->
                val updatedTicket = tickets.find { it.id == currentTicketId }
                if (updatedTicket != null) {
                    _state.update { it.copy(ticket = updatedTicket) }
                }
            }
            .launchIn(viewModelScope)
    }

    fun loadTicket(
        ticketId: Int,
        transferId: Int? = null,
        isReceivedTransfer: Boolean = false,
    ) {
        currentTicketId = ticketId
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = true,
                    error = null,
                    transferResponseId = transferId,
                    canRespondToTransfer = isReceivedTransfer && transferId != null,
                    respondingTransferAction = null,
                    transferResponseError = null,
                )
            }

            fetchTicketDetail(ticketId)
        }
    }

    private suspend fun refreshTicket(ticketId: Int) {
        currentTicketId = ticketId
        _state.update {
            it.copy(
                isLoading = true,
                error = null,
            )
        }
        fetchTicketDetail(ticketId)
    }

    private suspend fun fetchTicketDetail(ticketId: Int) {
        getTicketDetail(ticketId)
            .onSuccess { ticket ->
                _state.update {
                    it.copy(
                        ticket = ticket,
                        isLoading = false,
                    )
                }
            }
            .onFailure { error ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = error.message ?: "No se pudo cargar el ticket",
                    )
                }
            }
    }

    private fun ticketIdForStatus(status: TicketStatus): Int? {
        val ticket = state.value.ticket ?: return null
        val ticketId = ticket.id ?: return null
        if (ticket.status.type != status) return null
        return ticketId
    }

    private fun currentTicketIdFromState(): Int? =
        state.value.ticket?.id

    private fun technicianIdOrSetError(setError: (String) -> Unit): Int? {
        val technicianId = sessionManager.technician.value?.id
        if (technicianId == null) {
            setError(NO_TECHNICIAN_ID_ERROR)
        }
        return technicianId
    }

    fun onAcceptTransferClick(onSuccess: () -> Unit) {
        respondToTransfer(accept = true, onSuccess = onSuccess)
    }

    fun onRejectTransferClick(onSuccess: () -> Unit) {
        respondToTransfer(accept = false, onSuccess = onSuccess)
    }

    private fun respondToTransfer(
        accept: Boolean,
        onSuccess: () -> Unit,
    ) {
        val transferId = state.value.transferResponseId ?: return
        if (!state.value.canRespondToTransfer) return
        if (transferResponseInFlight) return

        val technicianId = technicianIdOrSetError { message ->
            _state.update { it.copy(transferResponseError = message) }
        } ?: return

        transferResponseInFlight = true
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isRespondingTransfer = true,
                    respondingTransferAction = if (accept) TransferResponseAction.Accept else TransferResponseAction.Reject,
                    transferResponseError = null,
                )
            }

            val result = if (accept) {
                respondTransfer.accept(
                    transferId = transferId,
                    destinationTechnicianId = technicianId,
                )
            } else {
                respondTransfer.reject(
                    transferId = transferId,
                    destinationTechnicianId = technicianId,
                )
            }

            result
                .onSuccess {
                    _state.update {
                        it.copy(
                            isRespondingTransfer = false,
                            respondingTransferAction = null,
                            transferResponseError = null,
                        )
                    }
                    onSuccess()
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            isRespondingTransfer = false,
                            respondingTransferAction = null,
                            transferResponseError = error.message ?: "No se pudo responder el traspaso",
                        )
                    }
                }

            transferResponseInFlight = false
        }
    }

    fun onStartTicketClick() {
        val ticketId = ticketIdForStatus(TicketStatus.SeenByTechnician) ?: return
        if (startInFlight) return

        val technicianId = technicianIdOrSetError { message ->
            _state.update { it.copy(error = message) }
        } ?: return

        startInFlight = true
        viewModelScope.launch {
            _state.update { it.copy(isStartingTicket = true, error = null) }

            startTicket(
                ticketId = ticketId,
                technicianId = technicianId,
            )
                .onSuccess {
                    refreshTicket(ticketId)
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            error = error.message ?: "No se pudo iniciar el ticket",
                        )
                    }
                }

            startInFlight = false
            _state.update { it.copy(isStartingTicket = false) }
        }
    }

    fun onMilestoneClick() {
        if (ticketIdForStatus(TicketStatus.InProgress) == null) return

        _state.update {
            it.copy(
                isMilestoneDialogVisible = true,
                milestoneError = null,
            )
        }
    }

    fun onMilestoneObservationChange(observation: String) {
        _state.update {
            it.copy(
                milestoneObservation = observation.take(MAX_MILESTONE_OBSERVATION_LENGTH),
                milestoneError = null,
            )
        }
    }

    fun onDismissMilestoneDialog() {
        if (state.value.isCreatingMilestone) return

        _state.update {
            it.copy(
                isMilestoneDialogVisible = false,
                milestoneObservation = "",
                milestoneError = null,
            )
        }
    }

    fun onMilestonesClick() {
        val ticketId = currentTicketIdFromState() ?: return

        _state.update {
            it.copy(
                isMilestonesDialogVisible = true,
                isLoadingMilestones = true,
                milestones = emptyList(),
                milestonesError = null,
            )
        }

        viewModelScope.launch {
            getTicketMilestones(ticketId)
                .onSuccess { milestones ->
                    _state.update {
                        it.copy(
                            milestones = milestones,
                            isLoadingMilestones = false,
                            milestonesError = null,
                        )
                    }
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            isLoadingMilestones = false,
                            milestonesError = error.message ?: "No se pudieron cargar los hitos",
                        )
                    }
                }
        }
    }

    fun onDismissMilestonesDialog() {
        _state.update {
            it.copy(
                isMilestonesDialogVisible = false,
                milestonesError = null,
            )
        }
    }

    fun onTransferClick() {
        if (ticketIdForStatus(TicketStatus.InProgress) == null) return

        val originTechnicianId = technicianIdOrSetError { message ->
            _state.update {
                it.copy(
                    isTransferDialogVisible = true,
                    isLoadingTransferTechnicians = false,
                    transferTechnicians = emptyList(),
                    selectedTransferTechnicianId = null,
                    transferReason = "",
                    transferError = message,
                )
            }
        } ?: return

        _state.update {
            it.copy(
                isTransferDialogVisible = true,
                isLoadingTransferTechnicians = true,
                transferTechnicians = emptyList(),
                selectedTransferTechnicianId = null,
                transferReason = "",
                transferError = null,
            )
        }

        viewModelScope.launch {
            getTransferTechnicians(originTechnicianId)
                .onSuccess { technicians ->
                    _state.update {
                        it.copy(
                            transferTechnicians = technicians,
                            isLoadingTransferTechnicians = false,
                            transferError = null,
                        )
                    }
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            isLoadingTransferTechnicians = false,
                            transferError = error.message ?: "No se pudieron cargar los tecnicos",
                        )
                    }
                }
        }
    }

    fun onPauseClick() {
        if (ticketIdForStatus(TicketStatus.InProgress) == null) return

        _state.update {
            it.copy(
                isPauseDialogVisible = true,
                isLoadingPauseReasons = true,
                pauseReasons = emptyList(),
                selectedPauseReasonId = null,
                pauseError = null,
            )
        }

        viewModelScope.launch {
            getPauseReasons()
                .onSuccess { reasons ->
                    _state.update {
                        it.copy(
                            pauseReasons = reasons,
                            isLoadingPauseReasons = false,
                            pauseError = null,
                        )
                    }
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            isLoadingPauseReasons = false,
                            pauseError = error.message ?: "No se pudieron cargar los motivos de pausa",
                        )
                    }
                }
        }
    }

    fun onPauseReasonSelected(reasonId: Int) {
        _state.update {
            it.copy(
                selectedPauseReasonId = reasonId,
                pauseError = null,
            )
        }
    }

    fun onDismissPauseDialog() {
        if (state.value.isCreatingPause) return

        _state.update {
            it.copy(
                isPauseDialogVisible = false,
                selectedPauseReasonId = null,
                pauseError = null,
            )
        }
    }

    fun onCreatePauseClick() {
        val ticketId = ticketIdForStatus(TicketStatus.InProgress) ?: return
        val pauseReasonId = state.value.selectedPauseReasonId

        if (pauseInFlight) return

        val technicianId = technicianIdOrSetError { message ->
            _state.update { it.copy(pauseError = message) }
        } ?: return

        if (pauseReasonId == null) {
            _state.update { it.copy(pauseError = "Selecciona un motivo de pausa") }
            return
        }

        pauseInFlight = true
        viewModelScope.launch {
            _state.update { it.copy(isCreatingPause = true, pauseError = null) }

            pauseTicket(
                ticketId = ticketId,
                technicianId = technicianId,
                pauseReasonId = pauseReasonId,
            )
                .onSuccess {
                    _state.update {
                        it.copy(
                            isPauseDialogVisible = false,
                            selectedPauseReasonId = null,
                            pauseError = null,
                        )
                    }
                    refreshTicket(ticketId)
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            pauseError = error.message ?: "No se pudo solicitar la pausa",
                        )
                    }
                }

            pauseInFlight = false
            _state.update { it.copy(isCreatingPause = false) }
        }
    }

    fun onFinishPauseClick() {
        val ticketId = ticketIdForStatus(TicketStatus.Paused) ?: return
        if (finishPauseInFlight) return

        finishPauseInFlight = true
        viewModelScope.launch {
            _state.update { it.copy(isFinishingPause = true, finishPauseError = null) }

            finishPause(ticketId)
                .onSuccess {
                    refreshTicket(ticketId)
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            finishPauseError = error.message ?: "No se pudo finalizar la pausa",
                        )
                    }
                }

            finishPauseInFlight = false
            _state.update { it.copy(isFinishingPause = false) }
        }
    }

    fun onTransferTechnicianSelected(technicianId: Int) {
        _state.update {
            it.copy(
                selectedTransferTechnicianId = technicianId,
                transferError = null,
            )
        }
    }

    fun onTransferReasonChange(reason: String) {
        _state.update {
            it.copy(
                transferReason = reason.take(MAX_TRANSFER_REASON_LENGTH),
                transferError = null,
            )
        }
    }

    fun onDismissTransferDialog() {
        if (state.value.isCreatingTransfer) return

        _state.update {
            it.copy(
                isTransferDialogVisible = false,
                selectedTransferTechnicianId = null,
                transferReason = "",
                transferError = null,
            )
        }
    }

    fun onCreateTransferClick() {
        val ticketId = ticketIdForStatus(TicketStatus.InProgress) ?: return
        val destinationTechnicianId = state.value.selectedTransferTechnicianId
        val reason = state.value.transferReason.trim()

        if (transferInFlight) return

        val originTechnicianId = technicianIdOrSetError { message ->
            _state.update { it.copy(transferError = message) }
        } ?: return

        when {
            destinationTechnicianId == null -> {
                _state.update { it.copy(transferError = "Selecciona un tecnico destino") }
                return
            }

            reason.isBlank() -> {
                _state.update { it.copy(transferError = "Ingresa el motivo del traspaso") }
                return
            }
        }

        transferInFlight = true
        viewModelScope.launch {
            _state.update { it.copy(isCreatingTransfer = true, transferError = null) }

            transferTicket(
                ticketId = ticketId,
                originTechnicianId = originTechnicianId,
                destinationTechnicianId = destinationTechnicianId,
                reason = reason,
            )
                .onSuccess {
                    _state.update {
                        it.copy(
                            isTransferDialogVisible = false,
                            selectedTransferTechnicianId = null,
                            transferReason = "",
                            transferError = null,
                        )
                    }
                    refreshTicket(ticketId)
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            transferError = error.message ?: "No se pudo solicitar el traspaso",
                        )
                    }
                }

            transferInFlight = false
            _state.update { it.copy(isCreatingTransfer = false) }
        }
    }

    fun onCreateMilestoneClick() {
        val ticketId = ticketIdForStatus(TicketStatus.InProgress) ?: return
        val observation = state.value.milestoneObservation.trim()

        if (milestoneInFlight) return

        if (observation.isBlank()) {
            _state.update { it.copy(milestoneError = "Ingresa una observacion") }
            return
        }

        val technicianId = technicianIdOrSetError { message ->
            _state.update { it.copy(milestoneError = message) }
        } ?: return

        milestoneInFlight = true
        viewModelScope.launch {
            _state.update { it.copy(isCreatingMilestone = true, milestoneError = null) }

            createTicketMilestone(
                ticketId = ticketId,
                technicianId = technicianId,
                observation = observation,
            )
                .onSuccess {
                    _state.update {
                        it.copy(
                            isMilestoneDialogVisible = false,
                            milestoneObservation = "",
                            milestoneError = null,
                        )
                    }
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            milestoneError = error.message ?: "No se pudo crear el hito",
                        )
                    }
                }

            milestoneInFlight = false
            _state.update { it.copy(isCreatingMilestone = false) }
        }
    }

    private companion object {
        const val MAX_MILESTONE_OBSERVATION_LENGTH = 500
        const val MAX_TRANSFER_REASON_LENGTH = 100
        const val NO_TECHNICIAN_ID_ERROR = "El tecnico no tiene ID asociado"
    }
}
