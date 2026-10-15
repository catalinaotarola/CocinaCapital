package cl.DSY1105.cocinacapital.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "actividad")
data class Actividad(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val correoUsuario: String,
    val tipo: String,
    val valor: String,
    val fecha: Long
)
