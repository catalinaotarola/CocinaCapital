package cl.DSY1105.cocinacapital.utils

private val CORREO_REGEX = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

fun esCorreoValido(correo: String): Boolean = CORREO_REGEX.matches(correo.trim())
