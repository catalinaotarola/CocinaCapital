package cl.DSY1105.cocinacapital.utils

import cl.DSY1105.cocinacapital.data.remote.dto.ErrorDto
import com.google.gson.Gson
import com.google.gson.JsonParseException
import retrofit2.HttpException
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

fun mensajeDeError(error: Throwable): String = when (error) {
    is UnknownHostException -> "Sin conexión a internet. Revisa tu red e inténtalo de nuevo."
    is ConnectException -> "No pudimos conectar con el servidor. ¿Están encendidos los microservicios?"
    is SocketTimeoutException -> "El servidor tardó demasiado en responder. Inténtalo otra vez."
    is HttpException -> mensajeDelServidor(error) ?: "El servidor respondió con un error (${error.code()})."
    is JsonParseException -> "Los datos del servidor llegaron con un formato inesperado."
    is IOException -> "No pudimos conectarnos. Inténtalo de nuevo."
    else -> "Ocurrió un problema al cargar los datos."
}

private fun mensajeDelServidor(error: HttpException): String? = runCatching {
    val texto = error.response()?.errorBody()?.string().orEmpty()
    Gson().fromJson(texto, ErrorDto::class.java)?.error
}.getOrNull()?.takeIf { it.isNotBlank() }
