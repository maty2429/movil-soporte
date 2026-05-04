package com.example.soporte.core.network

import com.example.soporte.features.tickets.data.dto.TecnicoDto
import com.example.soporte.features.tickets.data.dto.CatalogoFallaDto
import com.example.soporte.features.tickets.data.dto.HitoTicketDto
import com.example.soporte.features.tickets.data.dto.MotivoPausaDto
import com.example.soporte.features.tickets.data.dto.NivelPrioridadDto
import com.example.soporte.features.tickets.data.dto.ServicioDto
import com.example.soporte.features.tickets.data.dto.SolicitanteDto
import com.example.soporte.features.tickets.data.dto.TicketDto
import com.example.soporte.features.tickets.data.dto.TipoTicketDto
import com.example.soporte.features.transfers.data.dto.TransferSummaryDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class SoporteApiClient(
    private val httpClient: HttpClient,
) {
    private val baseUrl = "http://192.168.1.4:9005/api-soporte-tickets"

    suspend fun getTecnicoByRut(rut: String): TecnicoDto =
        httpClient.get("$baseUrl/tecnicos/rut/$rut").body()

    suspend fun getAllTecnicos(): List<TecnicoDto> =
        httpClient.get("$baseUrl/tecnicos/").body()

    suspend fun getSolicitanteByRut(rut: String): SolicitanteDto =
        httpClient.get("$baseUrl/solicitantes/rut/$rut").body()

    suspend fun getServices(): List<ServicioDto> =
        httpClient.get("$baseUrl/mantenedores/servicios").body<List<ServicioDto>?>().orEmpty()

    suspend fun getTicketTypes(): List<TipoTicketDto> =
        httpClient.get("$baseUrl/mantenedores/tipo-ticket").body<List<TipoTicketDto>?>().orEmpty()

    suspend fun getPriorityLevels(): List<NivelPrioridadDto> =
        httpClient.get("$baseUrl/mantenedores/niveles-prioridad").body<List<NivelPrioridadDto>?>().orEmpty()

    suspend fun getFailureCatalogs(): List<CatalogoFallaDto> =
        httpClient.get("$baseUrl/mantenedores/catalogo-fallas").body<List<CatalogoFallaDto>?>().orEmpty()

    suspend fun getPauseReasons(): List<MotivoPausaDto> =
        httpClient.get("$baseUrl/mantenedores/motivos-pausa").body<List<MotivoPausaDto>?>().orEmpty()

    suspend fun getTicketsByTechnicianAndStatus(
        technicianId: Int,
        statusCode: String,
    ): List<TicketDto> =
        httpClient.get("$baseUrl/ticket/") {
            parameter("id_tecnico_asignado", technicianId)
            parameter("cod_estado_ticket", statusCode)
        }.body()

    suspend fun getTicketById(ticketId: Int): TicketDto =
        httpClient.get("$baseUrl/ticket/$ticketId").body()

    suspend fun getTicketMilestones(ticketId: Int): List<HitoTicketDto> =
        httpClient.get("$baseUrl/hito/ticket/$ticketId").body()

    suspend fun markTicketAsSeen(
        ticketId: Int,
        technicianId: Int,
    ) {
        httpClient.patch("$baseUrl/ticket-tecnico/visto/$ticketId") {
            contentType(ContentType.Application.Json)
            setBody(
                TicketSeenRequest(
                    technicianId = technicianId,
                    statusCode = "VITEC",
                ),
            )
        }
    }

    suspend fun startTicket(
        ticketId: Int,
        technicianId: Int,
    ) {
        httpClient.patch("$baseUrl/ticket-tecnico/iniciar/$ticketId") {
            contentType(ContentType.Application.Json)
            setBody(
                TicketActionRequest(
                    technicianId = technicianId,
                    statusCode = "PRO",
                ),
            )
        }
    }

    suspend fun createTicketMilestone(
        ticketId: Int,
        technicianId: Int,
        milestoneCode: String,
        observation: String,
    ) {
        httpClient.post("$baseUrl/hito/") {
            contentType(ContentType.Application.Json)
            setBody(
                TicketMilestoneRequest(
                    ticketId = ticketId,
                    technicianId = technicianId,
                    milestoneCode = milestoneCode,
                    observation = observation,
                ),
            )
        }
    }

    suspend fun createTicketTransfer(
        ticketId: Int,
        originTechnicianId: Int,
        destinationTechnicianId: Int,
        statusDescription: String,
        milestoneCode: String,
        reason: String,
    ) {
        httpClient.post("$baseUrl/ticket-tecnico/traspaso") {
            contentType(ContentType.Application.Json)
            setBody(
                TicketTransferRequest(
                    ticketId = ticketId,
                    destinationTechnicianId = destinationTechnicianId,
                    originTechnicianId = originTechnicianId,
                    statusDescription = statusDescription,
                    milestoneCode = milestoneCode,
                    reason = reason,
                ),
            )
        }
    }

    suspend fun respondTicketTransfer(
        transferId: Int,
        statusCode: String,
        destinationTechnicianId: Int,
    ) {
        httpClient.patch("$baseUrl/ticket-tecnico/respuesta/$transferId") {
            contentType(ContentType.Application.Json)
            setBody(
                TicketTransferResponseRequest(
                    statusCode = statusCode,
                    destinationTechnicianId = destinationTechnicianId,
                ),
            )
        }
    }

    suspend fun createTicketPause(
        ticketId: Int,
        technicianId: Int,
        pauseReasonId: Int,
    ) {
        httpClient.post("$baseUrl/pausas-ticket/") {
            contentType(ContentType.Application.Json)
            setBody(
                TicketPauseRequest(
                    ticketId = ticketId,
                    technicianId = technicianId,
                    pauseReasonId = pauseReasonId,
                ),
            )
        }
    }

    suspend fun finishTicketPause(ticketId: Int) {
        httpClient.patch("$baseUrl/pausas-ticket/finalizar/$ticketId")
    }

    suspend fun createAutoAssignedTicket(
        requesterId: Int,
        serviceId: Int,
        ticketTypeId: Int,
        priorityLevelId: Int,
        departmentCode: String,
        assignedTechnicianId: Int,
        failureCatalogId: Int,
        isCritical: Boolean,
        reportedFailure: String,
        locationObservation: String,
    ) {
        httpClient.post("$baseUrl/ticket/auto-asignado") {
            contentType(ContentType.Application.Json)
            setBody(
                AutoAssignedTicketRequest(
                    requesterId = requesterId,
                    serviceId = serviceId,
                    ticketTypeId = ticketTypeId,
                    priorityLevelId = priorityLevelId,
                    departmentCode = departmentCode,
                    assignedTechnicianId = assignedTechnicianId,
                    failureCatalogId = failureCatalogId,
                    isCritical = isCritical,
                    reportedFailure = reportedFailure,
                    locationObservation = locationObservation,
                ),
            )
        }
    }

    suspend fun getReceivedTransfers(technicianId: Int): List<TransferSummaryDto> =
        httpClient.get("$baseUrl/ticket-tecnico/traspasos") {
            parameter("cod_estado", PENDING_TRANSFER_STATUS)
            parameter("id_tecnico_destino", technicianId)
        }.body<List<TransferSummaryDto>?>().orEmpty()

    suspend fun getSentTransfers(technicianId: Int): List<TransferSummaryDto> =
        httpClient.get("$baseUrl/ticket-tecnico/traspasos") {
            parameter("cod_estado", PENDING_TRANSFER_STATUS)
            parameter("id_tecnico_origen", technicianId)
        }.body<List<TransferSummaryDto>?>().orEmpty()

    private companion object {
        const val PENDING_TRANSFER_STATUS = "SOL"
    }
}

@Serializable
private data class TicketSeenRequest(
    @SerialName("id_tecnico_asignado")
    val technicianId: Int,
    @SerialName("cod_estado_ticket")
    val statusCode: String,
)

@Serializable
private data class TicketActionRequest(
    @SerialName("id_tecnico_asignado")
    val technicianId: Int,
    @SerialName("cod_estado_ticket")
    val statusCode: String,
)

@Serializable
private data class TicketMilestoneRequest(
    @SerialName("id_ticket")
    val ticketId: Int,
    @SerialName("id_tecnico")
    val technicianId: Int,
    @SerialName("codigo_hito")
    val milestoneCode: String,
    @SerialName("hito_obs")
    val observation: String,
)

@Serializable
private data class TicketTransferRequest(
    @SerialName("id_ticket")
    val ticketId: Int,
    @SerialName("id_tecnico_destino")
    val destinationTechnicianId: Int,
    @SerialName("id_tecnico_origen")
    val originTechnicianId: Int,
    @SerialName("descripcion_estado")
    val statusDescription: String,
    @SerialName("cod_hito")
    val milestoneCode: String,
    @SerialName("motivo")
    val reason: String,
)

@Serializable
private data class TicketTransferResponseRequest(
    @SerialName("cod_estado")
    val statusCode: String,
    @SerialName("id_tecnico_destino")
    val destinationTechnicianId: Int,
)

@Serializable
private data class TicketPauseRequest(
    @SerialName("id_ticket")
    val ticketId: Int,
    @SerialName("id_tecnico_pausa")
    val technicianId: Int,
    @SerialName("id_motivo_pausa")
    val pauseReasonId: Int,
)

@Serializable
private data class AutoAssignedTicketRequest(
    @SerialName("id_solicitante")
    val requesterId: Int,
    @SerialName("id_servicio")
    val serviceId: Int,
    @SerialName("id_tipo_ticket")
    val ticketTypeId: Int,
    @SerialName("id_nivel_prioridad")
    val priorityLevelId: Int,
    @SerialName("cod_departamento")
    val departmentCode: String,
    @SerialName("id_tecnico_asignado")
    val assignedTechnicianId: Int,
    @SerialName("id_catalogo_falla")
    val failureCatalogId: Int,
    @SerialName("critico")
    val isCritical: Boolean,
    @SerialName("detalle_falla_reportada")
    val reportedFailure: String,
    @SerialName("ubicacion_obs")
    val locationObservation: String,
)
