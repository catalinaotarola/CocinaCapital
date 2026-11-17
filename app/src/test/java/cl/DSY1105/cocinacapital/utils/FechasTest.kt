package cl.DSY1105.cocinacapital.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.ZoneId

class FechasTest {

    private val lunes = LocalDate.of(2026, 11, 9)

    @Test
    fun diasHasta_mismoAnio() {
        assertEquals(22L, diasHasta(1, 12, lunes))
    }

    @Test
    fun diasHasta_siYaPasoCuentaHastaElProximoAnio() {
        assertEquals(364L, diasHasta(8, 11, lunes))
    }

    @Test
    fun diasHasta_hoyEsCero() {
        assertEquals(0L, diasHasta(9, 11, lunes))
    }

    @Test
    fun diasHasta_29DeFebreroEnAnioNoBisiesto() {
        assertEquals(1L, diasHasta(29, 2, LocalDate.of(2027, 2, 27)))
    }

    @Test
    fun esFechaValida_aceptaSoloFormatoIso() {
        assertTrue(esFechaValida("2027-03-31"))
        assertFalse(esFechaValida("31-03-2027"))
    }

    @Test
    fun estaVencida_yEsHoy() {
        val hoy = LocalDate.now()
        assertTrue(estaVencida(hoy.minusDays(1).toString()))
        assertFalse(estaVencida(hoy.toString()))
        assertTrue(esHoy(hoy.toString()))
        assertFalse(esHoy(hoy.plusDays(1).toString()))
    }

    @Test
    fun formatearFecha_diaMesAnio() {
        assertEquals("05/01/2027", formatearFecha("2027-01-05"))
    }

    @Test
    fun formatearMillis_yFechaHora() {
        val millis = LocalDate.of(2026, 11, 9).atTime(14, 5).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        assertEquals("09/11/2026", formatearMillis(millis))
        assertEquals("09/11 a las 14:05", formatearFechaHora(millis))
    }

    @Test
    fun calcularVenceReserva_usaLasHorasSiAlcanza() {
        val ahora = LocalDate.now().atTime(10, 0).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val vence = calcularVenceReserva(ahora, 48, LocalDate.now().plusDays(30).toString())
        assertEquals(48L, (vence - ahora) / 3_600_000)
    }

    @Test
    fun calcularVenceReserva_noPasaDelUltimoDiaDeLaPromo() {
        val ahora = LocalDate.now().atTime(10, 0).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val vence = calcularVenceReserva(ahora, 48, LocalDate.now().toString())
        assertTrue(formatearFechaHora(vence).endsWith("23:59"))
    }

    @Test
    fun textos_deFechas() {
        assertEquals("1 de diciembre", textoDiaMes(1, 12))
        assertEquals("es hoy", textoCuando(0))
        assertEquals("es mañana", textoCuando(1))
        assertEquals("es en 8 días", textoCuando(8))
    }
}
