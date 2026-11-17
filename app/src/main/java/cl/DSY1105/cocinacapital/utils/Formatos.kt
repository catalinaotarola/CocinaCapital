package cl.DSY1105.cocinacapital.utils

fun formatearPesos(valor: Int): String =
    "$" + valor.toString().reversed().chunked(3).joinToString(".").reversed()
