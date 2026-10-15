package cl.DSY1105.cocinacapital.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cupones")
data class Cupon(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val correoUsuario: String,
    val promocionId: Int,
    val localId: Int,
    val codigo: String,
    val estado: EstadoCupon = EstadoCupon.ACTIVO,
    val fechaObtencion: Long,
    val fechaCanje: Long? = null
)
