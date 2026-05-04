package com.example.soporte.features.profile.domain.model

data class AutoAssignedRequester(
    val id: Int,
    val serviceId: Int?,
    val rut: String,
    val dv: String?,
    val fullName: String,
)

data class AutoAssignedService(
    val id: Int,
    val defaultPriorityLevelId: Int,
    val building: String,
    val floor: Int?,
    val serviceName: String,
    val unitName: String,
) {
    val label: String
        get() = listOfNotNull(
            building.takeIf { it.isNotBlank() },
            floor?.let { "Piso $it" },
            serviceName.takeIf { it.isNotBlank() },
            unitName.takeIf { it.isNotBlank() },
        ).joinToString(" - ")
}

data class AutoAssignedTicketType(
    val id: Int,
    val description: String,
)

data class AutoAssignedPriorityLevel(
    val id: Int,
    val description: String,
)

data class AutoAssignedFailureCatalog(
    val id: Int,
    val description: String,
    val category: String?,
    val subcategory: String?,
) {
    val label: String
        get() = listOfNotNull(
            description.takeIf { it.isNotBlank() },
            category?.takeIf { it.isNotBlank() },
            subcategory?.takeIf { it.isNotBlank() },
        ).joinToString(" - ")
}

data class AutoAssignedTicketFormOptions(
    val services: List<AutoAssignedService>,
    val ticketTypes: List<AutoAssignedTicketType>,
    val priorityLevels: List<AutoAssignedPriorityLevel>,
    val failureCatalogs: List<AutoAssignedFailureCatalog>,
)

data class CreateAutoAssignedTicketInput(
    val requesterId: Int,
    val serviceId: Int,
    val ticketTypeId: Int,
    val priorityLevelId: Int,
    val departmentCode: String = "IT",
    val assignedTechnicianId: Int,
    val failureCatalogId: Int,
    val isCritical: Boolean,
    val reportedFailure: String,
    val locationObservation: String,
)
