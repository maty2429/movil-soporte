package com.example.soporte.features.profile.presentation.autoassigned

import com.example.soporte.features.profile.domain.model.AutoAssignedFailureCatalog
import com.example.soporte.features.profile.domain.model.AutoAssignedPriorityLevel
import com.example.soporte.features.profile.domain.model.AutoAssignedRequester
import com.example.soporte.features.profile.domain.model.AutoAssignedService
import com.example.soporte.features.profile.domain.model.AutoAssignedTicketType

data class AutoAssignedTicketState(
    val isLoadingOptions: Boolean = false,
    val optionsError: String? = null,
    val services: List<AutoAssignedService> = emptyList(),
    val ticketTypes: List<AutoAssignedTicketType> = emptyList(),
    val priorityLevels: List<AutoAssignedPriorityLevel> = emptyList(),
    val failureCatalogs: List<AutoAssignedFailureCatalog> = emptyList(),

    val requesterRut: String = "",
    val requester: AutoAssignedRequester? = null,
    val isCheckingRequester: Boolean = false,
    val requesterError: String? = null,

    val selectedServiceId: Int? = null,
    val selectedTicketTypeId: Int? = null,
    val selectedPriorityLevelId: Int? = null,
    val selectedFailureCatalogId: Int? = null,
    val isCritical: Boolean = false,
    val reportedFailure: String = "",
    val locationObservation: String = "",

    val isCreatingTicket: Boolean = false,
    val submitError: String? = null,
    val successMessage: String? = null,
) {
    val canSubmit: Boolean
        get() = requester != null &&
            selectedServiceId != null &&
            selectedTicketTypeId != null &&
            selectedPriorityLevelId != null &&
            selectedFailureCatalogId != null &&
            reportedFailure.isNotBlank() &&
            !isLoadingOptions &&
            !isCheckingRequester &&
            !isCreatingTicket
}
