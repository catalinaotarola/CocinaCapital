package cl.DSY1105.cocinacapital.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import cl.DSY1105.cocinacapital.data.model.Cupon
import kotlinx.coroutines.flow.Flow

@Dao
interface CuponDao {
    @Insert
    suspend fun insertar(cupon: Cupon): Long

    @Update
    suspend fun actualizar(cupon: Cupon)

    @Query("SELECT * FROM cupones WHERE correoUsuario = :correo ORDER BY fechaObtencion DESC")
    fun cuponesDe(correo: String): Flow<List<Cupon>>

    @Query("SELECT * FROM cupones WHERE correoUsuario = :correo")
    suspend fun cuponesDeAhora(correo: String): List<Cupon>

    @Query("SELECT * FROM cupones WHERE id = :id")
    fun cupon(id: Long): Flow<Cupon?>

    @Query("SELECT COUNT(*) FROM cupones WHERE correoUsuario = :correo AND promocionId = :promocionId")
    suspend fun contarDe(correo: String, promocionId: Int): Int

    @Query("SELECT * FROM cupones WHERE codigo = :codigo LIMIT 1")
    suspend fun buscarPorCodigo(codigo: String): Cupon?

    @Query("SELECT * FROM cupones WHERE localId = :localId")
    fun cuponesDelLocal(localId: Int): Flow<List<Cupon>>

    @Query("DELETE FROM cupones WHERE correoUsuario = :correo")
    suspend fun borrarDe(correo: String)
}
