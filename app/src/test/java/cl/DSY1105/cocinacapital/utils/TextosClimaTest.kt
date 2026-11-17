package cl.DSY1105.cocinacapital.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TextosClimaTest {

    @Test
    fun describirClima_traduceLosCodigos() {
        assertEquals("Despejado", describirClima(0))
        assertEquals("Parcialmente nublado", describirClima(2))
        assertEquals("Nublado", describirClima(3))
        assertEquals("Neblina", describirClima(45))
        assertEquals("Llovizna", describirClima(53))
        assertEquals("Lluvia", describirClima(63))
        assertEquals("Nieve", describirClima(71))
        assertEquals("Tormenta", describirClima(95))
        assertEquals("Clima variable", describirClima(-1))
    }

    @Test
    fun sugerirSegunClima_cubreCadaCaso() {
        assertTrue(sugerirSegunClima(61, 15).startsWith("Día de lluvia"))
        assertTrue(sugerirSegunClima(0, 24).startsWith("Día soleado"))
        assertTrue(sugerirSegunClima(3, 10).startsWith("Hace frío"))
        assertTrue(sugerirSegunClima(3, 17).startsWith("Buen día"))
    }
}
