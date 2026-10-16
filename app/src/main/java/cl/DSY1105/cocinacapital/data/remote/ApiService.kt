package cl.DSY1105.cocinacapital.data.remote

import cl.DSY1105.cocinacapital.data.model.Local
import cl.DSY1105.cocinacapital.data.model.Promocion
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @GET("locales")
    suspend fun locales(): List<Local>

    @GET("promociones")
    suspend fun promociones(): List<Promocion>

    @POST("auth/recuperar")
    suspend fun recuperarClave(@Body cuerpo: Map<String, String>): Response<Unit>
}
