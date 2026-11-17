package cl.DSY1105.cocinacapital.utils

import java.time.LocalDate

data class AvisoProgramado(val fecha: LocalDate, val diasAntes: Int, val porcentaje: Int)

object Incentivos {
    val DIAS_DE_AVISO = listOf(15, 8, 1)

    fun porcentaje(diasFaltantes: Long): Int = when (diasFaltantes) {
        in 9L..15L -> 50
        in 2L..8L -> 35
        1L -> 20
        else -> 0
    }

    fun esDiaDeAviso(diasFaltantes: Long): Boolean = diasFaltantes.toInt() in DIAS_DE_AVISO

    fun etapa(diasFaltantes: Long): String = when (diasFaltantes) {
        15L -> "Te informamos"
        8L -> "Te recordamos"
        1L -> "Última oportunidad"
        else -> "Se acerca tu fecha"
    }

    fun calendario(dia: Int, mes: Int, hoy: LocalDate = LocalDate.now()): List<AvisoProgramado> {
        val faltan = diasHasta(dia, mes, hoy)
        val evento = hoy.plusDays(faltan)
        return DIAS_DE_AVISO
            .filter { it <= faltan }
            .map { AvisoProgramado(evento.minusDays(it.toLong()), it, porcentaje(it.toLong())) }
    }

    fun maxAvisosPorDia(accionesUltimaSemana: Int): Int = when {
        accionesUltimaSemana >= 10 -> 3
        accionesUltimaSemana >= 3 -> 2
        else -> 1
    }
}
