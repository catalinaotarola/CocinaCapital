package cl.DSY1105.cocinacapital.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import cl.DSY1105.cocinacapital.data.model.Usuario
import cl.DSY1105.cocinacapital.datamodel.Preferencias
import cl.DSY1105.cocinacapital.utils.esCorreoValido


//recuperarClave revisa que el correo sea válido. no revisa
// si el correo existe, así nadie puede usar esa pantalla
// para averiguar quién tiene cuenta en la app, lo que cuida la privacidad
sealed class ResultadoAuth {
    data object Exito : ResultadoAuth()
    data class Error(val mensaje: String) : ResultadoAuth()
}

class AuthViewModel : ViewModel() {

    // Lista temporal en memoria; en el laboratorio 8 la reemplazamos por Room
    private val usuarios = mutableListOf(
        Usuario("Sofía", "sofia@correo.cl", "cocina123", 0L)  // usuaria de prueba (datos ficticios)
    )

    var usuarioActual by mutableStateOf<Usuario?>(null)
        private set

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
        return ResultadoAuth.Exito
    }

fun recuperarClave(correo: String): ResultadoAuth {
    val correoLimpio = correo.trim().lowercase()
    return when {
        correoLimpio.isBlank() -> ResultadoAuth.Error("Ingresa tu correo.")
        !esCorreoValido(correoLimpio) -> ResultadoAuth.Error("El correo no tiene un formato válido.")
        else -> ResultadoAuth.Exito
    }
}

// guarda los gustos del usuario que inició sesión
fun guardarPreferencias(nuevas: Preferencias) {
    val actual = usuarioActual ?: return
    val actualizado = actual.copy(preferencias = nuevas)  // copy() de las data class (EA1)
    val posicion = usuarios.indexOfFirst { it.correo == actual.correo }
    if (posicion >= 0) {
        usuarios[posicion] = actualizado
    }
    usuarioActual = actualizado
}

fun cerrarSesion() {
    usuarioActual = null
}
}
