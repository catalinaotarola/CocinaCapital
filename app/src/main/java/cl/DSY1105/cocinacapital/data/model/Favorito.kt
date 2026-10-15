package cl.DSY1105.cocinacapital.data.model

import androidx.room.Entity

@Entity(tableName = "favoritos", primaryKeys = ["correoUsuario", "localId"])
data class Favorito(
    val correoUsuario: String,
    val localId: Int
)
