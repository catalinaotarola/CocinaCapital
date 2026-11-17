package cl.DSY1105.cocinacapital.utils

import org.junit.Assert.assertEquals
import org.junit.Test

class DistanciaTest {

    @Test
    fun distanciaKm_entreDosPuntosCercanos() {
        val km = distanciaKm(-32.9255, -71.5155, -32.9248, -71.5168)
        assertEquals("144 m", textoDistancia(km))
    }

    @Test
    fun distanciaKm_mismoPuntoEsCero() {
        assertEquals(0.0, distanciaKm(-32.9, -71.5, -32.9, -71.5), 0.0001)
    }

    @Test
    fun textoDistancia_usaKilometrosDesdeUno() {
        assertEquals("1,3 km", textoDistancia(1.26))
        assertEquals("400 m", textoDistancia(0.4))
    }
}
