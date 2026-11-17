package cl.DSY1105.cocinacapital.utils

import java.time.LocalDate

val NOMBRES_DIAS = listOf("Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom")

fun diasElegidos(dias: String): List<Int> =
    dias.mapNotNull { it.digitToIntOrNull() }.filter { it in 1..7 }.distinct().sorted()

fun alternarDia(dias: String, numero: Int): String {
    val elegidos = diasElegidos(dias).toMutableSet()
    if (!elegidos.add(numero)) elegidos.remove(numero)
    return elegidos.sorted().joinToString("")
}

fun textoDias(dias: String): String {
    val elegidos = diasElegidos(dias)
    return when {
        elegidos.isEmpty() -> "Sin días"
        elegidos.size == 7 -> "Todos los días"
        elegidos.size >= 3 && elegidos.last() - elegidos.first() == elegidos.size - 1 ->
            "${NOMBRES_DIAS[elegidos.first() - 1]} a ${NOMBRES_DIAS[elegidos.last() - 1].lowercase()}"
        else -> elegidos.joinToString(", ") { NOMBRES_DIAS[it - 1] }
    }
}

fun textoCondiciones(dias: String, horaInicio: Int, horaFin: Int): String =
    "${textoDias(dias)} · %02d:00 a %02d:00".format(horaInicio, horaFin)

fun aplicaElDia(dias: String, fecha: LocalDate = LocalDate.now()): Boolean =
    fecha.dayOfWeek.value in diasElegidos(dias)
