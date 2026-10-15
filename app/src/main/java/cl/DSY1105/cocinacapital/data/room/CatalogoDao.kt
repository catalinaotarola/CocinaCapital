package cl.DSY1105.cocinacapital.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import cl.DSY1105.cocinacapital.data.model.Local
import cl.DSY1105.cocinacapital.data.model.Promocion
import kotlinx.coroutines.flow.Flow

@Dao
interface CatalogoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarLocales(locales: List<Local>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarPromociones(promociones: List<Promocion>)

    @Insert
    suspend fun insertarPromocion(promocion: Promocion)

    @Update
    suspend fun actualizarPromocion(promocion: Promocion)

    @Query("SELECT COUNT(*) FROM locales")
    suspend fun contarLocales(): Int

    @Query("SELECT * FROM locales ORDER BY nombre")
    fun locales(): Flow<List<Local>>

    @Query("SELECT * FROM promociones")
    fun promociones(): Flow<List<Promocion>>

    @Query("SELECT * FROM locales")
    suspend fun localesAhora(): List<Local>

    @Query("SELECT * FROM promociones")
    suspend fun promocionesAhora(): List<Promocion>

    @Query("SELECT * FROM promociones WHERE id = :id")
    suspend fun promocion(id: Int): Promocion?

    @Query("UPDATE promociones SET cuposDisponibles = cuposDisponibles - 1 WHERE id = :id AND cuposDisponibles > 0")
    suspend fun descontarCupo(id: Int): Int
}
