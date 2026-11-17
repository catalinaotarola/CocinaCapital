package cl.DSY1105.cocinacapital.utils

import cl.DSY1105.cocinacapital.data.model.Ocasion
import cl.DSY1105.cocinacapital.data.model.Promocion
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class TextosCorreoTest {

    private val lunes = LocalDate.of(2026, 11, 9)

    @Test
    fun correoFechaGuardada_listaLosTresAvisos() {
        val texto = correoFechaGuardada("Sofía", "Aniversario", "En pareja", 1, 12, lunes)
        assertTrue(texto.startsWith("Hola Sofía:"))
        assertTrue(texto.contains("Guardamos tu fecha especial \"Aniversario\" (1 de diciembre · En pareja)."))
        assertTrue(texto.contains("• 16 de noviembre (15 días antes): hasta 50% de descuento"))
        assertTrue(texto.contains("• 30 de noviembre (1 día antes): hasta 20% de descuento"))
    }

    @Test
    fun correoFechaGuardada_fechaMuyCercana() {
        val texto = correoFechaGuardada("Sofía", "Cumple", "Familiar", 9, 11, lunes)
        assertTrue(texto.contains("Tu fecha es muy pronto"))
    }

    @Test
    fun correoPromocionGuardada_incluyeLosDatosDeLaPromo() {
        val promo = Promocion(
            id = 1, localId = 7, titulo = "2x1 en pizzas", etiqueta = "2x1",
            condiciones = "Lun a jue · 15:00 a 19:00", dias = "1234", horaInicio = 15, horaFin = 19,
            fechaTermino = "2027-03-31", cuposTotales = 30, cuposDisponibles = 12, precioTicket = 1490,
            bajaDemanda = true, ocasion = Ocasion.AMIGOS
        )
        val texto = correoPromocionGuardada("Pizzería Brasa", promo)
        assertTrue(texto.contains("• Válida hasta: 31/03/2027"))
        assertTrue(texto.contains("• Cupos: 12 de 30"))
        assertTrue(texto.contains("• Ticket de prepago: $1.490"))
        assertTrue(texto.contains("• Ideal para: Con amigos"))
        assertTrue(texto.contains("• Horario de baja demanda: sí"))
    }
}
