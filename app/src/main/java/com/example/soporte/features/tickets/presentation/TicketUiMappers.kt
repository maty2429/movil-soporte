package com.example.soporte.features.tickets.presentation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Warning
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.soporte.features.tickets.domain.model.TicketStatus

fun TicketStatus.toColor(): Color = when (this) {
    TicketStatus.Assigned -> Color(0xFF64B5F6) // Light Blue
    TicketStatus.SeenByTechnician -> Color(0xFF9575CD) // Purple
    TicketStatus.InProgress -> Color(0xFF4FC3F7) // Sky Blue
    TicketStatus.Paused -> Color(0xFFFFB74D) // Orange
    TicketStatus.WorkFinished -> Color(0xFF81C784) // Green
    TicketStatus.Closed -> Color(0xFF90A4AE) // Blue Grey
    TicketStatus.Unknown -> Color(0xFFE0E0E0) // Grey
}

fun TicketStatus.toIcon(): ImageVector = when (this) {
    TicketStatus.Assigned -> Icons.Default.Email
    TicketStatus.SeenByTechnician -> Icons.Default.Notifications
    TicketStatus.InProgress -> Icons.Default.PlayArrow
    TicketStatus.Paused -> Icons.Default.Pause
    TicketStatus.WorkFinished -> Icons.Default.CheckCircle
    TicketStatus.Closed -> Icons.Default.Info
    TicketStatus.Unknown -> Icons.Default.Warning
}

fun String?.toPriorityColor(): Color = when (this?.uppercase()) {
    "ALTA" -> Color(0xFFE57373) // Red
    "MEDIA" -> Color(0xFFFFB74D) // Orange
    "BAJA" -> Color(0xFF81C784) // Green
    else -> Color(0xFF90A4AE) // Grey
}
