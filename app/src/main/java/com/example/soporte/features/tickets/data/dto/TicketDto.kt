package com.example.soporte.features.tickets.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TicketDto(
    val id: Int? = null,
    @SerialName("nro_ticket")
    val nroTicket: String? = null,
    @SerialName("id_solicitante")
    val idSolicitante: Int? = null,
    @SerialName("id_departamento_soporte")
    val idDepartamentoSoporte: Int? = null,
    @SerialName("id_tecnico_asignado")
    val idTecnicoAsignado: Int? = null,
    @SerialName("id_tipo_ticket")
    val idTipoTicket: Int? = null,
    @SerialName("id_estado_ticket")
    val idEstadoTicket: Int? = null,
    @SerialName("id_servicio")
    val idServicio: Int? = null,
    @SerialName("id_nivel_prioridad")
    val idNivelPrioridad: Int? = null,
    @SerialName("id_catalogo_falla")
    val idCatalogoFalla: Int? = null,
    @SerialName("detalle_falla_reportada")
    val detalleFallaReportada: String? = null,
    val critico: Boolean? = null,
    @SerialName("ubicacion_obs")
    val ubicacionObs: String? = null,
    @SerialName("created_at")
    val createdAt: String? = null,
    @SerialName("updated_at")
    val updatedAt: String? = null,
    @SerialName("fecha_inicio_trabajo")
    val fechaInicioTrabajo: String? = null,
    @SerialName("fecha_fin_trabajo")
    val fechaFinTrabajo: String? = null,
    val servicio: ServicioDto? = null,
    val solicitante: SolicitanteDto? = null,
    @SerialName("tecnico_asignado")
    val tecnicoAsignado: TecnicoDto? = null,
    @SerialName("tipo_ticket")
    val tipoTicket: TipoTicketDto? = null,
    @SerialName("estado_ticket")
    val estadoTicket: EstadoTicketDto? = null,
    @SerialName("nivel_prioridad")
    val nivelPrioridad: NivelPrioridadDto? = null,
    @SerialName("catalogo_falla")
    val catalogoFalla: CatalogoFallaDto? = null,
    @SerialName("departamento_soporte")
    val departamentoSoporte: DepartamentoSoporteDto? = null,
)

@Serializable
data class ServicioDto(
    val id: Int? = null,
    @SerialName("id_nivel_prioridad_default")
    val idNivelPrioridadDefault: Int? = null,
    val edificio: String? = null,
    val piso: Int? = null,
    val ubicacion: String? = null,
    val servicios: String? = null,
    val unidades: String? = null,
)

@Serializable
data class SolicitanteDto(
    val id: Int? = null,
    @SerialName("id_servicio")
    val idServicio: Int? = null,
    val correo: String? = null,
    val rut: String? = null,
    val dv: String? = null,
    @SerialName("nombre_completo")
    val nombreCompleto: String? = null,
    val anexo: Int? = null,
    val estado: Boolean? = null,
)

@Serializable
data class TecnicoDto(
    val id: Int? = null,
    val rut: String? = null,
    val dv: String? = null,
    @SerialName("nombre_completo")
    val nombreCompleto: String? = null,
    @SerialName("id_tipo_tecnico")
    val idTipoTecnico: Int? = null,
    val activo: Boolean? = null,
    @SerialName("id_departamento")
    val idDepartamento: Int? = null,
    @SerialName("tickets_asignados_count")
    val ticketsAsignadosCount: Int? = null,
    @SerialName("tipo_tecnico")
    val tipoTecnico: TipoTecnicoDto? = null,
    @SerialName("departamento_soporte")
    val departamentoSoporte: DepartamentoSoporteDto? = null,
)

@Serializable
data class TipoTecnicoDto(
    val id: Int? = null,
    val descripcion: String? = null,
)

@Serializable
data class TipoTicketDto(
    val id: Int? = null,
    @SerialName("cod_tipo_ticket")
    val codTipoTicket: String? = null,
    val descripcion: String? = null,
)

@Serializable
data class EstadoTicketDto(
    val id: Int? = null,
    val descripcion: String? = null,
    @SerialName("cod_estado_ticket")
    val codEstadoTicket: String? = null,
)

@Serializable
data class NivelPrioridadDto(
    val id: Int? = null,
    val descripcion: String? = null,
)

@Serializable
data class CatalogoFallaDto(
    val id: Int? = null,
    @SerialName("id_departamento")
    val idDepartamento: Int? = null,
    @SerialName("descripcion_falla")
    val descripcionFalla: String? = null,
    val categoria: String? = null,
    val subcategoria: String? = null,
    val complejidad: Int? = null,
    @SerialName("tiempo_resolucion_estimado")
    val tiempoResolucionEstimado: String? = null,
    @SerialName("requiere_visita_fisica")
    val requiereVisitaFisica: Boolean? = null,
    @SerialName("departamento_soporte")
    val departamentoSoporte: DepartamentoSoporteDto? = null,
)

@Serializable
data class DepartamentoSoporteDto(
    val id: Int? = null,
    val descripcion: String? = null,
    @SerialName("cod_departamento")
    val codDepartamento: String? = null,
)
