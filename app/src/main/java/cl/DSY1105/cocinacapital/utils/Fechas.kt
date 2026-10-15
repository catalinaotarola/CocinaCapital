package cl.DSY1105.cocinacapital.utils

import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.temporal.ChronoUnit

private val MESES = listOf(
    "enero", "febrero", "marzo", "abril", "mayo", "junio",
    "julio", "agosto", "septiembre", "octubre", "noviembre", "diciembre"
)

fun diasHasta(dia: Int, mes: Int, hoy: LocalDate = LocalDate.now()): Long {
    fun fechaEnElAnio(anio: Int): LocalDate {
        val diaValido = minOf(dia, YearMonth.of(anio, mes).lengthOfMonth())
        return LocalDate.of(anio, mes, diaValido)
    }
    var proxima = fechaEnElAnio(hoy.year)
    if (proxima.isBefore(hoy)) proxima = fechaEnElAnio(hoy.year + 1)
    return ChronoUnit.DAYS.between(hoy, proxima)
}

fun estaVencida(fechaTermino: String): Boolean = LocalDate.now().isAfter(LocalDate.parse(fechaTermino))

fun esHoy(fechaIso: String): Boolean = LocalDate.parse(fechaIso) == LocalDate.now()

fun formatearFecha(fechaIso: String): String {
    val f = LocalDate.parse(fechaIso)
    return "%02d/%02d/%d".format(f.dayOfMonth, f.monthValue, f.year)
}

fun formatearMillis(millis: Long): String {
    val f = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate()
    return "%02d/%02d/%d".format(f.dayOfMonth, f.monthValue, f.year)
}

fun textoDiaMes(dia: Int, mes: Int): String = "$dia de ${MESES[mes - 1]}"

fun textoCuando(dias: Long): String = when (dias) {
    0L -> "es hoy"
    1L -> "es mañana"
    else -> "es en $dias días"
}
