package cl.DSY1105.cocinacapital.data.remote

import cl.DSY1105.cocinacapital.data.remote.dto.CambioResenaDto
import cl.DSY1105.cocinacapital.data.remote.dto.NuevaResenaDto
import cl.DSY1105.cocinacapital.data.remote.dto.RecomendacionDto
import cl.DSY1105.cocinacapital.data.remote.dto.ResenaDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ResenasApi {
    @GET("locales/{id}/resenas")
    suspend fun resenas(
        @Path("id") localId: Int,
        @Header("X-Usuario") correo: String
    ): List<ResenaDto>

    @POST("resenas")
    suspend fun crearResena(
        @Header("X-Usuario") correo: String,
        @Body resena: NuevaResenaDto
    ): ResenaDto

    @PUT("resenas/{id}")
    suspend fun editarResena(
        @Path("id") id: Int,
        @Header("X-Usuario") correo: String,
        @Body cambio: CambioResenaDto
    ): ResenaDto

    @DELETE("resenas/{id}")
    suspend fun eliminarResena(
        @Path("id") id: Int,
        @Header("X-Usuario") correo: String
    ): Response<Unit>

    @GET("recomendaciones")
    suspend fun recomendaciones(
        @Query("lat") latitud: Double,
        @Query("lon") longitud: Double,
        @Query("radioKm") radioKm: Int,
        @Query("categorias") categorias: String,
        @Query("limite") limite: Int
    ): List<RecomendacionDto>
}
