package cl.DSY1105.cocinacapital.utils

import com.google.gson.JsonParseException
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class ErroresRedTest {

    private fun errorHttp(codigo: Int, cuerpo: String): HttpException =
        HttpException(Response.error<Any>(codigo, cuerpo.toResponseBody("application/json".toMediaType())))

    @Test
    fun mensajeDeError_usaElTextoQueMandaElMicroservicio() {
        val error = errorHttp(409, "{\"error\":\"Ya publicaste una reseña para este local.\"}")
        assertEquals("Ya publicaste una reseña para este local.", mensajeDeError(error))
    }

    @Test
    fun mensajeDeError_siElCuerpoNoEsJsonMuestraElCodigo() {
        assertEquals("El servidor respondió con un error (500).", mensajeDeError(errorHttp(500, "<html>")))
    }

    @Test
    fun mensajeDeError_problemasDeConexion() {
        assertEquals("Sin conexión a internet. Revisa tu red e inténtalo de nuevo.", mensajeDeError(UnknownHostException()))
        assertEquals("No pudimos conectar con el servidor. ¿Están encendidos los microservicios?", mensajeDeError(ConnectException()))
        assertEquals("El servidor tardó demasiado en responder. Inténtalo otra vez.", mensajeDeError(SocketTimeoutException()))
        assertEquals("No pudimos conectarnos. Inténtalo de nuevo.", mensajeDeError(IOException()))
    }

    @Test
    fun mensajeDeError_otrosErrores() {
        assertEquals("Los datos del servidor llegaron con un formato inesperado.", mensajeDeError(JsonParseException("x")))
        assertEquals("Ocurrió un problema al cargar los datos.", mensajeDeError(IllegalStateException()))
    }
}
