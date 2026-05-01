package com.example.soporte.features.tickets.presentation.detail.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.soporte.features.tickets.domain.model.Ticket
import com.example.soporte.features.tickets.presentation.detail.TransferResponseAction
import com.example.soporte.features.tickets.presentation.buildingText
import com.example.soporte.features.tickets.presentation.failureCatalogText
import com.example.soporte.features.tickets.presentation.floorText
import com.example.soporte.features.tickets.presentation.locationObservationText
import com.example.soporte.features.tickets.presentation.locationText
import com.example.soporte.features.tickets.presentation.requesterText
import com.example.soporte.features.tickets.presentation.serviceText
import com.example.soporte.features.tickets.presentation.statusCode
import com.example.soporte.features.tickets.presentation.unitText

@Composable
fun TicketDetailBody(
    ticket: Ticket,
    isStartingTicket: Boolean,
    isFinishingPause: Boolean,
    finishPauseError: String?,
    onStartTicketClick: () -> Unit,
    onMilestoneClick: () -> Unit,
    onMilestonesClick: () -> Unit,
    onTransferClick: () -> Unit,
    onPauseClick: () -> Unit,
    onFinishPauseClick: () -> Unit,
    canRespondToTransfer: Boolean,
    isRespondingTransfer: Boolean,
    respondingTransferAction: TransferResponseAction?,
    transferResponseError: String?,
    onAcceptTransferClick: () -> Unit,
    onRejectTransferClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        TicketSummaryCard(ticket = ticket)

        DetailInfoSection(title = "Detalles Técnicos", icon = Icons.Default.Settings) {
            DetailInfoRow(label = "Catálogo de Falla", value = ticket.failureCatalogText())
            DetailInfoRow(label = "Falla Reportada", value = ticket.reportedFailure ?: "No informado")
        }

        DetailInfoSection(title = "Ubicación y Servicio", icon = Icons.Default.LocationOn) {
            DetailInfoRow(label = "Ubicación", value = ticket.locationText())
            DetailInfoRow(label = "Edificio / Piso", value = "${ticket.buildingText()} - ${ticket.floorText()}")
            DetailInfoRow(label = "Servicio / Unidad", value = "${ticket.serviceText()} - ${ticket.unitText()}")
            if (ticket.locationObservationText() != "No informado") {
                DetailInfoRow(label = "Observación de Ubicación", value = ticket.locationObservationText())
            }
        }

        DetailInfoSection(title = "Solicitante", icon = Icons.Default.Person) {
            DetailInfoRow(label = "Nombre", value = ticket.requesterText())
            DetailInfoRow(label = "Anexo", value = ticket.requester.extension?.toString() ?: "No informado")
        }

        DetailInfoSection(title = "Fechas e Historial", icon = Icons.Default.CalendarMonth) {
            DetailInfoRow(label = "Fecha de Creación", value = ticket.createdAt.dateOrUnavailable())
            DetailInfoRow(label = "Última Actualización", value = ticket.updatedAt.dateOrUnavailable())
            if (ticket.workStartedAt != null) {
                DetailInfoRow(label = "Inicio de Trabajo", value = ticket.workStartedAt.dateOrUnavailable())
            }
            if (ticket.workFinishedAt != null) {
                DetailInfoRow(label = "Fin de Trabajo", value = ticket.workFinishedAt.dateOrUnavailable())
            }
        }

        if (canRespondToTransfer) {
            DetailInfoSection(title = "Solicitud de traspaso", icon = Icons.Default.Info) {
                TransferResponseActions(
                    isResponding = isRespondingTransfer,
                    respondingAction = respondingTransferAction,
                    error = transferResponseError,
                    onAcceptClick = onAcceptTransferClick,
                    onRejectClick = onRejectTransferClick,
                )
            }
        }

        DetailInfoSection(title = "Acciones", icon = Icons.Default.Info) {
            TicketActions(
                statusCode = ticket.statusCode(),
                isStartingTicket = isStartingTicket,
                isFinishingPause = isFinishingPause,
                finishPauseError = finishPauseError,
                onStartTicketClick = onStartTicketClick,
                onMilestoneClick = onMilestoneClick,
                onMilestonesClick = onMilestonesClick,
                onTransferClick = onTransferClick,
                onPauseClick = onPauseClick,
                onFinishPauseClick = onFinishPauseClick,
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
