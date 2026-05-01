package com.example.soporte.features.transfers.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TransferSummaryDto(
    val id: Int? = null,
    @SerialName("id_ticket")
    val ticketId: Int? = null,
    @SerialName("nro_ticket")
    val ticketNumber: String? = null,
    val motivo: String? = null,
    @SerialName("fecha_traspaso")
    val requestedAt: String? = null,
    @SerialName("tecnico_origen_nombre")
    val originTechnicianName: String? = null,
    @SerialName("tecnico_destino_nombre")
    val destinationTechnicianName: String? = null,
    @SerialName("cod_estado")
    val statusCode: String? = null,
    @SerialName("estado")
    val statusDescription: String? = null,
    val tipo: String? = null,
)
