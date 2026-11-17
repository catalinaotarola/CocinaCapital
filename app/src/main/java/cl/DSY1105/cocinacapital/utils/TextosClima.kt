package cl.DSY1105.cocinacapital.utils

fun describirClima(codigo: Int): String = when (codigo) {
    0 -> "Despejado"
    1, 2 -> "Parcialmente nublado"
    3 -> "Nublado"
    45, 48 -> "Neblina"
    51, 53, 55, 56, 57 -> "Llovizna"
    61, 63, 65, 66, 67, 80, 81, 82 -> "Lluvia"
    71, 73, 75, 77, 85, 86 -> "Nieve"
    95, 96, 97, 99 -> "Tormenta"
    else -> "Clima variable"
}

fun sugerirSegunClima(codigo: Int, temperatura: Int): String = when {
    codigo in 51..67 || codigo in 80..82 || codigo >= 95 -> "Día de lluvia: ideal para un café o un ramen calentito."
    codigo <= 2 && temperatura >= 20 -> "Día soleado: perfecto para una terraza frente al mar."
    temperatura <= 12 -> "Hace frío: busca una parrilla o algo calentito."
    else -> "Buen día para salir a comer cerca de ti."
}
