package com.example.soporte.features.tickets.presentation.detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.soporte.features.tickets.domain.model.Ticket
import com.example.soporte.features.tickets.presentation.*
import com.example.soporte.features.tickets.presentation.detail.components.DetailInfoRow
import com.example.soporte.features.tickets.presentation.detail.components.DetailInfoSection
import com.example.soporte.features.tickets.presentation.detail.components.TicketSummaryCard
import org.koin.androidx.compose.koinViewModel

@Composable
fun TicketDetailScreen(
    ticketId: Int,
    onBackClick: () -> Unit,
    viewModel: TicketDetailViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(ticketId) {
        viewModel.loadTicket(ticketId)
    }

    TicketDetailContent(
        state = state,
        onStartTicketClick = viewModel::onStartTicketClick,
        onBackClick = onBackClick,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TicketDetailContent(
    state: TicketDetailState,
    onStartTicketClick: () -> Unit,
    onBackClick: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Detalle de Ticket",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                )
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            when {
                state.isLoading -> CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                )

                state.error != null -> Text(
                    text = state.error,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center),
                )

                state.ticket != null -> TicketDetailBody(
                    ticket = state.ticket,
                    isStartingTicket = state.isStartingTicket,
                    onStartTicketClick = onStartTicketClick,
                )
            }
        }
    }
}

@Composable
private fun TicketDetailBody(
    ticket: Ticket,
    isStartingTicket: Boolean,
    onStartTicketClick: () -> Unit,
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

        DetailInfoSection(title = "Acciones", icon = Icons.Default.Info) {
            TicketActions(
                statusCode = ticket.statusCode(),
                isStartingTicket = isStartingTicket,
                onStartTicketClick = onStartTicketClick,
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun TicketActions(
    statusCode: String,
    isStartingTicket: Boolean,
    onStartTicketClick: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        when (statusCode) {
            "ASI" -> Text(
                text = "El ticket debe estar visto por el tecnico para iniciar el trabajo.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            "VITEC" -> Button(
                onClick = onStartTicketClick,
                enabled = !isStartingTicket,
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(if (isStartingTicket) "INICIANDO..." else "INICIAR TRABAJO")
            }

            "PRO" -> {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    OutlinedButton(
                        onClick = {},
                        modifier = Modifier.weight(1f),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text("HITO")
                    }
                    OutlinedButton(
                        onClick = {},
                        modifier = Modifier.weight(1f),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text("VER HITOS")
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    OutlinedButton(
                        onClick = {},
                        modifier = Modifier.weight(1f),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text("TRASPASAR")
                    }
                    OutlinedButton(
                        onClick = {},
                        modifier = Modifier.weight(1f),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text("PAUSAR")
                    }
                }
                Button(
                    onClick = {},
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF81C784),
                        contentColor = Color.White
                    )
                ) {
                    Text("FINALIZAR TRABAJO")
                }
            }

            "PAU" -> Text(
                text = "El ticket se encuentra pausado.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            "TER", "CER" -> Text(
                text = "Este ticket ya ha sido finalizado.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            else -> Text(
                text = "No hay acciones disponibles para este estado.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun String?.dateOrUnavailable(): String =
    this?.toDisplayDateTime() ?: "No disponible"
