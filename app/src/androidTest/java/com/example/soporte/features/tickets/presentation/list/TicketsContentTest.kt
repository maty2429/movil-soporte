package com.example.soporte.features.tickets.presentation.list

import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.example.soporte.core.designsystem.theme.SoporteTheme
import com.example.soporte.features.tickets.domain.model.FailureCatalog
import com.example.soporte.features.tickets.domain.model.Technician
import com.example.soporte.features.tickets.domain.model.Ticket
import com.example.soporte.features.tickets.domain.model.TicketRequester
import com.example.soporte.features.tickets.domain.model.TicketService
import com.example.soporte.features.tickets.domain.model.TicketStatusInfo
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class TicketsContentTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun alPresionarTicketEnviaIdParaAbrirDetalle() {
        var clickedTicketId: Int? = null

        composeRule.setContent {
            SoporteTheme(dynamicColor = false) {
                TicketsContent(
                    state = TicketsState(
                        technician = Technician(
                            id = 2,
                            rut = "1",
                            dv = "9",
                            fullName = "Tecnico Test",
                            typeDescription = "Tecnico de campo",
                            supportDepartmentDescription = "Informatica",
                        ),
                        tickets = listOf(ticket()),
                    ),
                    modifier = Modifier,
                    onStatusClick = {},
                    onRetryClick = {},
                    onTicketClick = { clickedTicketId = it.id },
                )
            }
        }

        composeRule.onNodeWithTag("ticket-card-23").performClick()

        composeRule.runOnIdle {
            assertEquals(23, clickedTicketId)
        }
    }

    private fun ticket() = Ticket(
        id = 23,
        number = "NA6OI8-26",
        reportedFailure = "No funciona teclado",
        isCritical = true,
        locationObservation = null,
        createdAt = null,
        updatedAt = null,
        workStartedAt = null,
        workFinishedAt = null,
        service = TicketService(
            building = "TORRE",
            floor = 1,
            location = "SUBIENDO LAS ESCALERAS",
            serviceName = "INFORMATICA",
            unitName = "DESARROLLO",
        ),
        requester = TicketRequester(
            fullName = "MATIAS GODOY",
            extension = 1234,
        ),
        status = TicketStatusInfo(code = "ASI", description = "ASIGNADO"),
        priority = "ALTA",
        failureCatalog = FailureCatalog(
            description = "No funciona",
            category = null,
            subcategory = null,
            complexity = null,
            requiresPhysicalVisit = null,
        ),
    )
}
