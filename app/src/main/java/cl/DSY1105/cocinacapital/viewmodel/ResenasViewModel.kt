package cl.DSY1105.cocinacapital.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.DSY1105.cocinacapital.data.model.Recomendacion
import cl.DSY1105.cocinacapital.data.model.Resena
import cl.DSY1105.cocinacapital.data.repository.ResenasRepository
import cl.DSY1105.cocinacapital.utils.mensajeDeError
import cl.DSY1105.cocinacapital.utils.validarResena
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private data class ConsultaDestacados(
    val latitud: Double,
    val longitud: Double,
    val radioKm: Int,
    val categorias: Set<String>
)

class ResenasViewModel(
    private val repo: ResenasRepository = ResenasRepository()
) : ViewModel() {

    private val _resenas = MutableStateFlow<UiState<List<Resena>>>(UiState.Cargando)
    val resenas: StateFlow<UiState<List<Resena>>> = _resenas.asStateFlow()

    private val _destacados = MutableStateFlow<UiState<List<Recomendacion>>>(UiState.Cargando)
    val destacados: StateFlow<UiState<List<Recomendacion>>> = _destacados.asStateFlow()

    var enviando by mutableStateOf(false)
        private set

    private var localActual = 0
    private var correoActual = ""
    private var trabajoResenas: Job? = null
    private var ultimaConsulta: ConsultaDestacados? = null

    fun cargarResenas(localId: Int, correo: String) {
        localActual = localId
        correoActual = correo
        trabajoResenas?.cancel()
        trabajoResenas = viewModelScope.launch {
            _resenas.value = UiState.Cargando
            _resenas.value = try {
                UiState.Exito(repo.resenasDe(localId, correo))
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                UiState.Error(mensajeDeError(e))
            }
        }
    }

    fun reintentarResenas() {
        cargarResenas(localActual, correoActual)
    }

    fun guardar(
        localId: Int,
        correo: String,
        autor: String,
        editando: Resena?,
        puntaje: Int,
        comentario: String,
        alTerminar: (problema: String?) -> Unit
    ) {
        val problema = validarResena(puntaje, comentario)
        if (problema != null) {
            alTerminar(problema)
            return
        }
        if (enviando) return
        enviando = true
        viewModelScope.launch {
            val resultado = try {
                if (editando == null) {
                    repo.publicar(localId, correo, autor, puntaje, comentario)
                } else {
                    repo.editar(editando.id, correo, puntaje, comentario)
                }
                null
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                mensajeDeError(e)
            } finally {
                enviando = false
            }
            if (resultado == null) cargarResenas(localId, correo)
            alTerminar(resultado)
        }
    }

    fun eliminar(resena: Resena, correo: String, alTerminar: (problema: String?) -> Unit) {
        if (enviando) return
        enviando = true
        viewModelScope.launch {
            val resultado = try {
                repo.eliminar(resena.id, correo)
                null
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                mensajeDeError(e)
            } finally {
                enviando = false
            }
            if (resultado == null) cargarResenas(resena.localId, correo)
            alTerminar(resultado)
        }
    }

    fun cargarDestacados(latitud: Double, longitud: Double, radioKm: Int, categorias: Set<String>) {
        val consulta = ConsultaDestacados(latitud, longitud, radioKm, categorias)
        if (_destacados.value is UiState.Exito && consulta == ultimaConsulta) return
        ultimaConsulta = consulta
        viewModelScope.launch {
            _destacados.value = UiState.Cargando
            _destacados.value = try {
                UiState.Exito(repo.recomendaciones(latitud, longitud, radioKm, categorias, 5))
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                UiState.Error(mensajeDeError(e))
            }
        }
    }

    fun reintentarDestacados() {
        val consulta = ultimaConsulta ?: return
        ultimaConsulta = null
        cargarDestacados(consulta.latitud, consulta.longitud, consulta.radioKm, consulta.categorias)
    }
}
