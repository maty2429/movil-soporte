package com.example.soporte.features.transfers.data.mapper

import com.example.soporte.features.transfers.data.dto.TransferSummaryDto
import com.example.soporte.features.transfers.domain.model.TransferSummary
import com.example.soporte.features.transfers.domain.model.TransferSummaryType

fun TransferSummaryDto.toDomain(): TransferSummary =
    TransferSummary(
        id = id ?: 0,
        ticketId = ticketId ?: 0,
        ticketNumber = ticketNumber.orEmpty(),
        reason = motivo.orEmpty(),
        requestedAt = requestedAt,
        originTechnicianName = originTechnicianName.orEmpty(),
        destinationTechnicianName = destinationTechnicianName.orEmpty(),
        statusCode = statusCode.orEmpty(),
        statusDescription = statusDescription.orEmpty(),
        type = when (tipo?.uppercase()) {
            "ENVIADA" -> TransferSummaryType.Sent
            else -> TransferSummaryType.Received
        },
    )
