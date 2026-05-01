package com.example.soporte.features.tickets.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.soporte.core.session.SessionManager
import com.example.soporte.features.tickets.domain.model.Ticket
import com.example.soporte.features.tickets.domain.model.TicketStatus
import com.example.soporte.features.tickets.domain.usecase.GetTechnicianByRutUseCase
import com.example.soporte.features.tickets.domain.usecase.GetTicketsByStatusUseCase
import com.example.soporte.features.tickets.domain.usecase.MarkTicketAsSeenUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

import com.example.soporte.features.tickets.domain.repository.TicketsRepository
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class TicketsViewModel(
    private val repository: TicketsRepository,
    private val getTechnicianByRut: GetTechnicianByRutUseCase,
    private val getTicketsByStatus: GetTicketsByStatusUseCase,
    private val markTicketAsSeen: MarkTicketAsSeenUseCase,
    private val sessionManager: SessionManager,
) : ViewModel() {

    private val _state = MutableStateFlow(TicketsState())
    val state = _state.asStateFlow()
    private val seenMarkInFlight = mutableSetOf<Int>()
    private val seenMarkDone = mutableSetOf<Int>()

    init {
        observeRepositoryTickets()
        loadTechnicianAndTickets()
    }

    private fun observeRepositoryTickets() {
        repository.tickets
            .onEach { tickets ->
                _state.update { currentState ->
                    // Filter locally to ensure that if a ticket status changed,
                    // it only stays in the list if it still matches the filter.
                    val filteredTickets = tickets.filter { 
                        it.status.code == currentState.selectedStatus.code 
                    }
                    currentState.copy(tickets = filteredTickets)
                }
            }
            .launchIn(viewModelScope)
    }

    fun selectStatus(status: TicketStatus) {
        if (status == state.value.selectedStatus) return
        _state.update { it.copy(selectedStatus = status, tickets = emptyList(), error = null) }
        loadTickets()
    }

    fun refresh() {
        if (state.value.technician == null) {
            loadTechnicianAndTickets()
        } else {
            loadTickets()
        }
    }

    fun onTicketSelected(
        ticket: Ticket,
        onNavigate: (Int) -> Unit,
    ) {
        val ticketId = ticket.id ?: return
        val technicianId = state.value.technician?.id

        if (ticket.status.type != TicketStatus.Assigned || technicianId == null) {
            onNavigate(ticketId)
            return
        }

        if (ticketId in seenMarkDone) {
            onNavigate(ticketId)
            return
        }

        if (!seenMarkInFlight.add(ticketId)) return

        viewModelScope.launch {
            runCatching {
                markTicketAsSeen(
                    ticketId = ticketId,
                    technicianId = technicianId,
                )
            }
            seenMarkInFlight.remove(ticketId)
            seenMarkDone.add(ticketId)
            // Removed manual loadTickets() as the repository flow will update us
            onNavigate(ticketId)
        }
    }

    private fun loadTechnicianAndTickets() {
        sessionManager.technician.value?.let { technician ->
            _state.update {
                it.copy(
                    technician = technician,
                    isLoadingTechnician = false,
                    error = null,
                )
            }
            loadTickets()
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoadingTechnician = true, error = null) }

            getTechnicianByRut(DEV_TECHNICIAN_RUT)
                .onSuccess { technician ->
                    sessionManager.setTechnician(technician)
                    _state.update {
                        it.copy(
                            technician = technician,
                            isLoadingTechnician = false,
                        )
                    }
                    loadTickets()
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            isLoadingTechnician = false,
                            error = error.message ?: "No se pudo obtener el tecnico",
                        )
                    }
                }
        }
    }

    private fun loadTickets() {
        val technicianId = state.value.technician?.id
        if (technicianId == null) {
            _state.update { it.copy(error = "El tecnico no tiene ID asociado") }
            return
        }
        val status = state.value.selectedStatus

        viewModelScope.launch {
            _state.update { it.copy(isLoadingTickets = true, error = null) }

            getTicketsByStatus(
                technicianId = technicianId,
                statusCode = status.code,
            )
                .onSuccess {
                    _state.update {
                        it.copy(
                            isLoadingTickets = false,
                        )
                    }
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            isLoadingTickets = false,
                            error = error.message ?: "No se pudieron cargar los tickets",
                        )
                    }
                }
        }
    }

    private companion object {
        const val DEV_TECHNICIAN_RUT = "1-9"
    }
}
