package com.example.soporte.features.tickets.data.mapper

import com.example.soporte.features.tickets.data.dto.TecnicoDto
import com.example.soporte.features.tickets.data.dto.TecnicoTurnoDto
import com.example.soporte.features.tickets.data.dto.TipoTurnoDto
import org.junit.Assert.assertEquals
import org.junit.Test

class TicketMapperTest {

    @Test
    fun `tecnico mapea nombre de tipo turno`() {
        val technician = TecnicoDto(
            id = 1,
            rut = "1",
            dv = "9",
            nombreCompleto = "colo colo",
            tecnicoTurno = TecnicoTurnoDto(
                tipoTurno = TipoTurnoDto(
                    nombre = "CUARTO TURNO",
                ),
            ),
        ).toDomain()

        assertEquals("CUARTO TURNO", technician.shiftName)
    }
}
