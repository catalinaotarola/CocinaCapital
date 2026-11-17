package cl.DSY1105.cocinacapital.utils

import cl.DSY1105.cocinacapital.data.model.Promocion
import java.time.LocalDate

fun correoFechaGuardada(
    nombreUsuario: String,
    nombreFecha: String,
    ocasion: String,
    dia: Int,
    mes: Int,
    hoy: LocalDate = LocalDate.now()
): String {
    val avisos = Incentivos.calendario(dia, mes, hoy)
    val lineasAvisos = if (avisos.isEmpty()) {
        listOf("• Tu fecha es muy pronto: desde hoy verás sugerencias en Inicio.")
    } else {
        avisos.map { aviso ->
            val dias = if (aviso.diasAntes == 1) "1 día antes" else "${aviso.diasAntes} días antes"
            "• ${textoDiaMes(aviso.fecha.dayOfMonth, aviso.fecha.monthValue)} ($dias): hasta ${aviso.porcentaje}% de descuento"
        }
    }
    return (
        listOf(
            "Hola $nombreUsuario:",
            "",
            "Guardamos tu fecha especial \"$nombreFecha\" (${textoDiaMes(dia, mes)} · $ocasion).",
            "",
            "Te avisaremos con promociones para celebrar:"
        ) + lineasAvisos + listOf(
            "",
            "Mientras antes reserves, mayor es el beneficio.",
            "",
            "Equipo Cocina Capital"
        )
    ).joinToString("\n")
}

fun correoPromocionGuardada(nombreLocal: String, promo: Promocion): String =
    listOf(
        "Hola $nombreLocal:",
        "",
        "Tu promoción quedó guardada en Cocina Capital.",
        "",
        "• Promoción: ${promo.titulo}",
        "• Insignia: ${promo.etiqueta}",
        "• Días y horario: ${promo.condiciones}",
        "• Válida hasta: ${formatearFecha(promo.fechaTermino)}",
        "• Cupos: ${promo.cuposDisponibles} de ${promo.cuposTotales}",
        "• Ticket de prepago: ${formatearPesos(promo.precioTicket)}",
        "• Ideal para: ${promo.ocasion.etiqueta}",
        "• Horario de baja demanda: ${if (promo.bajaDemanda) "sí" else "no"}",
        "",
        "Equipo Cocina Capital"
    ).joinToString("\n")
