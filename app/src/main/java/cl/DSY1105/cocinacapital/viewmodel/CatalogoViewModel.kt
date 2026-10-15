package cl.DSY1105.cocinacapital.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.DSY1105.cocinacapital.data.model.FechaEspecial
import cl.DSY1105.cocinacapital.data.model.Local
import cl.DSY1105.cocinacapital.data.model.Promocion
import cl.DSY1105.cocinacapital.data.model.Usuario
import cl.DSY1105.cocinacapital.data.repository.CocinaRepository
import cl.DSY1105.cocinacapital.data.repository.DatosRecomendacion
import cl.DSY1105.cocinacapital.utils.Reglas
import cl.DSY1105.cocinacapital.utils.distanciaKm
import cl.DSY1105.cocinacapital.utils.estaVencida
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class TarjetaPromo(
    val promocion: Promocion,
    val local: Local,
    val distanciaKm: Double,
    val puntaje: Int
)

data class Sugerencia(
    val fecha: FechaEspecial,
    val diasFaltantes: Long,
    val opciones: List<TarjetaPromo>
)

class CatalogoViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = CocinaRepository(app)

    val locales: StateFlow<List<Local>> =
        repo.locales.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    val promociones: StateFlow<List<Promocion>> =
        repo.promociones.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    var busqueda by mutableStateOf("")
        private set
    var categoria by mutableStateOf("Todos")
        private set
    var soloConPromocion by mutableStateOf(false)
        private set

    var latitud by mutableDoubleStateOf(Reglas.LATITUD_CONCON)
        private set
    var longitud by mutableDoubleStateOf(Reglas.LONGITUD_CONCON)
        private set

    var datosUsuario by mutableStateOf(DatosRecomendacion())
        private set

    var mensajeConexion by mutableStateOf<String?>(null)
        private set

    init {
        sincronizar()
        viewModelScope.launch {
            repo.ajustes.leerUbicacion()?.let { (lat, lon) ->
                latitud = lat
                longitud = lon
            }
        }
    }

    fun cambiarBusqueda(texto: String) { busqueda = texto }
    fun cambiarCategoria(nueva: String) { categoria = nueva }
    fun cambiarSoloConPromocion(valor: Boolean) { soloConPromocion = valor }

    fun actualizarUbicacion(lat: Double, lon: Double) {
        latitud = lat
        longitud = lon
        viewModelScope.launch { repo.ajustes.guardarUbicacion(lat, lon) }
    }

    fun sincronizar() {
        viewModelScope.launch { mensajeConexion = repo.sincronizarCatalogo() }
    }

    fun refrescarDatosUsuario(correo: String) {
        viewModelScope.launch { datosUsuario = repo.datosParaRecomendar(correo) }
    }

    fun registrarBusqueda(correo: String) {
        val texto = busqueda.trim()
        if (texto.isBlank()) return
        viewModelScope.launch { repo.registrarActividad(correo, "BUSQUEDA", texto) }
    }

    fun registrarVista(correo: String, localId: Int) {
        viewModelScope.launch { repo.registrarActividad(correo, "VISTA", localId.toString()) }
    }

    fun favoritosDe(correo: String): Flow<List<Int>> = repo.favoritosDe(correo)

    fun cambiarFavorito(correo: String, localId: Int, yaEsFavorito: Boolean) {
        viewModelScope.launch { repo.cambiarFavorito(correo, localId, yaEsFavorito) }
    }

    fun distanciaA(local: Local): Double = distanciaKm(latitud, longitud, local.latitud, local.longitud)

    fun estaDisponible(promo: Promocion): Boolean =
        promo.activa && promo.cuposDisponibles > 0 && !estaVencida(promo.fechaTermino)

    fun tarjetas(usuario: Usuario?, locales: List<Local>, promociones: List<Promocion>): List<TarjetaPromo> {
        val texto = busqueda.trim()
        return promociones
            .filter { estaDisponible(it) }
            .mapNotNull { promo ->
                val local = locales.find { it.id == promo.localId } ?: return@mapNotNull null
                val coincideTexto = texto.isBlank() ||
                    local.nombre.contains(texto, ignoreCase = true) ||
                    local.categoria.contains(texto, ignoreCase = true) ||
                    promo.titulo.contains(texto, ignoreCase = true)
                val coincideCategoria = categoria == "Todos" || local.categoria == categoria
                if (coincideTexto && coincideCategoria) {
                    TarjetaPromo(promo, local, distanciaA(local), puntaje(usuario, promo, local))
                } else {
                    null
                }
            }
            .sortedWith(compareByDescending<TarjetaPromo> { it.puntaje }.thenBy { it.distanciaKm })
    }

    fun puntaje(usuario: Usuario?, promo: Promocion, local: Local): Int = 0

    fun sugerencia(usuario: Usuario?, tarjetas: List<TarjetaPromo>): Sugerencia? = null

    fun localesCercanos(
        locales: List<Local>,
        promociones: List<Promocion>,
        radioKm: Int
    ): List<Pair<Local, Double>> =
        locales
            .map { it to distanciaA(it) }
            .filter { (local, km) ->
                km <= radioKm &&
                    (!soloConPromocion || promociones.any { it.localId == local.id && estaDisponible(it) })
            }
            .sortedBy { it.second }
}
