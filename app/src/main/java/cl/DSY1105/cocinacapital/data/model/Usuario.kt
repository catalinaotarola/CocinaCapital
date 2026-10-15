package cl.DSY1105.cocinacapital.data.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usuarios")
data class Usuario(
    val nombre: String,
    @PrimaryKey val correo: String,
    val clave: String,
    val fechaConsentimiento: Long,
    @Embedded val preferencias: Preferencias = Preferencias()
)
