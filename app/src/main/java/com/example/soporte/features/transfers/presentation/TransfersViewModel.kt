package com.example.soporte.features.transfers.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.soporte.core.session.SessionManager
import com.example.soporte.features.transfers.domain.usecase.GetReceivedTransfersUseCase
import com.example.soporte.features.transfers.domain.usecase.GetSentTransfersUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TransfersViewModel(
    private val getReceivedTransfers: GetReceivedTransfersUseCase,
    private val getSentTransfers: GetSentTransfersUseCase,
    private val sessionManager: SessionManager,
) : ViewModel() {

    private val _state = MutableStateFlow(TransfersState())
    val state = _state.asStateFlow()

    private var technicianId: Int? = null

    init {
        observeTechnician()
    }

    fun selectTab(tab: TransfersTab) {
        if (tab == state.value.selectedTab) return
        _state.update { it.copy(selectedTab = tab, transfers = emptyList(), error = null) }
        loadTransfers()
    }

    fun refresh() {
        loadTransfers()
    }

    private fun observeTechnician() {
        sessionManager.technician
            .onEach { technician ->
                val newTechnicianId = technician?.id
                _state.update {
                    it.copy(
                        technicianName = technician?.fullName,
                        transfers = if (newTechnicianId == null) emptyList() else it.transfers,
                        error = if (newTechnicianId == null) "No hay tecnico activo" else null,
                    )
                }
                if (newTechnicianId == null) {
                    technicianId = null
                    return@onEach
                }
                if (newTechnicianId != technicianId) {
                    technicianId = newTechnicianId
                    loadTransfers()
                }
            }
            .launchIn(viewModelScope)
    }

    private fun loadTransfers() {
        val currentTechnicianId = technicianId
        if (currentTechnicianId == null) {
            _state.update { it.copy(isLoading = false, transfers = emptyList(), error = "No hay tecnico activo") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            val result = when (state.value.selectedTab) {
                TransfersTab.Received -> getReceivedTransfers(currentTechnicianId)
                TransfersTab.Sent -> getSentTransfers(currentTechnicianId)
            }

            result
                .onSuccess { transfers ->
                    _state.update {
                        it.copy(
                            transfers = transfers.filter { transfer -> transfer.ticketId > 0 },
                            isLoading = false,
                            error = null,
                        )
                    }
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            transfers = emptyList(),
                            isLoading = false,
                            error = error.message ?: "No se pudieron cargar los traspasos",
                        )
                    }
                }
        }
    }
}
