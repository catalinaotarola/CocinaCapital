package cl.DSY1105.cocinacapital.data.repository

import cl.DSY1105.cocinacapital.data.model.Resena
import cl.DSY1105.cocinacapital.data.remote.ResenasApi
import cl.DSY1105.cocinacapital.data.remote.dto.CambioResenaDto
import cl.DSY1105.cocinacapital.data.remote.dto.NuevaResenaDto
import cl.DSY1105.cocinacapital.data.remote.dto.RecomendacionDto
import cl.DSY1105.cocinacapital.data.remote.dto.ResenaDto
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response

class ResenasRepositoryTest {

    private val correo = "sofia@duocuc.cl"
    private val api = mockk<ResenasApi>()
    private val repo = ResenasRepository(api)

    private fun dto(id: Int?, puntaje: Int? = 5) = ResenaDto(id, 1, "Sofía", puntaje, "Muy rico todo", "2026-11-10", true)

    @Test
    fun resenasDe_descartaLasQueVienenIncompletas() = runTest {
        coEvery { api.resenas(1, correo) } returns listOf(dto(3), dto(null), dto(4, null))

        val resenas = repo.resenasDe(1, correo)

        assertEquals(listOf(3), resenas.map { it.id })
    }

    @Test
    fun publicar_limpiaLosTextosAntesDeEnviar() = runTest {
        val enviada = NuevaResenaDto(1, "Sofía", 5, "Muy rico todo")
        coEvery { api.crearResena(correo, enviada) } returns dto(20)

        val resena = repo.publicar(1, correo, "  Sofía ", 5, "  Muy rico todo  ")

        assertEquals(Resena(20, 1, "Sofía", 5, "Muy rico todo", "2026-11-10", true), resena)
        coVerify(exactly = 1) { api.crearResena(correo, enviada) }
    }

    @Test
    fun publicar_siElServidorNoDevuelveLaResenaLanzaError() = runTest {
        coEvery { api.crearResena(correo, NuevaResenaDto(1, "Sofía", 5, "Muy rico todo")) } returns dto(null)

        val error = runCatching { repo.publicar(1, correo, "Sofía", 5, "Muy rico todo") }.exceptionOrNull()

        assertTrue(error is IllegalStateException)
    }

    @Test
    fun editar_enviaElCambio() = runTest {
        coEvery { api.editarResena(20, correo, CambioResenaDto(3, "Ahora regular")) } returns dto(20, 3)

        val resena = repo.editar(20, correo, 3, " Ahora regular ")

        assertEquals(3, resena.puntaje)
    }

    @Test
    fun eliminar_conRespuestaCorrectaNoFalla() = runTest {
        coEvery { api.eliminarResena(20, correo) } returns Response.success(Unit)

        repo.eliminar(20, correo)

        coVerify(exactly = 1) { api.eliminarResena(20, correo) }
    }

    @Test
    fun eliminar_conRespuestaDeErrorLanzaHttpException() = runTest {
        val cuerpo = "{\"error\":\"Solo puedes eliminar tus propias reseñas.\"}".toResponseBody("application/json".toMediaType())
        coEvery { api.eliminarResena(20, correo) } returns Response.error(403, cuerpo)

        val error = runCatching { repo.eliminar(20, correo) }.exceptionOrNull()

        assertTrue(error is HttpException)
        assertEquals(403, (error as HttpException).code())
    }

    @Test
    fun recomendaciones_unePreferenciasConComas() = runTest {
        coEvery { api.recomendaciones(-32.9, -71.5, 3, "Pizza,Sushi", 5) } returns listOf(
            RecomendacionDto(7, "Pizzería Brasa", "Pizza", 4.5, 2, "★ 4,5"),
            RecomendacionDto(null, "Sin id", "Pizza", 1.0, 1, "")
        )

        val lista = repo.recomendaciones(-32.9, -71.5, 3, linkedSetOf("Pizza", "Sushi"), 5)

        assertEquals(listOf("Pizzería Brasa"), lista.map { it.nombre })
    }
}
