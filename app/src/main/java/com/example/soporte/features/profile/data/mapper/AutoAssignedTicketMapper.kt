package com.example.soporte.features.profile.data.mapper

import com.example.soporte.features.profile.domain.model.AutoAssignedFailureCatalog
import com.example.soporte.features.profile.domain.model.AutoAssignedPriorityLevel
import com.example.soporte.features.profile.domain.model.AutoAssignedRequester
import com.example.soporte.features.profile.domain.model.AutoAssignedService
import com.example.soporte.features.profile.domain.model.AutoAssignedTicketType
import com.example.soporte.features.tickets.data.dto.CatalogoFallaDto
import com.example.soporte.features.tickets.data.dto.NivelPrioridadDto
import com.example.soporte.features.tickets.data.dto.ServicioDto
import com.example.soporte.features.tickets.data.dto.SolicitanteDto
import com.example.soporte.features.tickets.data.dto.TipoTicketDto

fun SolicitanteDto.toAutoAssignedRequester(): AutoAssignedRequester =
    AutoAssignedRequester(
        id = id ?: 0,
        serviceId = idServicio,
        rut = rut.orEmpty(),
        dv = dv,
        fullName = nombreCompleto.orEmpty(),
    )

fun ServicioDto.toAutoAssignedService(): AutoAssignedService =
    AutoAssignedService(
        id = id ?: 0,
        defaultPriorityLevelId = idNivelPrioridadDefault ?: 0,
        building = edificio.orEmpty(),
        floor = piso,
        serviceName = servicios.orEmpty(),
        unitName = unidades.orEmpty(),
    )

fun TipoTicketDto.toAutoAssignedTicketType(): AutoAssignedTicketType =
    AutoAssignedTicketType(
        id = id ?: 0,
        description = descripcion.orEmpty(),
    )

fun NivelPrioridadDto.toAutoAssignedPriorityLevel(): AutoAssignedPriorityLevel =
    AutoAssignedPriorityLevel(
        id = id ?: 0,
        description = descripcion.orEmpty(),
    )

fun CatalogoFallaDto.toAutoAssignedFailureCatalog(): AutoAssignedFailureCatalog =
    AutoAssignedFailureCatalog(
        id = id ?: 0,
        description = descripcionFalla.orEmpty(),
        category = categoria,
        subcategory = subcategoria,
    )
