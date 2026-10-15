package cl.DSY1105.cocinacapital.data.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import cl.DSY1105.cocinacapital.data.model.FechaEspecial
import kotlinx.coroutines.flow.Flow

@Dao
interface FechaEspecialDao {
    @Insert
    suspend fun insertar(fecha: FechaEspecial): Long

    @Delete
    suspend fun eliminar(fecha: FechaEspecial)

    @Query("SELECT * FROM fechas_especiales WHERE correoUsuario = :correo ORDER BY mes, dia")
    fun fechasDe(correo: String): Flow<List<FechaEspecial>>

    @Query("SELECT * FROM fechas_especiales WHERE correoUsuario = :correo")
    suspend fun fechasDeAhora(correo: String): List<FechaEspecial>

    @Query("DELETE FROM fechas_especiales WHERE correoUsuario = :correo")
    suspend fun borrarDe(correo: String)
}
