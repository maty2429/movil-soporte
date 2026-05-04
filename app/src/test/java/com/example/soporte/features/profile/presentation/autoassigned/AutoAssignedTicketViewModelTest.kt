package com.example.soporte.features.profile.presentation.autoassigned

import com.example.soporte.core.session.SessionManager
import com.example.soporte.features.profile.domain.model.AutoAssignedFailureCatalog
import com.example.soporte.features.profile.domain.model.AutoAssignedPriorityLevel
import com.example.soporte.features.profile.domain.model.AutoAssignedRequester
import com.example.soporte.features.profile.domain.model.AutoAssignedService
import com.example.soporte.features.profile.domain.model.AutoAssignedTicketFormOptions
import com.example.soporte.features.profile.domain.model.AutoAssignedTicketType
import com.example.soporte.features.profile.domain.model.CreateAutoAssignedTicketInput
import com.example.soporte.features.profile.domain.repository.AutoAssignedTicketRepository
import com.example.soporte.features.profile.domain.usecase.CreateAutoAssignedTicketUseCase
import com.example.soporte.features.profile.domain.usecase.FindAutoAssignedRequesterUseCase
import com.example.soporte.features.profile.domain.usecase.GetAutoAssignedTicketFormOptionsUseCase
import com.example.soporte.features.tickets.domain.model.Technician
import com.example.soporte.testing.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AutoAssignedTicketViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `carga mantenedores al iniciar`() = runTest(mainDispatcherRule.testDispatcher) {
        val repository = FakeAutoAssignedTicketRepository()
        val viewModel = viewModel(repository)

        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals(1, repository.optionsRequests)
        assertEquals(listOf(10), state.services.map { it.id })
        assertEquals(listOf(20), state.ticketTypes.map { it.id })
        assertEquals(listOf(30, 31), state.priorityLevels.map { it.id })
        assertEquals(listOf(40), state.failureCatalogs.map { it.id })
        assertFalse(state.isLoadingOptions)
        assertNull(state.optionsError)
    }

    @Test
    fun `verifica solicitante por rut y guarda su id`() = runTest(mainDispatcherRule.testDispatcher) {
        val repository = FakeAutoAssignedTicketRepository()
        val viewModel = viewModel(repository)
        advanceUntilIdle()

        viewModel.onRequesterRutChange("19688310-K")
        viewModel.onVerifyRequesterClick()
        advanceUntilIdle()

        assertEquals(listOf("19688310-K"), repository.requesterRutRequests)
        assertEquals(5, viewModel.state.value.requester?.id)
        assertEquals("MATIAS GODOY", viewModel.state.value.requester?.fullName)
        assertEquals(10, viewModel.state.value.selectedServiceId)
        assertEquals(30, viewModel.state.value.selectedPriorityLevelId)
        assertNull(viewModel.state.value.requesterError)
    }

    @Test
    fun `si solicitante falla bloquea creacion y muestra error`() = runTest(mainDispatcherRule.testDispatcher) {
        val repository = FakeAutoAssignedTicketRepository().apply {
            requesterResult = Result.failure(IllegalStateException("solicitante no existe"))
        }
        val viewModel = viewModel(repository)
        advanceUntilIdle()

        viewModel.onRequesterRutChange("11111111-1")
        viewModel.onVerifyRequesterClick()
        advanceUntilIdle()
        viewModel.onCreateTicketClick()
        advanceUntilIdle()

        assertNull(viewModel.state.value.requester)
        assertEquals("solicitante no existe", viewModel.state.value.requesterError)
        assertEquals("Verifica el solicitante antes de crear el ticket", viewModel.state.value.submitError)
        assertEquals(emptyList<CreateAutoAssignedTicketInput>(), repository.createRequests)
    }

    @Test
    fun `seleccionar servicio auto selecciona prioridad default y permite cambiarla`() = runTest(mainDispatcherRule.testDispatcher) {
        val repository = FakeAutoAssignedTicketRepository()
        val viewModel = viewModel(repository)
        advanceUntilIdle()

        viewModel.onServiceSelected(10)
        assertEquals(30, viewModel.state.value.selectedPriorityLevelId)

        viewModel.onPriorityLevelSelected(31)
        assertEquals(31, viewModel.state.value.selectedPriorityLevelId)
    }

    @Test
    fun `crear ticket envia body correcto con departamento IT y tecnico global`() = runTest(mainDispatcherRule.testDispatcher) {
        val repository = FakeAutoAssignedTicketRepository()
        val viewModel = viewModel(repository, sessionManagerWithTechnician())
        advanceUntilIdle()

        viewModel.onRequesterRutChange("19688310-K")
        viewModel.onVerifyRequesterClick()
        advanceUntilIdle()
        viewModel.onServiceSelected(10)
        viewModel.onTicketTypeSelected(20)
        viewModel.onFailureCatalogSelected(40)
        viewModel.onCriticalChange(true)
        viewModel.onReportedFailureChange(" Equipo no enciende ")
        viewModel.onLocationObservationChange(" Oficina 3 ")
        viewModel.onCreateTicketClick()
        advanceUntilIdle()

        assertEquals(
            listOf(
                CreateAutoAssignedTicketInput(
                    requesterId = 5,
                    serviceId = 10,
                    ticketTypeId = 20,
                    priorityLevelId = 30,
                    departmentCode = "IT",
                    assignedTechnicianId = 2,
                    failureCatalogId = 40,
                    isCritical = true,
                    reportedFailure = "Equipo no enciende",
                    locationObservation = "Oficina 3",
                ),
            ),
            repository.createRequests,
        )
        assertEquals("Ticket autoasignado creado", viewModel.state.value.successMessage)
        assertEquals("", viewModel.state.value.reportedFailure)
        assertNull(viewModel.state.value.requester)
    }

    @Test
    fun `sin tecnico global muestra error y no llama api`() = runTest(mainDispatcherRule.testDispatcher) {
        val repository = FakeAutoAssignedTicketRepository()
        val viewModel = viewModel(repository, SessionManager())
        advanceUntilIdle()

        viewModel.onRequesterRutChange("19688310-K")
        viewModel.onVerifyRequesterClick()
        advanceUntilIdle()
        viewModel.onServiceSelected(10)
        viewModel.onTicketTypeSelected(20)
        viewModel.onFailureCatalogSelected(40)
        viewModel.onReportedFailureChange("Equipo no enciende")
        viewModel.onCreateTicketClick()
        advanceUntilIdle()

        assertEquals("El tecnico no tiene ID asociado", viewModel.state.value.submitError)
        assertTrue(repository.createRequests.isEmpty())
    }

    private fun viewModel(
        repository: FakeAutoAssignedTicketRepository,
        sessionManager: SessionManager = sessionManagerWithTechnician(),
    ) = AutoAssignedTicketViewModel(
        getFormOptions = GetAutoAssignedTicketFormOptionsUseCase(repository),
        findRequester = FindAutoAssignedRequesterUseCase(repository),
        createTicket = CreateAutoAssignedTicketUseCase(repository),
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

private class FakeAutoAssignedTicketRepository : AutoAssignedTicketRepository {
    var optionsRequests = 0
    val requesterRutRequests = mutableListOf<String>()
    val createRequests = mutableListOf<CreateAutoAssignedTicketInput>()

    var optionsResult: Result<AutoAssignedTicketFormOptions> = Result.success(
        AutoAssignedTicketFormOptions(
            services = listOf(
                AutoAssignedService(
                    id = 10,
                    defaultPriorityLevelId = 30,
                    building = "TORRE",
                    floor = 1,
                    serviceName = "INFORMATICA",
                    unitName = "DESARROLLO",
                ),
            ),
            ticketTypes = listOf(AutoAssignedTicketType(id = 20, description = "INCIDENTE")),
            priorityLevels = listOf(
                AutoAssignedPriorityLevel(id = 30, description = "CRITICA"),
                AutoAssignedPriorityLevel(id = 31, description = "MEDIA"),
            ),
            failureCatalogs = listOf(
                AutoAssignedFailureCatalog(
                    id = 40,
                    description = "Equipo no enciende",
                    category = "Hardware",
                    subcategory = "PC",
                ),
            ),
        ),
    )
    var requesterResult: Result<AutoAssignedRequester> = Result.success(
        AutoAssignedRequester(
            id = 5,
            serviceId = 10,
            rut = "19688310",
            dv = "K",
            fullName = "MATIAS GODOY",
        ),
    )
    var createResult: Result<Unit> = Result.success(Unit)

    override suspend fun getFormOptions(): Result<AutoAssignedTicketFormOptions> {
        optionsRequests += 1
        return optionsResult
    }

    override suspend fun findRequesterByRut(rut: String): Result<AutoAssignedRequester> {
        requesterRutRequests += rut
        return requesterResult
    }

    override suspend fun createAutoAssignedTicket(input: CreateAutoAssignedTicketInput): Result<Unit> {
        createRequests += input
        return createResult
    }
}
