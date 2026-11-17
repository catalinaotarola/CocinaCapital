package cl.DSY1105.cocinacapital.data.remote

import cl.DSY1105.cocinacapital.data.remote.dto.CatalogoRespuesta
import retrofit2.http.GET

interface CatalogoApi {
    @GET("catalogo")
    suspend fun catalogo(): CatalogoRespuesta
}
