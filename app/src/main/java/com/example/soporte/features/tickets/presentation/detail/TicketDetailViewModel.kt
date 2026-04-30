package com.example.soporte.features.tickets.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.soporte.core.session.SessionManager
import com.example.soporte.features.tickets.domain.model.TicketStatus
import com.example.soporte.features.tickets.domain.repository.TicketsRepository
import com.example.soporte.features.tickets.domain.usecase.GetTicketDetailUseCase
import com.example.soporte.features.tickets.domain.usecase.StartTicketUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TicketDetailViewModel(
    private val getTicketDetail: GetTicketDetailUseCase,
    private val startTicket: StartTicketUseCase,
    private val repository: TicketsRepository,
    private val sessionManager: SessionManager,
) : ViewModel() {

    private val _state = MutableStateFlow(TicketDetailState())
    val state = _state.asStateFlow()
    private var currentTicketId: Int? = null
    private var startInFlight = false

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

    fun loadTicket(ticketId: Int) {
        currentTicketId = ticketId
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

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
    }

    fun onStartTicketClick() {
        val ticket = state.value.ticket ?: return
        val ticketId = ticket.id ?: return

        if (ticket.status.type != TicketStatus.SeenByTechnician) return
        if (startInFlight) return

        val technicianId = sessionManager.technician.value?.id
        if (technicianId == null) {
            _state.update { it.copy(error = "El tecnico no tiene ID asociado") }
            return
        }

        startInFlight = true
        viewModelScope.launch {
            _state.update { it.copy(isStartingTicket = true, error = null) }

            startTicket(
                ticketId = ticketId,
                technicianId = technicianId,
            )
                .onSuccess {
                    loadTicket(ticketId)
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
}
