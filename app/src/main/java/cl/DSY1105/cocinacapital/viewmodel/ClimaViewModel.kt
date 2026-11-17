package cl.DSY1105.cocinacapital.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.DSY1105.cocinacapital.data.model.Clima
import cl.DSY1105.cocinacapital.data.repository.ClimaRepository
import cl.DSY1105.cocinacapital.utils.mensajeDeError
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ClimaViewModel(
    private val repo: ClimaRepository = ClimaRepository()
) : ViewModel() {
    private val _estado = MutableStateFlow<UiState<Clima>>(UiState.Cargando)
    val estado: StateFlow<UiState<Clima>> = _estado.asStateFlow()

    private var trabajo: Job? = null
    private var ultimaUbicacion: Pair<Double, Double>? = null

    fun cargar(latitud: Double, longitud: Double) {
        val ubicacion = latitud to longitud
        if (_estado.value is UiState.Exito && ubicacion == ultimaUbicacion) return
        ultimaUbicacion = ubicacion
        trabajo?.cancel()
        trabajo = viewModelScope.launch {
            _estado.value = UiState.Cargando
            _estado.value = try {
                UiState.Exito(repo.climaActual(latitud, longitud))
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                UiState.Error(mensajeDeError(e))
            }
        }
    }

    fun reintentar() {
        val (latitud, longitud) = ultimaUbicacion ?: return
        ultimaUbicacion = null
        cargar(latitud, longitud)
    }
}
