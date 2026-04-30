package com.example.soporte.features.tickets.presentation.list

import com.example.soporte.core.session.SessionManager
import com.example.soporte.features.tickets.FakeTicketsRepository
import com.example.soporte.features.tickets.TicketFixtures
import com.example.soporte.features.tickets.domain.model.TicketStatus
import com.example.soporte.features.tickets.domain.usecase.GetTechnicianByRutUseCase
import com.example.soporte.features.tickets.domain.usecase.GetTicketsByStatusUseCase
import com.example.soporte.features.tickets.domain.usecase.MarkTicketAsSeenUseCase
import com.example.soporte.testing.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TicketsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `carga tecnico dev y tickets asignados al iniciar`() = runTest(mainDispatcherRule.testDispatcher) {
        val repository = FakeTicketsRepository().apply {
            ticketsByStatus["ASI"] = Result.success(
                listOf(ticket(id = 23, number = "NA6OI8-26", status = "ASI")),
            )
        }
        val sessionManager = SessionManager()

        val viewModel = viewModel(repository, sessionManager)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals(listOf("1-9"), repository.requestedRut)
        assertEquals(listOf(2 to "ASI"), repository.ticketRequests)
        assertEquals(TicketStatus.Assigned, state.selectedStatus)
        assertEquals("NA6OI8-26", state.tickets.single().number)
        assertEquals("Tecnico Test", state.technician?.fullName)
        assertEquals("Tecnico Test", sessionManager.technician.value?.fullName)
        assertNull(state.error)
    }

    @Test
    fun `al cambiar chip recarga tickets por estado seleccionado`() = runTest(mainDispatcherRule.testDispatcher) {
        val repository = FakeTicketsRepository().apply {
            ticketsByStatus["ASI"] = Result.success(
                listOf(ticket(id = 23, number = "ASI-26", status = "ASI")),
            )
            ticketsByStatus["PRO"] = Result.success(
                listOf(ticket(id = 24, number = "PRO-26", status = "PRO")),
            )
        }
        val viewModel = viewModel(repository, SessionManager())
        advanceUntilIdle()

        viewModel.selectStatus(TicketStatus.InProgress)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals(TicketStatus.InProgress, state.selectedStatus)
        assertEquals("PRO-26", state.tickets.single().number)
        assertEquals(listOf(2 to "ASI", 2 to "PRO"), repository.ticketRequests)
        assertNull(state.error)
    }

    @Test
    fun `al seleccionar ticket ASI marca visto y navega`() = runTest(mainDispatcherRule.testDispatcher) {
        val repository = FakeTicketsRepository().apply {
            ticketsByStatus["ASI"] = Result.success(
                listOf(ticket(id = 23, number = "ASI-26", status = "ASI")),
            )
        }
        val viewModel = viewModel(repository, SessionManager())
        advanceUntilIdle()
        val navigations = mutableListOf<Int>()

        viewModel.onTicketSelected(viewModel.state.value.tickets.single()) { navigations += it }
        advanceUntilIdle()

        assertEquals(listOf(23 to 2), repository.markSeenRequests)
        assertEquals(listOf(2 to "ASI", 2 to "ASI"), repository.ticketRequests)
        assertEquals(listOf(23), navigations)
    }

    @Test
    fun `al seleccionar ticket ASI dos veces rapido no duplica marcado visto ni navegacion`() = runTest(mainDispatcherRule.testDispatcher) {
        val repository = FakeTicketsRepository().apply {
            ticketsByStatus["ASI"] = Result.success(
                listOf(ticket(id = 23, number = "ASI-26", status = "ASI")),
            )
        }
        val viewModel = viewModel(repository, SessionManager())
        advanceUntilIdle()
        val ticket = viewModel.state.value.tickets.single()
        val navigations = mutableListOf<Int>()

        viewModel.onTicketSelected(ticket) { navigations += it }
        viewModel.onTicketSelected(ticket) { navigations += it }
        advanceUntilIdle()

        assertEquals(listOf(23 to 2), repository.markSeenRequests)
        assertEquals(listOf(2 to "ASI", 2 to "ASI"), repository.ticketRequests)
        assertEquals(listOf(23), navigations)
    }

    @Test
    fun `al volver a seleccionar ticket ASI ya marcado navega sin reenviar visto`() = runTest(mainDispatcherRule.testDispatcher) {
        val repository = FakeTicketsRepository().apply {
            ticketsByStatus["ASI"] = Result.success(
                listOf(ticket(id = 23, number = "ASI-26", status = "ASI")),
            )
        }
        val viewModel = viewModel(repository, SessionManager())
        advanceUntilIdle()
        val ticket = viewModel.state.value.tickets.single()
        val navigations = mutableListOf<Int>()

        viewModel.onTicketSelected(ticket) { navigations += it }
        advanceUntilIdle()
        viewModel.onTicketSelected(ticket) { navigations += it }
        advanceUntilIdle()

        assertEquals(listOf(23 to 2), repository.markSeenRequests)
        assertEquals(listOf(2 to "ASI", 2 to "ASI"), repository.ticketRequests)
        assertEquals(listOf(23, 23), navigations)
    }

    @Test
    fun `al fallar marcado visto navega igual al detalle`() = runTest(mainDispatcherRule.testDispatcher) {
        val repository = FakeTicketsRepository().apply {
            markSeenResult = Result.failure(IllegalStateException("sin red"))
            ticketsByStatus["ASI"] = Result.success(
                listOf(ticket(id = 23, number = "ASI-26", status = "ASI")),
            )
        }
        val viewModel = viewModel(repository, SessionManager())
        advanceUntilIdle()
        val navigations = mutableListOf<Int>()

        viewModel.onTicketSelected(viewModel.state.value.tickets.single()) { navigations += it }
        advanceUntilIdle()

        assertEquals(listOf(23 to 2), repository.markSeenRequests)
        assertEquals(listOf(2 to "ASI", 2 to "ASI"), repository.ticketRequests)
        assertEquals(listOf(23), navigations)
    }

    @Test
    fun `al seleccionar tickets no ASI navega sin marcar visto`() = runTest(mainDispatcherRule.testDispatcher) {
        listOf("VITEC", "PRO", "PAU", "TER").forEachIndexed { index, status ->
            val ticketId = 24 + index
            val repository = FakeTicketsRepository().apply {
                ticketsByStatus["ASI"] = Result.success(
                    listOf(ticket(id = ticketId, number = "$status-26", status = status)),
                )
            }
            val viewModel = viewModel(repository, SessionManager())
            advanceUntilIdle()
            val navigations = mutableListOf<Int>()

            viewModel.onTicketSelected(viewModel.state.value.tickets.single()) { navigations += it }
            advanceUntilIdle()

            assertEquals(emptyList<Pair<Int, Int>>(), repository.markSeenRequests)
            assertEquals(listOf(ticketId), navigations)
        }
    }

    @Test
    fun `al seleccionar ticket sin id no navega ni marca visto`() = runTest(mainDispatcherRule.testDispatcher) {
        val repository = FakeTicketsRepository().apply {
            ticketsByStatus["ASI"] = Result.success(
                listOf(ticket(id = 23, number = "ASI-26", status = "ASI").copy(id = null)),
            )
        }
        val viewModel = viewModel(repository, SessionManager())
        advanceUntilIdle()
        val navigations = mutableListOf<Int>()

        viewModel.onTicketSelected(viewModel.state.value.tickets.single()) { navigations += it }
        advanceUntilIdle()

        assertEquals(emptyList<Pair<Int, Int>>(), repository.markSeenRequests)
        assertEquals(emptyList<Int>(), navigations)
    }

    private fun viewModel(
        repository: FakeTicketsRepository,
        sessionManager: SessionManager,
    ) = TicketsViewModel(
        repository = repository,
        getTechnicianByRut = GetTechnicianByRutUseCase(repository),
        getTicketsByStatus = GetTicketsByStatusUseCase(repository),
        markTicketAsSeen = MarkTicketAsSeenUseCase(repository),
        sessionManager = sessionManager,
    )

    private fun ticket(
        id: Int,
        number: String,
        status: String,
    ) = TicketFixtures.ticket(id = id, number = number, status = status)
}
