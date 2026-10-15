package cl.DSY1105.cocinacapital.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "fechas_especiales")
data class FechaEspecial(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val correoUsuario: String,
    val nombre: String,
    val ocasion: Ocasion,
    val dia: Int,
    val mes: Int,
    val diasAviso: Int = 3
)
