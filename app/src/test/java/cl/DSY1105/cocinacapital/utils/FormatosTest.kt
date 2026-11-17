package cl.DSY1105.cocinacapital.utils

import org.junit.Assert.assertEquals
import org.junit.Test

class FormatosTest {

    @Test
    fun formatearPesos_agregaPuntosDeMiles() {
        assertEquals("$0", formatearPesos(0))
        assertEquals("$990", formatearPesos(990))
        assertEquals("$1.990", formatearPesos(1990))
        assertEquals("$1.234.567", formatearPesos(1234567))
    }
}
