package com.example.soporte.features.tickets.presentation.detail

import com.example.soporte.core.session.SessionManager
import com.example.soporte.features.tickets.FakeTicketsRepository
import com.example.soporte.features.tickets.TicketFixtures
import com.example.soporte.features.tickets.domain.model.Technician
import com.example.soporte.features.tickets.domain.model.TicketMilestone
import com.example.soporte.features.tickets.domain.usecase.CreateTicketMilestoneUseCase
import com.example.soporte.features.tickets.domain.usecase.FinishPauseUseCase
import com.example.soporte.features.tickets.domain.usecase.GetPauseReasonsUseCase
import com.example.soporte.features.tickets.domain.usecase.GetTicketDetailUseCase
import com.example.soporte.features.tickets.domain.usecase.GetTicketMilestonesUseCase
import com.example.soporte.features.tickets.domain.usecase.GetTransferTechniciansUseCase
import com.example.soporte.features.tickets.domain.usecase.RespondTransferUseCase
import com.example.soporte.features.tickets.domain.usecase.PauseTicketUseCase
import com.example.soporte.features.tickets.domain.usecase.StartTicketUseCase
import com.example.soporte.features.tickets.domain.usecase.TransferTicketUseCase
import com.example.soporte.testing.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
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

    @Test
    fun `ver hitos carga hitos del ticket y los muestra en estado`() = runTest(mainDispatcherRule.testDispatcher) {
        val milestones = listOf(
            TicketMilestone(
                id = 7,
                ticketId = 23,
                technicianId = 2,
                milestoneTypeId = 4,
                date = "2026-04-30T12:30:00Z",
                observation = "Revision inicial",
                technicianName = "Tecnico Test",
                milestoneTypeCode = "TEC",
                milestoneTypeDescription = "Comentario tecnico",
            ),
        )
        val repository = FakeTicketsRepository().apply {
            detailResult = Result.success(TicketFixtures.ticket(id = 23, number = "PRO-26", status = "PRO"))
            milestonesResult = Result.success(milestones)
        }
        val viewModel = viewModel(repository, sessionManagerWithTechnician())

        viewModel.loadTicket(23)
        advanceUntilIdle()
        viewModel.onMilestonesClick()
        advanceUntilIdle()

        assertEquals(listOf(23), repository.milestoneRequests)
        assertEquals(true, viewModel.state.value.isMilestonesDialogVisible)
        assertEquals(milestones, viewModel.state.value.milestones)
        assertEquals(false, viewModel.state.value.isLoadingMilestones)
        assertNull(viewModel.state.value.milestonesError)
    }

    @Test
    fun `si falla ver hitos mantiene modal abierto con error`() = runTest(mainDispatcherRule.testDispatcher) {
        val repository = FakeTicketsRepository().apply {
            detailResult = Result.success(TicketFixtures.ticket(id = 23, number = "PRO-26", status = "PRO"))
            milestonesResult = Result.failure(IllegalStateException("no se pudieron cargar"))
        }
        val viewModel = viewModel(repository, sessionManagerWithTechnician())

        viewModel.loadTicket(23)
        advanceUntilIdle()
        viewModel.onMilestonesClick()
        advanceUntilIdle()

        assertEquals(true, viewModel.state.value.isMilestonesDialogVisible)
        assertEquals(false, viewModel.state.value.isLoadingMilestones)
        assertEquals("no se pudieron cargar", viewModel.state.value.milestonesError)
    }

    @Test
    fun `ticket PRO crea hito tecnico con observacion y tecnico en sesion`() = runTest(mainDispatcherRule.testDispatcher) {
        val repository = FakeTicketsRepository().apply {
            detailResult = Result.success(TicketFixtures.ticket(id = 23, number = "PRO-26", status = "PRO"))
        }
        val viewModel = viewModel(repository, sessionManagerWithTechnician())

        viewModel.loadTicket(23)
        advanceUntilIdle()
        viewModel.onMilestoneClick()
        viewModel.onMilestoneObservationChange(" Se revisa equipo en terreno ")
        viewModel.onCreateMilestoneClick()
        advanceUntilIdle()

        assertEquals(
            listOf(
                FakeTicketsRepository.CreateMilestoneRequest(
                    ticketId = 23,
                    technicianId = 2,
                    milestoneCode = "TEC",
                    observation = "Se revisa equipo en terreno",
                ),
            ),
            repository.createMilestoneRequests,
        )
        assertEquals(false, viewModel.state.value.isMilestoneDialogVisible)
        assertEquals("", viewModel.state.value.milestoneObservation)
        assertNull(viewModel.state.value.milestoneError)
    }

    @Test
    fun `hito sin observacion no llama repositorio y muestra error`() = runTest(mainDispatcherRule.testDispatcher) {
        val repository = FakeTicketsRepository().apply {
            detailResult = Result.success(TicketFixtures.ticket(id = 23, number = "PRO-26", status = "PRO"))
        }
        val viewModel = viewModel(repository, sessionManagerWithTechnician())

        viewModel.loadTicket(23)
        advanceUntilIdle()
        viewModel.onMilestoneClick()
        viewModel.onCreateMilestoneClick()
        advanceUntilIdle()

        assertEquals(emptyList<FakeTicketsRepository.CreateMilestoneRequest>(), repository.createMilestoneRequests)
        assertEquals("Ingresa una observacion", viewModel.state.value.milestoneError)
    }

    @Test
    fun `si falla crear hito mantiene modal abierto con error`() = runTest(mainDispatcherRule.testDispatcher) {
        val repository = FakeTicketsRepository().apply {
            detailResult = Result.success(TicketFixtures.ticket(id = 23, number = "PRO-26", status = "PRO"))
            createMilestoneResult = Result.failure(IllegalStateException("no se pudo crear"))
        }
        val viewModel = viewModel(repository, sessionManagerWithTechnician())

        viewModel.loadTicket(23)
        advanceUntilIdle()
        viewModel.onMilestoneClick()
        viewModel.onMilestoneObservationChange("Revision inicial")
        viewModel.onCreateMilestoneClick()
        advanceUntilIdle()

        assertEquals(true, viewModel.state.value.isMilestoneDialogVisible)
        assertEquals("no se pudo crear", viewModel.state.value.milestoneError)
        assertEquals(false, viewModel.state.value.isCreatingMilestone)
    }

    @Test
    fun `abrir traspaso carga tecnicos destino sin incluir tecnico actual`() = runTest(mainDispatcherRule.testDispatcher) {
        val repository = FakeTicketsRepository().apply {
            detailResult = Result.success(TicketFixtures.ticket(id = 23, number = "PRO-26", status = "PRO"))
        }
        val viewModel = viewModel(repository, sessionManagerWithTechnician())

        viewModel.loadTicket(23)
        advanceUntilIdle()
        viewModel.onTransferClick()
        advanceUntilIdle()

        assertEquals(1, repository.allTechniciansRequests)
        assertEquals(true, viewModel.state.value.isTransferDialogVisible)
        assertEquals(listOf(3), viewModel.state.value.transferTechnicians.map { it.id })
        assertEquals(false, viewModel.state.value.isLoadingTransferTechnicians)
        assertNull(viewModel.state.value.transferError)
    }

    @Test
    fun `traspaso envia ticket origen destino motivo estado SOL e hito COM`() = runTest(mainDispatcherRule.testDispatcher) {
        val repository = FakeTicketsRepository().apply {
            detailResult = Result.success(TicketFixtures.ticket(id = 23, number = "PRO-26", status = "PRO"))
        }
        val viewModel = viewModel(repository, sessionManagerWithTechnician())

        viewModel.loadTicket(23)
        advanceUntilIdle()
        viewModel.onTransferClick()
        advanceUntilIdle()
        viewModel.onTransferTechnicianSelected(3)
        viewModel.onTransferReasonChange(" Necesita apoyo de redes ")
        viewModel.onCreateTransferClick()
        advanceUntilIdle()

        assertEquals(
            listOf(
                FakeTicketsRepository.CreateTransferRequest(
                    ticketId = 23,
                    originTechnicianId = 2,
                    destinationTechnicianId = 3,
                    statusDescription = "SOL",
                    milestoneCode = "COM",
                    reason = "Necesita apoyo de redes",
                ),
            ),
            repository.createTransferRequests,
        )
        assertEquals(false, viewModel.state.value.isTransferDialogVisible)
        assertEquals("", viewModel.state.value.transferReason)
        assertNull(viewModel.state.value.transferError)
    }

    @Test
    fun `traspaso sin destino ni motivo muestra error y no llama repositorio`() = runTest(mainDispatcherRule.testDispatcher) {
        val repository = FakeTicketsRepository().apply {
            detailResult = Result.success(TicketFixtures.ticket(id = 23, number = "PRO-26", status = "PRO"))
        }
        val viewModel = viewModel(repository, sessionManagerWithTechnician())

        viewModel.loadTicket(23)
        advanceUntilIdle()
        viewModel.onTransferClick()
        advanceUntilIdle()
        viewModel.onCreateTransferClick()
        advanceUntilIdle()

        assertEquals(emptyList<FakeTicketsRepository.CreateTransferRequest>(), repository.createTransferRequests)
        assertEquals("Selecciona un tecnico destino", viewModel.state.value.transferError)
    }

    @Test
    fun `aceptar traspaso recibido envia ACE y navega al exito`() = runTest(mainDispatcherRule.testDispatcher) {
        val repository = FakeTicketsRepository().apply {
            detailResult = Result.success(TicketFixtures.ticket(id = 23, number = "SOTR-26", status = "SOTR"))
        }
        val viewModel = viewModel(repository, sessionManagerWithTechnician())
        var navigated = false

        viewModel.loadTicket(ticketId = 23, transferId = 9, isReceivedTransfer = true)
        advanceUntilIdle()
        viewModel.onAcceptTransferClick { navigated = true }
        advanceUntilIdle()

        assertEquals(
            listOf(FakeTicketsRepository.RespondTransferRequest(9, "ACE", 2)),
            repository.respondTransferRequests,
        )
        assertTrue(navigated)
        assertNull(viewModel.state.value.transferResponseError)
    }

    @Test
    fun `rechazar traspaso recibido envia REC y navega al exito`() = runTest(mainDispatcherRule.testDispatcher) {
        val repository = FakeTicketsRepository().apply {
            detailResult = Result.success(TicketFixtures.ticket(id = 23, number = "SOTR-26", status = "SOTR"))
        }
        val viewModel = viewModel(repository, sessionManagerWithTechnician())
        var navigated = false

        viewModel.loadTicket(ticketId = 23, transferId = 9, isReceivedTransfer = true)
        advanceUntilIdle()
        viewModel.onRejectTransferClick { navigated = true }
        advanceUntilIdle()

        assertEquals(
            listOf(FakeTicketsRepository.RespondTransferRequest(9, "REC", 2)),
            repository.respondTransferRequests,
        )
        assertTrue(navigated)
        assertNull(viewModel.state.value.transferResponseError)
    }

    @Test
    fun `detalle sin traspaso recibido no responde solicitud`() = runTest(mainDispatcherRule.testDispatcher) {
        val repository = FakeTicketsRepository().apply {
            detailResult = Result.success(TicketFixtures.ticket(id = 23, number = "SOTR-26", status = "SOTR"))
        }
        val viewModel = viewModel(repository, sessionManagerWithTechnician())

        viewModel.loadTicket(ticketId = 23, transferId = 9, isReceivedTransfer = false)
        advanceUntilIdle()
        viewModel.onAcceptTransferClick {}
        advanceUntilIdle()

        assertEquals(emptyList<FakeTicketsRepository.RespondTransferRequest>(), repository.respondTransferRequests)
    }

    @Test
    fun `pausar ticket carga motivos y crea pausa con tecnico global`() = runTest(mainDispatcherRule.testDispatcher) {
        val repository = FakeTicketsRepository().apply {
            detailResult = Result.success(TicketFixtures.ticket(id = 23, number = "PRO-26", status = "PRO"))
        }
        val viewModel = viewModel(repository, sessionManagerWithTechnician())

        viewModel.loadTicket(23)
        advanceUntilIdle()
        viewModel.onPauseClick()
        advanceUntilIdle()
        viewModel.onPauseReasonSelected(4)
        viewModel.onCreatePauseClick()
        advanceUntilIdle()

        assertEquals(
            listOf(FakeTicketsRepository.CreatePauseRequest(23, 2, 4)),
            repository.createPauseRequests,
        )
        assertEquals(false, viewModel.state.value.isPauseDialogVisible)
        assertNull(viewModel.state.value.pauseError)
    }

    @Test
    fun `finalizar pausa envia id ticket y recarga detalle`() = runTest(mainDispatcherRule.testDispatcher) {
        val repository = FakeTicketsRepository().apply {
            detailResult = Result.success(TicketFixtures.ticket(id = 23, number = "PAU-26", status = "PAU"))
        }
        val viewModel = viewModel(repository, sessionManagerWithTechnician())

        viewModel.loadTicket(23)
        advanceUntilIdle()
        viewModel.onFinishPauseClick()
        advanceUntilIdle()

        assertEquals(listOf(23), repository.finishPauseRequests)
        assertEquals(listOf(23, 23), repository.detailRequests)
        assertEquals(false, viewModel.state.value.isFinishingPause)
        assertNull(viewModel.state.value.finishPauseError)
    }

    private fun viewModel(
        repository: FakeTicketsRepository,
        sessionManager: SessionManager = sessionManagerWithTechnician(),
    ) = TicketDetailViewModel(
        getTicketDetail = GetTicketDetailUseCase(repository),
        getTicketMilestones = GetTicketMilestonesUseCase(repository),
        getTransferTechnicians = GetTransferTechniciansUseCase(repository),
        getPauseReasons = GetPauseReasonsUseCase(repository),
        startTicket = StartTicketUseCase(repository),
        createTicketMilestone = CreateTicketMilestoneUseCase(repository),
        transferTicket = TransferTicketUseCase(repository),
        respondTransfer = RespondTransferUseCase(repository),
        pauseTicket = PauseTicketUseCase(repository),
        finishPause = FinishPauseUseCase(repository),
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
