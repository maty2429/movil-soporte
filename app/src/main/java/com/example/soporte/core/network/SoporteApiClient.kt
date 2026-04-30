package com.example.soporte.core.network

import com.example.soporte.features.tickets.data.dto.TecnicoDto
import com.example.soporte.features.tickets.data.dto.TicketDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.patch
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class SoporteApiClient(
    private val httpClient: HttpClient,
) {
    private val baseUrl = "http://10.6.22.9:9005/api-soporte-tickets"

    suspend fun getTecnicoByRut(rut: String): TecnicoDto =
        httpClient.get("$baseUrl/tecnicos/rut/$rut").body()

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
