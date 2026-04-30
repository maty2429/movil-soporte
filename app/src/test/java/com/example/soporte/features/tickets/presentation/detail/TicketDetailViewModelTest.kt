package com.example.soporte.features.tickets.presentation.detail

import com.example.soporte.core.session.SessionManager
import com.example.soporte.features.tickets.FakeTicketsRepository
import com.example.soporte.features.tickets.TicketFixtures
import com.example.soporte.features.tickets.domain.model.Technician
import com.example.soporte.features.tickets.domain.usecase.GetTicketDetailUseCase
import com.example.soporte.features.tickets.domain.usecase.StartTicketUseCase
import com.example.soporte.testing.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TicketDetailViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `carga detalle por id de ticket`() = runTest(mainDispatcherRule.testDispatcher) {
        val repository = FakeTicketsRepository().apply {
            detailResult = Result.success(
                TicketFixtures.ticket(id = 23, number = "NA6OI8-26", reportedFailure = "No funciona"),
            )
        }
        val viewModel = viewModel(repository)

        viewModel.loadTicket(23)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals(listOf(23), repository.detailRequests)
        assertEquals("NA6OI8-26", state.ticket?.number)
        assertEquals("No funciona", state.ticket?.reportedFailure)
        assertEquals(false, state.isLoading)
        assertNull(state.error)
    }

    @Test
    fun `expone error cuando falla la carga de detalle`() = runTest(mainDispatcherRule.testDispatcher) {
        val repository = FakeTicketsRepository().apply {
            detailResult = Result.failure(IllegalStateException("ticket no encontrado"))
        }
        val viewModel = viewModel(repository)

        viewModel.loadTicket(99)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals(listOf(99), repository.detailRequests)
        assertNull(state.ticket)
        assertEquals("ticket no encontrado", state.error)
        assertEquals(false, state.isLoading)
    }

    @Test
    fun `ticket VITEC inicia trabajo y refresca detalle`() = runTest(mainDispatcherRule.testDispatcher) {
        val repository = FakeTicketsRepository().apply {
            detailResult = Result.success(TicketFixtures.ticket(id = 23, number = "VITEC-26", status = "VITEC"))
        }
        val sessionManager = sessionManagerWithTechnician()
        val viewModel = viewModel(repository, sessionManager)

        viewModel.loadTicket(23)
        advanceUntilIdle()
        repository.detailResult = Result.success(TicketFixtures.ticket(id = 23, number = "VITEC-26", status = "PRO"))
        viewModel.onStartTicketClick()
        advanceUntilIdle()

        assertEquals(listOf(23 to 2), repository.startTicketRequests)
        assertEquals(listOf(23, 23), repository.detailRequests)
        assertEquals("PRO", viewModel.state.value.ticket?.status?.code)
        assertEquals(false, viewModel.state.value.isStartingTicket)
        assertNull(viewModel.state.value.error)
    }

    @Test
    fun `ticket no VITEC no inicia trabajo`() = runTest(mainDispatcherRule.testDispatcher) {
        listOf("ASI", "PRO", "PAU", "TER", "CER").forEach { status ->
            val repository = FakeTicketsRepository().apply {
                detailResult = Result.success(TicketFixtures.ticket(id = 23, number = "$status-26", status = status))
            }
            val viewModel = viewModel(repository, sessionManagerWithTechnician())

            viewModel.loadTicket(23)
            advanceUntilIdle()
            viewModel.onStartTicketClick()
            advanceUntilIdle()

            assertEquals(emptyList<Pair<Int, Int>>(), repository.startTicketRequests)
        }
    }

    @Test
    fun `doble tap al iniciar no duplica llamada`() = runTest(mainDispatcherRule.testDispatcher) {
        val repository = FakeTicketsRepository().apply {
            detailResult = Result.success(TicketFixtures.ticket(id = 23, number = "VITEC-26", status = "VITEC"))
        }
        val viewModel = viewModel(repository, sessionManagerWithTechnician())

        viewModel.loadTicket(23)
        advanceUntilIdle()
        viewModel.onStartTicketClick()
        viewModel.onStartTicketClick()
        advanceUntilIdle()

        assertEquals(listOf(23 to 2), repository.startTicketRequests)
    }

    @Test
    fun `sin tecnico en sesion expone error y no inicia`() = runTest(mainDispatcherRule.testDispatcher) {
        val repository = FakeTicketsRepository().apply {
            detailResult = Result.success(TicketFixtures.ticket(id = 23, number = "VITEC-26", status = "VITEC"))
        }
        val viewModel = viewModel(repository, SessionManager())

        viewModel.loadTicket(23)
        advanceUntilIdle()
        viewModel.onStartTicketClick()
        advanceUntilIdle()

        assertEquals(emptyList<Pair<Int, Int>>(), repository.startTicketRequests)
        assertEquals("El tecnico no tiene ID asociado", viewModel.state.value.error)
    }

    @Test
    fun `si falla iniciar trabajo expone error y no refresca detalle`() = runTest(mainDispatcherRule.testDispatcher) {
        val repository = FakeTicketsRepository().apply {
            startTicketResult = Result.failure(IllegalStateException("no se pudo iniciar"))
            detailResult = Result.success(TicketFixtures.ticket(id = 23, number = "VITEC-26", status = "VITEC"))
        }
        val viewModel = viewModel(repository, sessionManagerWithTechnician())

        viewModel.loadTicket(23)
        advanceUntilIdle()
        viewModel.onStartTicketClick()
        advanceUntilIdle()

        assertEquals(listOf(23 to 2), repository.startTicketRequests)
        assertEquals(listOf(23), repository.detailRequests)
        assertEquals("no se pudo iniciar", viewModel.state.value.error)
        assertEquals(false, viewModel.state.value.isStartingTicket)
    }

    @Test
    fun `ticket sin id no inicia trabajo`() = runTest(mainDispatcherRule.testDispatcher) {
        val repository = FakeTicketsRepository().apply {
            detailResult = Result.success(TicketFixtures.ticket(id = 23, number = "VITEC-26", status = "VITEC").copy(id = null))
        }
        val viewModel = viewModel(repository, sessionManagerWithTechnician())

        viewModel.loadTicket(23)
        advanceUntilIdle()
        viewModel.onStartTicketClick()
        advanceUntilIdle()

        assertEquals(emptyList<Pair<Int, Int>>(), repository.startTicketRequests)
    }

    private fun viewModel(
        repository: FakeTicketsRepository,
        sessionManager: SessionManager = sessionManagerWithTechnician(),
    ) = TicketDetailViewModel(
        getTicketDetail = GetTicketDetailUseCase(repository),
        startTicket = StartTicketUseCase(repository),
        repository = repository,
        sessionManager = sessionManager,
    )

    private fun sessionManagerWithTechnician() =
        SessionManager().apply {
            setTechnician(
                Technician(
                    id = 2,
                    rut = "1",
                    dv = "9",
                    fullName = "Tecnico Test",
                    typeDescription = "Tecnico de campo",
                    supportDepartmentDescription = "Informatica",
                ),
            )
        }
}
