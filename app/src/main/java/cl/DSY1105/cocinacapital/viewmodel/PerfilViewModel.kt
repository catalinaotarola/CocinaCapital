package cl.DSY1105.cocinacapital.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.DSY1105.cocinacapital.data.AjustesAvisos
import cl.DSY1105.cocinacapital.data.model.FechaEspecial
import cl.DSY1105.cocinacapital.data.model.Ocasion
import cl.DSY1105.cocinacapital.data.repository.CocinaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PerfilViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = CocinaRepository(app)

    val avisos: StateFlow<AjustesAvisos> =
        repo.ajustes.avisos.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AjustesAvisos())

    fun cambiarAvisos(nuevos: AjustesAvisos) {
        viewModelScope.launch { repo.ajustes.cambiarAvisos(nuevos) }
    }

    fun fechasDe(correo: String): Flow<List<FechaEspecial>> = repo.fechasDe(correo)

    fun agregarFecha(
        correo: String,
        nombre: String,
        ocasion: Ocasion,
        dia: Int,
        mes: Int,
        diasAviso: Int
    ): String? {
        if (nombre.isBlank()) return "Escribe qué se celebra."
        if (dia == 0 || mes == 0) return "Elige la fecha."
        viewModelScope.launch {
            repo.agregarFecha(
                FechaEspecial(
                    correoUsuario = correo,
                    nombre = nombre.trim(),
                    ocasion = ocasion,
                    dia = dia,
                    mes = mes,
                    diasAviso = diasAviso
                )
            )
        }
        return null
    }

    fun eliminarFecha(fecha: FechaEspecial) {
        viewModelScope.launch { repo.eliminarFecha(fecha) }
    }
}
