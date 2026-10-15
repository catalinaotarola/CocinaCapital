package cl.DSY1105.cocinacapital.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "locales")
data class Local(
    @PrimaryKey val id: Int,
    val nombre: String,
    val categoria: String,
    val descripcion: String,
    val direccion: String,
    val latitud: Double,
    val longitud: Double,
    val horario: String,
    val nivelPrecio: String,
    val menuDestacado: String
)
