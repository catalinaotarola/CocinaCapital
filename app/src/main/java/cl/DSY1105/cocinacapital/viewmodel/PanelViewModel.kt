package cl.DSY1105.cocinacapital.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.DSY1105.cocinacapital.data.model.Cupon
import cl.DSY1105.cocinacapital.data.model.Local
import cl.DSY1105.cocinacapital.data.model.Ocasion
import cl.DSY1105.cocinacapital.data.model.Promocion
import cl.DSY1105.cocinacapital.data.repository.CocinaRepository
import cl.DSY1105.cocinacapital.data.repository.ResultadoCupon
import cl.DSY1105.cocinacapital.utils.estaVencida
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PanelViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = CocinaRepository(app)

    val locales: StateFlow<List<Local>> =
        repo.locales.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    val promociones: StateFlow<List<Promocion>> =
        repo.promociones.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    var localId by mutableIntStateOf(1)
        private set

    fun elegirLocal(id: Int) {
        localId = id
    }

    fun cuponesDelLocal(id: Int): Flow<List<Cupon>> = repo.cuponesDelLocal(id)
    fun vistasDelLocal(id: Int): Flow<Int> = repo.vistasDelLocal(id)

    fun validar(codigo: String, alTerminar: (exito: Boolean, mensaje: String) -> Unit) {
        if (codigo.isBlank()) {
            alTerminar(false, "Escribe o escanea el código del cupón.")
            return
        }
        viewModelScope.launch {
            when (val resultado = repo.validarCupon(codigo, localId)) {
                is ResultadoCupon.Exito -> {
                    val promo = promociones.value.find { it.id == resultado.cupon.promocionId }
                    val cliente = repo.usuarios().find { it.correo == resultado.cupon.correoUsuario }?.nombre ?: "Cliente"
                    alTerminar(true, "Cupón válido. ${promo?.titulo ?: "Beneficio"} · $cliente")
                }
                is ResultadoCupon.Error -> alTerminar(false, resultado.mensaje)
            }
        }
    }

    fun cambiarActiva(promo: Promocion, activa: Boolean) {
        viewModelScope.launch { repo.actualizarPromocion(promo.copy(activa = activa)) }
    }

    fun guardarPromocion(
        original: Promocion?,
        titulo: String,
        etiqueta: String,
        condiciones: String,
        horaInicio: String,
        horaFin: String,
        fechaTermino: String,
        cupos: String,
        ocasion: Ocasion,
        bajaDemanda: Boolean
    ): String? {
        if (titulo.isBlank() || etiqueta.isBlank() || condiciones.isBlank()) {
            return "Completa el título, la etiqueta y las condiciones."
        }
        val inicio = horaInicio.toIntOrNull()
        val fin = horaFin.toIntOrNull()
        if (inicio == null || fin == null || inicio !in 0..23 || fin !in 1..24 || inicio >= fin) {
            return "Revisa el horario: las horas van de 0 a 24 y el inicio debe ser antes del fin."
        }
        if (fechaTermino.isBlank()) return "Elige hasta qué día es válida la promoción."
        if (estaVencida(fechaTermino)) return "La fecha de término no puede estar en el pasado."
        val totales = cupos.toIntOrNull()
        if (totales == null || totales <= 0) return "Los cupos deben ser un número mayor que 0."

        val usados = if (original != null) original.cuposTotales - original.cuposDisponibles else 0
        val promo = Promocion(
            id = original?.id ?: 0,
            localId = original?.localId ?: localId,
            titulo = titulo.trim(),
            etiqueta = etiqueta.trim(),
            condiciones = condiciones.trim(),
            horaInicio = inicio,
            horaFin = fin,
            fechaTermino = fechaTermino,
            cuposTotales = totales,
            cuposDisponibles = (totales - usados).coerceAtLeast(0),
            bajaDemanda = bajaDemanda,
            ocasion = ocasion,
            activa = original?.activa ?: true
        )
        viewModelScope.launch {
            if (original == null) repo.crearPromocion(promo) else repo.actualizarPromocion(promo)
        }
        return null
    }
}
