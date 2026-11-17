package cl.DSY1105.cocinacapital.data.remote.dto

import cl.DSY1105.cocinacapital.data.model.Recomendacion
import cl.DSY1105.cocinacapital.data.model.Resena

data class ResenaDto(
    val id: Int?,
    val localId: Int?,
    val autor: String?,
    val puntaje: Int?,
    val comentario: String?,
    val fecha: String?,
    val esMia: Boolean?
)

data class NuevaResenaDto(
    val localId: Int,
    val autor: String,
    val puntaje: Int,
    val comentario: String
)

data class CambioResenaDto(
    val puntaje: Int,
    val comentario: String
)

data class RecomendacionDto(
    val localId: Int?,
    val nombre: String?,
    val categoria: String?,
    val promedio: Double?,
    val cantidadResenas: Int?,
    val motivo: String?
)

data class ErrorDto(
    val error: String?
)

fun ResenaDto.aModelo(): Resena? {
    if (id == null || localId == null || puntaje == null) return null
    return Resena(
        id = id,
        localId = localId,
        autor = autor ?: "Anónimo",
        puntaje = puntaje.coerceIn(1, 5),
        comentario = comentario.orEmpty(),
        fecha = fecha.orEmpty(),
        esMia = esMia ?: false
    )
}

fun RecomendacionDto.aModelo(): Recomendacion? {
    if (localId == null || nombre.isNullOrBlank()) return null
    return Recomendacion(
        localId = localId,
        nombre = nombre,
        categoria = categoria.orEmpty(),
        promedio = promedio ?: 0.0,
        cantidadResenas = cantidadResenas ?: 0,
        motivo = motivo.orEmpty()
    )
}
