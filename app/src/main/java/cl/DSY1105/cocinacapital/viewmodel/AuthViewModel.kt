package cl.DSY1105.cocinacapital.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.DSY1105.cocinacapital.data.model.Preferencias
import cl.DSY1105.cocinacapital.data.model.Usuario
import cl.DSY1105.cocinacapital.data.repository.CocinaRepository
import cl.DSY1105.cocinacapital.utils.esCorreoValido
import kotlinx.coroutines.launch

sealed class ResultadoAuth {
    data object Exito : ResultadoAuth()
    data class Error(val mensaje: String) : ResultadoAuth()
}

class AuthViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = CocinaRepository(app)

    private val usuarios = mutableListOf<Usuario>()

    var usuarioActual by mutableStateOf<Usuario?>(null)
        private set

    var listo by mutableStateOf(false)
        private set

    init {
        viewModelScope.launch {
            repo.cargarDatosIniciales()
            usuarios.addAll(repo.usuarios())
            val correoGuardado = repo.ajustes.leerSesion()
            usuarioActual = usuarios.find { it.correo == correoGuardado }
            listo = true
        }
    }

    fun registrar(
        nombre: String,
        correo: String,
        clave: String,
        confirmar: String,
        aceptaPrivacidad: Boolean
    ): ResultadoAuth {
        val correoLimpio = correo.trim().lowercase()
        return when {
            nombre.isBlank() || correo.isBlank() || clave.isBlank() || confirmar.isBlank() ->
                ResultadoAuth.Error("Completa todos los campos.")
            !esCorreoValido(correoLimpio) ->
                ResultadoAuth.Error("El correo no tiene un formato válido.")
            clave.length < 6 ->
                ResultadoAuth.Error("La contraseña debe tener al menos 6 caracteres.")
            clave != confirmar ->
                ResultadoAuth.Error("Las contraseñas no coinciden.")
            !aceptaPrivacidad ->
                ResultadoAuth.Error("Debes aceptar la política de privacidad para continuar.")
            usuarios.any { it.correo == correoLimpio } ->
                ResultadoAuth.Error("Ese correo ya está registrado.")
            else -> {
                val nuevo = Usuario(nombre.trim(), correoLimpio, clave, System.currentTimeMillis())
                usuarios.add(nuevo)
                usuarioActual = nuevo
                viewModelScope.launch {
                    repo.guardarUsuario(nuevo)
                    repo.ajustes.guardarSesion(nuevo.correo)
                }
                ResultadoAuth.Exito
            }
        }
    }

    fun iniciarSesion(correo: String, clave: String): ResultadoAuth {
        val correoLimpio = correo.trim().lowercase()
        if (correoLimpio.isBlank() || clave.isBlank()) {
            return ResultadoAuth.Error("Ingresa tu correo y tu contraseña.")
        }
        if (!esCorreoValido(correoLimpio)) {
            return ResultadoAuth.Error("El correo no tiene un formato válido.")
        }
        val usuario = usuarios.find { it.correo == correoLimpio && it.clave == clave }
            ?: return ResultadoAuth.Error("Correo o contraseña incorrectos.")
        usuarioActual = usuario
        viewModelScope.launch { repo.ajustes.guardarSesion(usuario.correo) }
        return ResultadoAuth.Exito
    }

    fun recuperarClave(correo: String): ResultadoAuth {
        val correoLimpio = correo.trim().lowercase()
        return when {
            correoLimpio.isBlank() -> ResultadoAuth.Error("Ingresa tu correo.")
            !esCorreoValido(correoLimpio) -> ResultadoAuth.Error("El correo no tiene un formato válido.")
            else -> {
                viewModelScope.launch { repo.pedirRecuperacion(correoLimpio) }
                ResultadoAuth.Exito
            }
        }
    }

    fun guardarPreferencias(nuevas: Preferencias) {
        val actual = usuarioActual ?: return
        actualizarEnTodasPartes(actual.copy(preferencias = nuevas))
    }

    fun actualizarNombre(nombre: String): ResultadoAuth {
        val actual = usuarioActual ?: return ResultadoAuth.Error("No hay una sesión iniciada.")
        if (nombre.isBlank()) return ResultadoAuth.Error("El nombre no puede quedar vacío.")
        actualizarEnTodasPartes(actual.copy(nombre = nombre.trim()))
        return ResultadoAuth.Exito
    }

    fun eliminarCuenta() {
        val actual = usuarioActual ?: return
        usuarios.removeAll { it.correo == actual.correo }
        usuarioActual = null
        viewModelScope.launch { repo.eliminarCuenta(actual) }
    }

    fun cerrarSesion() {
        usuarioActual = null
        viewModelScope.launch { repo.ajustes.borrarSesion() }
    }

    private fun actualizarEnTodasPartes(actualizado: Usuario) {
        val posicion = usuarios.indexOfFirst { it.correo == actualizado.correo }
        if (posicion >= 0) usuarios[posicion] = actualizado
        usuarioActual = actualizado
        viewModelScope.launch { repo.actualizarUsuario(actualizado) }
    }
}
