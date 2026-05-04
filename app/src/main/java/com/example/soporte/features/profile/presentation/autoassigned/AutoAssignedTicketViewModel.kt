package com.example.soporte.features.profile.presentation.autoassigned

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.soporte.core.session.SessionManager
import com.example.soporte.features.profile.domain.model.CreateAutoAssignedTicketInput
import com.example.soporte.features.profile.domain.usecase.CreateAutoAssignedTicketUseCase
import com.example.soporte.features.profile.domain.usecase.FindAutoAssignedRequesterUseCase
import com.example.soporte.features.profile.domain.usecase.GetAutoAssignedTicketFormOptionsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AutoAssignedTicketViewModel(
    private val getFormOptions: GetAutoAssignedTicketFormOptionsUseCase,
    private val findRequester: FindAutoAssignedRequesterUseCase,
    private val createTicket: CreateAutoAssignedTicketUseCase,
    private val sessionManager: SessionManager,
) : ViewModel() {

    private val _state = MutableStateFlow(AutoAssignedTicketState())
    val state = _state.asStateFlow()

    init {
        loadOptions()
    }

    fun retryOptions() {
        loadOptions()
    }

    fun onRequesterRutChange(rut: String) {
        _state.update {
            it.copy(
                requesterRut = rut,
                requester = null,
                requesterError = null,
                submitError = null,
                successMessage = null,
            )
        }
    }

    fun onVerifyRequesterClick() {
        val rut = state.value.requesterRut.trim()
        if (rut.isBlank()) {
            _state.update { it.copy(requesterError = "Ingresa el RUT del solicitante") }
            return
        }

        viewModelScope.launch {
            _state.update {
                it.copy(
                    requester = null,
                    isCheckingRequester = true,
                    requesterError = null,
                    submitError = null,
                    successMessage = null,
                )
            }

            findRequester(rut)
                .onSuccess { requester ->
                    val requesterServiceId = requester.serviceId
                        ?.takeIf { serviceId ->
                            state.value.services.any { it.id == serviceId }
                        }
                    val defaultPriorityId = requesterServiceId
                        ?.let(::defaultPriorityIdForService)

                    _state.update {
                        it.copy(
                            requester = requester,
                            selectedServiceId = requesterServiceId ?: it.selectedServiceId,
                            selectedPriorityLevelId = defaultPriorityId ?: it.selectedPriorityLevelId,
                            isCheckingRequester = false,
                        )
                    }
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            isCheckingRequester = false,
                            requesterError = error.message ?: "Solicitante no encontrado",
                        )
                    }
                }
        }
    }

    fun onServiceSelected(serviceId: Int) {
        val defaultPriorityId = defaultPriorityIdForService(serviceId)

        _state.update {
            it.copy(
                selectedServiceId = serviceId,
                selectedPriorityLevelId = defaultPriorityId ?: it.selectedPriorityLevelId,
                submitError = null,
                successMessage = null,
            )
        }
    }

    private fun defaultPriorityIdForService(serviceId: Int): Int? =
        state.value.services
            .firstOrNull { it.id == serviceId }
            ?.defaultPriorityLevelId
            ?.takeIf { priorityId ->
                state.value.priorityLevels.any { it.id == priorityId }
            }

    fun onTicketTypeSelected(ticketTypeId: Int) {
        _state.update {
            it.copy(
                selectedTicketTypeId = ticketTypeId,
                submitError = null,
                successMessage = null,
            )
        }
    }

    fun onPriorityLevelSelected(priorityLevelId: Int) {
        _state.update {
            it.copy(
                selectedPriorityLevelId = priorityLevelId,
                submitError = null,
                successMessage = null,
            )
        }
    }

    fun onFailureCatalogSelected(failureCatalogId: Int) {
        _state.update {
            it.copy(
                selectedFailureCatalogId = failureCatalogId,
                submitError = null,
                successMessage = null,
            )
        }
    }

    fun onCriticalChange(isCritical: Boolean) {
        _state.update {
            it.copy(
                isCritical = isCritical,
                submitError = null,
                successMessage = null,
            )
        }
    }

    fun onReportedFailureChange(reportedFailure: String) {
        _state.update {
            it.copy(
                reportedFailure = reportedFailure,
                submitError = null,
                successMessage = null,
            )
        }
    }

    fun onLocationObservationChange(locationObservation: String) {
        _state.update {
            it.copy(
                locationObservation = locationObservation,
                submitError = null,
                successMessage = null,
            )
        }
    }

    fun onCreateTicketClick() {
        val currentState = state.value
        val requesterId = currentState.requester?.id
        val serviceId = currentState.selectedServiceId
        val ticketTypeId = currentState.selectedTicketTypeId
        val priorityLevelId = currentState.selectedPriorityLevelId
        val failureCatalogId = currentState.selectedFailureCatalogId
        val reportedFailure = currentState.reportedFailure.trim()
        val technicianId = sessionManager.technician.value?.id

        val validationError = when {
            requesterId == null -> "Verifica el solicitante antes de crear el ticket"
            serviceId == null -> "Selecciona un servicio"
            ticketTypeId == null -> "Selecciona un tipo de ticket"
            priorityLevelId == null -> "Selecciona una prioridad"
            failureCatalogId == null -> "Selecciona una falla"
            technicianId == null -> "El tecnico no tiene ID asociado"
            reportedFailure.isBlank() -> "Ingresa el detalle de la falla"
            else -> null
        }

        if (validationError != null) {
            _state.update { it.copy(submitError = validationError, successMessage = null) }
            return
        }

        viewModelScope.launch {
            _state.update {
                it.copy(
                    isCreatingTicket = true,
                    submitError = null,
                    successMessage = null,
                )
            }

            createTicket(
                CreateAutoAssignedTicketInput(
                    requesterId = requesterId!!,
                    serviceId = serviceId!!,
                    ticketTypeId = ticketTypeId!!,
                    priorityLevelId = priorityLevelId!!,
                    assignedTechnicianId = technicianId!!,
                    failureCatalogId = failureCatalogId!!,
                    isCritical = currentState.isCritical,
                    reportedFailure = reportedFailure,
                    locationObservation = currentState.locationObservation.trim(),
                ),
            )
                .onSuccess {
                    _state.update {
                        it.copy(
                            requesterRut = "",
                            requester = null,
                            selectedServiceId = null,
                            selectedTicketTypeId = null,
                            selectedPriorityLevelId = null,
                            selectedFailureCatalogId = null,
                            isCritical = false,
                            reportedFailure = "",
                            locationObservation = "",
                            isCreatingTicket = false,
                            submitError = null,
                            successMessage = "Ticket autoasignado creado",
                        )
                    }
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            isCreatingTicket = false,
                            submitError = error.message ?: "No se pudo crear el ticket",
                        )
                    }
                }
        }
    }

    private fun loadOptions() {
        viewModelScope.launch {
            _state.update { it.copy(isLoadingOptions = true, optionsError = null) }

            getFormOptions()
                .onSuccess { options ->
                    _state.update {
                        it.copy(
                            isLoadingOptions = false,
                            optionsError = null,
                            services = options.services,
                            ticketTypes = options.ticketTypes,
                            priorityLevels = options.priorityLevels,
                            failureCatalogs = options.failureCatalogs,
                        )
                    }
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            isLoadingOptions = false,
                            optionsError = error.message ?: "No se pudieron cargar los mantenedores",
                        )
                    }
                }
        }
    }
}
