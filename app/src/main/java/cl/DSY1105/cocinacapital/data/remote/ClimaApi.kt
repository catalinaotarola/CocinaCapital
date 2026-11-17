package cl.DSY1105.cocinacapital.data.remote

import cl.DSY1105.cocinacapital.data.remote.dto.ClimaRespuesta
import retrofit2.http.GET
import retrofit2.http.Query

interface ClimaApi {
    @GET("v1/forecast")
    suspend fun climaActual(
        @Query("latitude") latitud: Double,
        @Query("longitude") longitud: Double,
        @Query("current") datos: String,
        @Query("timezone") zonaHoraria: String
    ): ClimaRespuesta
}
