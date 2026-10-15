package cl.DSY1105.cocinacapital.utils

import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt

fun distanciaKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val radioTierra = 6371.0
    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)
    val a = sin(dLat / 2).pow(2) +
        cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLon / 2).pow(2)
    return radioTierra * 2 * atan2(sqrt(a), sqrt(1 - a))
}

fun textoDistancia(km: Double): String =
    if (km < 1) "${(km * 1000).roundToInt()} m"
    else String.format("%.1f km", km).replace('.', ',')
