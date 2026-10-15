package cl.DSY1105.cocinacapital.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.time.LocalDate

private val Context.ajustesDataStore by preferencesDataStore(name = "ajustes")

data class AjustesAvisos(
    val gustos: Boolean = true,
    val fechas: Boolean = true,
    val cercania: Boolean = false
)

class AjustesManager(private val context: Context) {
    private val CORREO_SESION = stringPreferencesKey("correo_sesion")
    private val AVISOS_GUSTOS = booleanPreferencesKey("avisos_gustos")
    private val AVISOS_FECHAS = booleanPreferencesKey("avisos_fechas")
    private val AVISOS_CERCA = booleanPreferencesKey("avisos_cerca")
    private val ULTIMA_LATITUD = doublePreferencesKey("ultima_latitud")
    private val ULTIMA_LONGITUD = doublePreferencesKey("ultima_longitud")
    private val DIA_AVISOS = stringPreferencesKey("dia_avisos")
    private val AVISOS_HOY = intPreferencesKey("avisos_hoy")

    suspend fun guardarSesion(correo: String) {
        context.ajustesDataStore.edit { it[CORREO_SESION] = correo }
    }

    suspend fun leerSesion(): String? = context.ajustesDataStore.data.first()[CORREO_SESION]

    suspend fun borrarSesion() {
        context.ajustesDataStore.edit { it.remove(CORREO_SESION) }
    }

    val avisos: Flow<AjustesAvisos> = context.ajustesDataStore.data.map { datos ->
        AjustesAvisos(
            gustos = datos[AVISOS_GUSTOS] ?: true,
            fechas = datos[AVISOS_FECHAS] ?: true,
            cercania = datos[AVISOS_CERCA] ?: false
        )
    }

    suspend fun cambiarAvisos(nuevos: AjustesAvisos) {
        context.ajustesDataStore.edit {
            it[AVISOS_GUSTOS] = nuevos.gustos
            it[AVISOS_FECHAS] = nuevos.fechas
            it[AVISOS_CERCA] = nuevos.cercania
        }
    }

    suspend fun guardarUbicacion(latitud: Double, longitud: Double) {
        context.ajustesDataStore.edit {
            it[ULTIMA_LATITUD] = latitud
            it[ULTIMA_LONGITUD] = longitud
        }
    }

    suspend fun leerUbicacion(): Pair<Double, Double>? {
        val datos = context.ajustesDataStore.data.first()
        val latitud = datos[ULTIMA_LATITUD]
        val longitud = datos[ULTIMA_LONGITUD]
        return if (latitud != null && longitud != null) latitud to longitud else null
    }

    suspend fun puedeAvisarHoy(maximo: Int): Boolean {
        val datos = context.ajustesDataStore.data.first()
        val hoy = LocalDate.now().toString()
        val enviadosHoy = if (datos[DIA_AVISOS] == hoy) datos[AVISOS_HOY] ?: 0 else 0
        return enviadosHoy < maximo
    }

    suspend fun contarAviso() {
        context.ajustesDataStore.edit {
            val hoy = LocalDate.now().toString()
            val enviadosHoy = if (it[DIA_AVISOS] == hoy) it[AVISOS_HOY] ?: 0 else 0
            it[DIA_AVISOS] = hoy
            it[AVISOS_HOY] = enviadosHoy + 1
        }
    }
}
