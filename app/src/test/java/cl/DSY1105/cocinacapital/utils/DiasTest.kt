package cl.DSY1105.cocinacapital.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class DiasTest {

    @Test
    fun diasElegidos_ignoraRepetidosYValoresInvalidos() {
        assertEquals(listOf(1, 3, 5), diasElegidos("5a31390"))
    }

    @Test
    fun textoDias_rangoSeguido() {
        assertEquals("Lun a jue", textoDias("1234"))
        assertEquals("Lun a vie", textoDias("12345"))
    }

    @Test
    fun textoDias_casosEspeciales() {
        assertEquals("Todos los días", textoDias("1234567"))
        assertEquals("Lun, Mié, Vie", textoDias("135"))
        assertEquals("Sáb, Dom", textoDias("67"))
        assertEquals("Sin días", textoDias(""))
        assertEquals("Sin días", textoDias("9a0"))
    }

    @Test
    fun alternarDia_agregaYQuita() {
        assertEquals("1235", alternarDia("135", 2))
        assertEquals("125", alternarDia("1235", 3))
    }

    @Test
    fun textoCondiciones_juntaDiasYHorario() {
        assertEquals("Lun a jue · 09:00 a 18:00", textoCondiciones("1234", 9, 18))
    }

    @Test
    fun aplicaElDia_revisaElDiaDeLaSemana() {
        val lunes = LocalDate.of(2026, 11, 9)
        assertTrue(aplicaElDia("1234", lunes))
        assertFalse(aplicaElDia("1234", lunes.plusDays(6)))
    }
}
