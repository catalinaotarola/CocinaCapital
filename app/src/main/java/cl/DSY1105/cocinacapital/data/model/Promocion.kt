package cl.DSY1105.cocinacapital.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "promociones")
data class Promocion(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val localId: Int,
    val titulo: String,
    val etiqueta: String,
    val condiciones: String,
    val horaInicio: Int,
    val horaFin: Int,
    val fechaTermino: String,
    val cuposTotales: Int,
    val cuposDisponibles: Int,
    val bajaDemanda: Boolean,
    val ocasion: Ocasion,
    val activa: Boolean = true
)
