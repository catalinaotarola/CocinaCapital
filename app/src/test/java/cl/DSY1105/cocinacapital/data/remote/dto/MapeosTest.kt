package cl.DSY1105.cocinacapital.data.remote.dto

import cl.DSY1105.cocinacapital.data.model.Ocasion
import cl.DSY1105.cocinacapital.data.model.Resena
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class MapeosTest {

    @Test
    fun promocionDto_validaSeConvierte() {
        val promo = PromocionDto(101, 7, "2x1 en pizzas", "2x1", "1234", 15, 19, "2027-03-31", 30, 1490, true, "AMIGOS").aModelo()
        assertEquals("Lun a jue · 15:00 a 19:00", promo?.condiciones)
        assertEquals(Ocasion.AMIGOS, promo?.ocasion)
        assertEquals(30, promo?.cuposDisponibles)
    }

    @Test
    fun promocionDto_ocasionDesconocidaQuedaEnCualquiera() {
        val promo = PromocionDto(1, 7, "x", null, "1", 1, 2, "2027-01-01", null, null, null, "XYZ").aModelo()
        assertEquals(Ocasion.CUALQUIERA, promo?.ocasion)
        assertEquals("Promo", promo?.etiqueta)
    }

    @Test
    fun promocionDto_invalidaSeDescarta() {
        assertNull(PromocionDto(1, 7, null, "x", "1", 1, 2, "2027-01-01", 1, 1, true, null).aModelo())
        assertNull(PromocionDto(1, 7, "x", "x", "1", 19, 15, "2027-01-01", 1, 1, true, null).aModelo())
        assertNull(PromocionDto(1, 7, "x", "x", "1", 1, 2, "mañana", 1, 1, true, null).aModelo())
        assertNull(PromocionDto(1, 7, "x", "x", "", 1, 2, "2027-01-01", 1, 1, true, null).aModelo())
    }

    @Test
    fun localDto_rellenaLosVacios() {
        val local = LocalDto(7, "Pizzería Brasa", null, null, null, -32.9, -71.5, null, null, listOf("A · 1", "B · 2"), null).aModelo()
        assertEquals("A · 1\nB · 2", local?.menuDestacado)
        assertEquals("Otra", local?.categoria)
        assertEquals("", local?.correo)
    }

    @Test
    fun localDto_sinCoordenadasSeDescarta() {
        assertNull(LocalDto(7, "x", null, null, null, null, -71.5, null, null, null, null).aModelo())
    }

    @Test
    fun resenaDto_seConvierteYLimitaElPuntaje() {
        val resena = ResenaDto(1, 2, null, 9, "ok", "2026-11-10", null).aModelo()
        assertEquals(Resena(1, 2, "Anónimo", 5, "ok", "2026-11-10", false), resena)
        assertNull(ResenaDto(null, 2, "A", 3, "", "", true).aModelo())
    }

    @Test
    fun recomendacionDto_seConvierte() {
        val r = RecomendacionDto(1, "Caleta", "Mariscos", 4.7, 3, "m").aModelo()
        assertEquals(4.7, r!!.promedio, 0.0)
        assertEquals(3, r.cantidadResenas)
    }
}
