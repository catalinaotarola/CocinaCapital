package cl.DSY1105.cocinacapital.data.model

data class Resena(
    val id: Int,
    val localId: Int,
    val autor: String,
    val puntaje: Int,
    val comentario: String,
    val fecha: String,
    val esMia: Boolean
)

data class Recomendacion(
    val localId: Int,
    val nombre: String,
    val categoria: String,
    val promedio: Double,
    val cantidadResenas: Int,
    val motivo: String
)
