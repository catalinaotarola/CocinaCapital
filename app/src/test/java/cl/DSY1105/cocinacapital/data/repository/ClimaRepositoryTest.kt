package cl.DSY1105.cocinacapital.data.repository

import cl.DSY1105.cocinacapital.data.model.Clima
import cl.DSY1105.cocinacapital.data.remote.ClimaApi
import cl.DSY1105.cocinacapital.data.remote.dto.ClimaActualDto
import cl.DSY1105.cocinacapital.data.remote.dto.ClimaRespuesta
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ClimaRepositoryTest {

    private val api = mockk<ClimaApi>()
    private val repo = ClimaRepository(api)

    @Test
    fun climaActual_redondeaYDescribe() = runTest {
        coEvery { api.climaActual(-32.9, -71.5, "temperature_2m,weather_code", "auto") } returns
            ClimaRespuesta(ClimaActualDto(18.6, 0))

        val clima = repo.climaActual(-32.9, -71.5)

        assertEquals(Clima(19, "Despejado", "Buen día para salir a comer cerca de ti."), clima)
        coVerify(exactly = 1) { api.climaActual(-32.9, -71.5, "temperature_2m,weather_code", "auto") }
    }

    @Test
    fun climaActual_sinCodigoUsaClimaVariable() = runTest {
        coEvery { api.climaActual(-32.9, -71.5, "temperature_2m,weather_code", "auto") } returns
            ClimaRespuesta(ClimaActualDto(10.2, null))

        assertEquals("Clima variable", repo.climaActual(-32.9, -71.5).descripcion)
    }

    @Test
    fun climaActual_sinDatosLanzaError() = runTest {
        coEvery { api.climaActual(-32.9, -71.5, "temperature_2m,weather_code", "auto") } returns ClimaRespuesta(null)

        val error = runCatching { repo.climaActual(-32.9, -71.5) }.exceptionOrNull()
        assertTrue(error is IllegalStateException)
    }

    @Test
    fun climaActual_sinTemperaturaLanzaError() = runTest {
        coEvery { api.climaActual(-32.9, -71.5, "temperature_2m,weather_code", "auto") } returns
            ClimaRespuesta(ClimaActualDto(null, 3))

        val error = runCatching { repo.climaActual(-32.9, -71.5) }.exceptionOrNull()
        assertTrue(error is IllegalStateException)
    }
}
