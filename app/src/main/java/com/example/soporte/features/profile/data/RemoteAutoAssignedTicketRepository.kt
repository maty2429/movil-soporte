package com.example.soporte.features.profile.data

import com.example.soporte.core.network.SoporteApiClient
import com.example.soporte.features.profile.data.mapper.toAutoAssignedFailureCatalog
import com.example.soporte.features.profile.data.mapper.toAutoAssignedPriorityLevel
import com.example.soporte.features.profile.data.mapper.toAutoAssignedRequester
import com.example.soporte.features.profile.data.mapper.toAutoAssignedService
import com.example.soporte.features.profile.data.mapper.toAutoAssignedTicketType
import com.example.soporte.features.profile.domain.model.AutoAssignedTicketFormOptions
import com.example.soporte.features.profile.domain.model.CreateAutoAssignedTicketInput
import com.example.soporte.features.profile.domain.repository.AutoAssignedTicketRepository

class RemoteAutoAssignedTicketRepository(
    private val apiClient: SoporteApiClient,
) : AutoAssignedTicketRepository {

    override suspend fun getFormOptions() =
        runCatching {
            AutoAssignedTicketFormOptions(
                services = apiClient.getServices()
                    .map { it.toAutoAssignedService() }
                    .filter { it.id > 0 && it.label.isNotBlank() },
                ticketTypes = apiClient.getTicketTypes()
                    .map { it.toAutoAssignedTicketType() }
                    .filter { it.id > 0 && it.description.isNotBlank() },
                priorityLevels = apiClient.getPriorityLevels()
                    .map { it.toAutoAssignedPriorityLevel() }
                    .filter { it.id > 0 && it.description.isNotBlank() },
                failureCatalogs = apiClient.getFailureCatalogs()
                    .map { it.toAutoAssignedFailureCatalog() }
                    .filter { it.id > 0 && it.description.isNotBlank() },
            )
        }

    override suspend fun findRequesterByRut(rut: String) =
        runCatching {
            apiClient.getSolicitanteByRut(rut)
                .toAutoAssignedRequester()
                .also { requester ->
                    require(requester.id > 0) { "Solicitante no encontrado" }
                }
        }

    override suspend fun createAutoAssignedTicket(input: CreateAutoAssignedTicketInput): Result<Unit> =
        runCatching {
            apiClient.createAutoAssignedTicket(
                requesterId = input.requesterId,
                serviceId = input.serviceId,
                ticketTypeId = input.ticketTypeId,
                priorityLevelId = input.priorityLevelId,
                departmentCode = input.departmentCode,
                assignedTechnicianId = input.assignedTechnicianId,
                failureCatalogId = input.failureCatalogId,
                isCritical = input.isCritical,
                reportedFailure = input.reportedFailure,
                locationObservation = input.locationObservation,
            )
        }

}
