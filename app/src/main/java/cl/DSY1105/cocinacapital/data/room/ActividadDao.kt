package cl.DSY1105.cocinacapital.data.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import cl.DSY1105.cocinacapital.data.model.Actividad
import cl.DSY1105.cocinacapital.data.model.Favorito
import kotlinx.coroutines.flow.Flow

@Dao
interface ActividadDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun agregarFavorito(favorito: Favorito)

    @Delete
    suspend fun quitarFavorito(favorito: Favorito)

    @Query("SELECT localId FROM favoritos WHERE correoUsuario = :correo")
    fun favoritosDe(correo: String): Flow<List<Int>>

    @Query("SELECT localId FROM favoritos WHERE correoUsuario = :correo")
    suspend fun favoritosDeAhora(correo: String): List<Int>

    @Insert
    suspend fun registrar(actividad: Actividad)

    @Query("SELECT * FROM actividad WHERE correoUsuario = :correo")
    suspend fun actividadDeAhora(correo: String): List<Actividad>

    @Query("SELECT COUNT(*) FROM actividad WHERE tipo = 'VISTA' AND valor = :localId")
    fun vistasDelLocal(localId: String): Flow<Int>

    @Query("DELETE FROM favoritos WHERE correoUsuario = :correo")
    suspend fun borrarFavoritosDe(correo: String)

    @Query("DELETE FROM actividad WHERE correoUsuario = :correo")
    suspend fun borrarActividadDe(correo: String)
}
