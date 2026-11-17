package cl.DSY1105.cocinacapital.data.repository

import cl.DSY1105.cocinacapital.data.model.Recomendacion
import cl.DSY1105.cocinacapital.data.model.Resena
import cl.DSY1105.cocinacapital.data.remote.ClienteApi
import cl.DSY1105.cocinacapital.data.remote.ResenasApi
import cl.DSY1105.cocinacapital.data.remote.dto.CambioResenaDto
import cl.DSY1105.cocinacapital.data.remote.dto.NuevaResenaDto
import cl.DSY1105.cocinacapital.data.remote.dto.aModelo
import retrofit2.HttpException

class ResenasRepository(private val api: ResenasApi = ClienteApi.resenas) {

    suspend fun resenasDe(localId: Int, correo: String): List<Resena> =
        api.resenas(localId, correo).mapNotNull { it.aModelo() }

    suspend fun publicar(localId: Int, correo: String, autor: String, puntaje: Int, comentario: String): Resena =
        api.crearResena(correo, NuevaResenaDto(localId, autor.trim(), puntaje, comentario.trim())).aModelo()
            ?: throw IllegalStateException("El servidor no devolvió la reseña.")

    suspend fun editar(id: Int, correo: String, puntaje: Int, comentario: String): Resena =
        api.editarResena(id, correo, CambioResenaDto(puntaje, comentario.trim())).aModelo()
            ?: throw IllegalStateException("El servidor no devolvió la reseña.")

    suspend fun eliminar(id: Int, correo: String) {
        val respuesta = api.eliminarResena(id, correo)
        if (!respuesta.isSuccessful) throw HttpException(respuesta)
    }

    suspend fun recomendaciones(
        latitud: Double,
        longitud: Double,
        radioKm: Int,
        categorias: Set<String>,
        limite: Int
    ): List<Recomendacion> =
        api.recomendaciones(latitud, longitud, radioKm, categorias.joinToString(","), limite)
            .mapNotNull { it.aModelo() }
}
