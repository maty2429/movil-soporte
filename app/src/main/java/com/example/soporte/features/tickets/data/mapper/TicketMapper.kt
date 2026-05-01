package com.example.soporte.features.tickets.data.mapper

import com.example.soporte.features.tickets.data.dto.TecnicoDto
import com.example.soporte.features.tickets.data.dto.HitoTicketDto
import com.example.soporte.features.tickets.data.dto.MotivoPausaDto
import com.example.soporte.features.tickets.data.dto.TicketDto
import com.example.soporte.features.tickets.domain.model.FailureCatalog
import com.example.soporte.features.tickets.domain.model.PauseReason
import com.example.soporte.features.tickets.domain.model.Technician
import com.example.soporte.features.tickets.domain.model.Ticket
import com.example.soporte.features.tickets.domain.model.TicketMilestone
import com.example.soporte.features.tickets.domain.model.TicketRequester
import com.example.soporte.features.tickets.domain.model.TicketService
import com.example.soporte.features.tickets.domain.model.TicketStatusInfo

fun TecnicoDto.toDomain(): Technician =
    Technician(
        id = id,
        rut = rut,
        dv = dv,
        fullName = nombreCompleto,
        typeDescription = tipoTecnico?.descripcion,
        supportDepartmentDescription = departamentoSoporte?.descripcion,
        isActive = activo,
    )

fun HitoTicketDto.toDomain(): TicketMilestone =
    TicketMilestone(
        id = id,
        ticketId = idTicket,
        technicianId = idTecnico,
        milestoneTypeId = idTipoHito,
        date = fechaHito,
        observation = hitoObs,
        technicianName = tecnico?.nombreCompleto,
        milestoneTypeCode = tipoHito?.codTipoHito,
        milestoneTypeDescription = tipoHito?.descripcion,
    )

fun MotivoPausaDto.toDomain(): PauseReason =
    PauseReason(
        id = id ?: 0,
        reason = motivoPausa.orEmpty(),
        requiresAuthorization = requiereAutorizacion ?: true,
    )

fun TicketDto.toDomain(): Ticket =
    Ticket(
        id = id,
        number = nroTicket,
        reportedFailure = detalleFallaReportada,
        isCritical = critico == true,
        locationObservation = ubicacionObs,
        createdAt = createdAt,
        updatedAt = updatedAt,
        workStartedAt = fechaInicioTrabajo,
        workFinishedAt = fechaFinTrabajo,
        service = TicketService(
            building = servicio?.edificio,
            floor = servicio?.piso,
            location = servicio?.ubicacion,
            serviceName = servicio?.servicios,
            unitName = servicio?.unidades,
        ),
        requester = TicketRequester(
            fullName = solicitante?.nombreCompleto,
            extension = solicitante?.anexo,
        ),
        status = TicketStatusInfo(
            code = estadoTicket?.codEstadoTicket ?: "SIN",
            description = estadoTicket?.descripcion,
        ),
        priority = nivelPrioridad?.descripcion,
        failureCatalog = FailureCatalog(
            description = catalogoFalla?.descripcionFalla,
            category = catalogoFalla?.categoria,
            subcategory = catalogoFalla?.subcategoria,
            complexity = catalogoFalla?.complejidad,
            requiresPhysicalVisit = catalogoFalla?.requiereVisitaFisica,
        ),
    )
