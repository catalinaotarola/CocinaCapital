package cl.DSY1105.cocinacapital.data.repository

import cl.DSY1105.cocinacapital.data.model.Clima
import cl.DSY1105.cocinacapital.data.remote.ClienteApi
import cl.DSY1105.cocinacapital.data.remote.ClimaApi
import cl.DSY1105.cocinacapital.utils.describirClima
import cl.DSY1105.cocinacapital.utils.sugerirSegunClima
import kotlin.math.roundToInt

class ClimaRepository(private val api: ClimaApi = ClienteApi.clima) {
    suspend fun climaActual(latitud: Double, longitud: Double): Clima {
        val respuesta = api.climaActual(latitud, longitud, "temperature_2m,weather_code", "auto")
        val actual = respuesta.actual ?: throw IllegalStateException("La respuesta no trae el clima actual.")
        val temperatura = actual.temperatura?.roundToInt()
            ?: throw IllegalStateException("La respuesta no trae la temperatura.")
        val codigo = actual.codigo ?: -1
        return Clima(
            temperatura = temperatura,
            descripcion = describirClima(codigo),
            sugerencia = sugerirSegunClima(codigo, temperatura)
        )
    }
}
