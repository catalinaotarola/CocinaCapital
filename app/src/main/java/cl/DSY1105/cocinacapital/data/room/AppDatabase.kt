package cl.DSY1105.cocinacapital.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import cl.DSY1105.cocinacapital.data.model.Actividad
import cl.DSY1105.cocinacapital.data.model.Cupon
import cl.DSY1105.cocinacapital.data.model.FechaEspecial
import cl.DSY1105.cocinacapital.data.model.Favorito
import cl.DSY1105.cocinacapital.data.model.Local
import cl.DSY1105.cocinacapital.data.model.Promocion
import cl.DSY1105.cocinacapital.data.model.Usuario

@Database(
    entities = [
        Usuario::class, Local::class, Promocion::class, Cupon::class,
        FechaEspecial::class, Favorito::class, Actividad::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Convertidores::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun usuarioDao(): UsuarioDao
    abstract fun catalogoDao(): CatalogoDao
    abstract fun cuponDao(): CuponDao
    abstract fun fechaEspecialDao(): FechaEspecialDao
    abstract fun actividadDao(): ActividadDao

    companion object {
        @Volatile
        private var instancia: AppDatabase? = null

        fun obtener(context: Context): AppDatabase =
            instancia ?: synchronized(this) {
                instancia ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "cocina_capital.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { instancia = it }
            }
    }
}
