package com.example.soporte.features.tickets.presentation.detail.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AddLocation
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun TicketActions(
    statusCode: String,
    isStartingTicket: Boolean,
    isFinishingPause: Boolean,
    finishPauseError: String?,
    onStartTicketClick: () -> Unit,
    onMilestoneClick: () -> Unit,
    onMilestonesClick: () -> Unit,
    onTransferClick: () -> Unit,
    onPauseClick: () -> Unit,
    onFinishPauseClick: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        when (statusCode) {
            "ASI" -> {
                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        text = "El ticket debe estar visto por el técnico para iniciar el trabajo.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(16.dp),
                        textAlign = TextAlign.Center,
                    )
                }
            }

            "VITEC" -> SwipeToActionButton(
                text = "DESLIZA PARA INICIAR",
                onAction = onStartTicketClick,
                isLoading = isStartingTicket,
                modifier = Modifier.fillMaxWidth(),
            )

            "PRO" -> {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        ActionCard(
                            label = "HITO",
                            icon = Icons.Default.AddLocation,
                            onClick = onMilestoneClick,
                            modifier = Modifier.weight(1f),
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        )
                        ActionCard(
                            label = "HISTORIAL",
                            icon = Icons.Default.History,
                            onClick = onMilestonesClick,
                            modifier = Modifier.weight(1f),
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        ActionCard(
                            label = "TRASPASAR",
                            icon = Icons.Default.SwapHoriz,
                            onClick = onTransferClick,
                            modifier = Modifier.weight(1f),
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                            contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                        )
                        ActionCard(
                            label = "PAUSAR",
                            icon = Icons.Default.Pause,
                            onClick = onPauseClick,
                            modifier = Modifier.weight(1f),
                            containerColor = Color(0xFFFFECB3),
                            contentColor = Color(0xFF5D4037),
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                SwipeToActionButton(
                    text = "DESLIZA PARA FINALIZAR",
                    onAction = { /* TODO: Finalizar */ },
                    isLoading = false,
                    containerColor = Color(0xFF4CAF50),
                    contentColor = Color.White,
                    modifier = Modifier.fillMaxWidth(),
                    icon = Icons.AutoMirrored.Filled.ArrowForward,
                )
            }

            "PAU" -> {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Surface(
                        color = Color(0xFFFFECB3), // Fondo ámbar suave
                        shape = MaterialTheme.shapes.medium,
                        modifier = Modifier.fillMaxWidth(),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFD54F))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Pause,
                                    contentDescription = null,
                                    tint = Color(0xFF5D4037),
                                    modifier = Modifier.size(24.dp)
                                )
                                Column {
                                    Text(
                                        text = "Trabajo en Pausa",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF5D4037)
                                    )
                                    Text(
                                        text = "El tiempo de resolución está detenido.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFF5D4037).copy(alpha = 0.8f)
                                    )
                                }
                            }

                            finishPauseError?.let {
                                Text(
                                    text = it,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }

                    SwipeToActionButton(
                        text = "DESLIZA PARA REANUDAR",
                        onAction = onFinishPauseClick,
                        isLoading = isFinishingPause,
                        containerColor = Color(0xFFFFB300), // Ámbar vibrante
                        contentColor = Color.White,
                        modifier = Modifier.fillMaxWidth(),
                        icon = Icons.Default.PlayArrow
                    )
                }
            }

            "TER", "CER" -> Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = "Este ticket ya ha sido finalizado.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(16.dp),
                    textAlign = TextAlign.Center,
                )
            }

            else -> Text(
                text = "No hay acciones disponibles.",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
            )
        }
    }
}
