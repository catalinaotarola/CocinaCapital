package cl.DSY1105.cocinacapital.data.remote.dto

import cl.DSY1105.cocinacapital.data.model.Local
import cl.DSY1105.cocinacapital.data.model.Ocasion
import cl.DSY1105.cocinacapital.data.model.Promocion
import cl.DSY1105.cocinacapital.utils.diasElegidos
import cl.DSY1105.cocinacapital.utils.esFechaValida
import cl.DSY1105.cocinacapital.utils.textoCondiciones

data class CatalogoRespuesta(
    val locales: List<LocalDto>?,
    val promociones: List<PromocionDto>?
)

data class LocalDto(
    val id: Int?,
    val nombre: String?,
    val categoria: String?,
    val descripcion: String?,
    val direccion: String?,
    val latitud: Double?,
    val longitud: Double?,
    val horario: String?,
    val nivelPrecio: String?,
    val menuDestacado: List<String>?,
    val correo: String?
)

data class PromocionDto(
    val id: Int?,
    val localId: Int?,
    val titulo: String?,
    val etiqueta: String?,
    val dias: String?,
    val horaInicio: Int?,
    val horaFin: Int?,
    val fechaTermino: String?,
    val cupos: Int?,
    val precioTicket: Int?,
    val bajaDemanda: Boolean?,
    val ocasion: String?
)

fun LocalDto.aModelo(): Local? {
    if (id == null || nombre.isNullOrBlank() || latitud == null || longitud == null) return null
    return Local(
        id = id,
        nombre = nombre,
        categoria = categoria ?: "Otra",
        descripcion = descripcion.orEmpty(),
        direccion = direccion.orEmpty(),
        latitud = latitud,
        longitud = longitud,
        horario = horario.orEmpty(),
        nivelPrecio = nivelPrecio ?: "\$\$",
        menuDestacado = menuDestacado.orEmpty().joinToString("\n"),
        correo = correo.orEmpty()
    )
}

fun PromocionDto.aModelo(): Promocion? {
    if (id == null || localId == null || titulo.isNullOrBlank()) return null
    if (horaInicio == null || horaFin == null || horaInicio >= horaFin) return null
    if (fechaTermino == null || !esFechaValida(fechaTermino)) return null
    val diasValidos = diasElegidos(dias.orEmpty()).joinToString("")
    if (diasValidos.isEmpty()) return null
    val totales = cupos ?: 0
    return Promocion(
        id = id,
        localId = localId,
        titulo = titulo,
        etiqueta = etiqueta ?: "Promo",
        condiciones = textoCondiciones(diasValidos, horaInicio, horaFin),
        dias = diasValidos,
        horaInicio = horaInicio,
        horaFin = horaFin,
        fechaTermino = fechaTermino,
        cuposTotales = totales,
        cuposDisponibles = totales,
        precioTicket = precioTicket ?: 0,
        bajaDemanda = bajaDemanda ?: false,
        ocasion = Ocasion.entries.find { it.name == ocasion } ?: Ocasion.CUALQUIERA
    )
}
