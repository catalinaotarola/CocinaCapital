package cl.DSY1105.cocinacapital.utils

const val MAXIMO_COMENTARIO = 300

fun validarResena(puntaje: Int, comentario: String): String? {
    val texto = comentario.trim()
    return when {
        puntaje !in 1..5 -> "Elige de 1 a 5 estrellas."
        texto.length < 10 -> "Cuéntanos un poco más: mínimo 10 caracteres."
        texto.length > MAXIMO_COMENTARIO -> "Máximo $MAXIMO_COMENTARIO caracteres."
        else -> null
    }
}
