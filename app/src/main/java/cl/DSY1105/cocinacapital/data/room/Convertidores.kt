package cl.DSY1105.cocinacapital.data.room

import androidx.room.TypeConverter

class Convertidores {
    @TypeConverter
    fun desdeConjunto(valores: Set<String>): String = valores.joinToString("|")

    @TypeConverter
    fun haciaConjunto(texto: String): Set<String> =
        if (texto.isBlank()) emptySet() else texto.split("|").toSet()
}
