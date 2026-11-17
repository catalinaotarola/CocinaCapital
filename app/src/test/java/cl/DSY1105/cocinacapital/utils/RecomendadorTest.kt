package cl.DSY1105.cocinacapital.utils

import cl.DSY1105.cocinacapital.data.model.Actividad
import cl.DSY1105.cocinacapital.data.model.FechaEspecial
import cl.DSY1105.cocinacapital.data.model.Local
import cl.DSY1105.cocinacapital.data.model.Ocasion
import cl.DSY1105.cocinacapital.data.model.Preferencias
import cl.DSY1105.cocinacapital.data.model.Promocion
import cl.DSY1105.cocinacapital.data.model.Usuario
import cl.DSY1105.cocinacapital.data.repository.DatosRecomendacion
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class RecomendadorTest {

    private val usuario = Usuario(
        nombre = "Sofía",
        correo = "sofia@duocuc.cl",
        clave = "Clave123",
        fechaConsentimiento = 0L,
        preferencias = Preferencias(categorias = setOf("Pizza"), radioKm = 3)
    )

    private val local = Local(
        id = 7, nombre = "Pizzería Brasa", categoria = "Pizza", descripcion = "", direccion = "",
        latitud = -32.92, longitud = -71.51, horario = "", nivelPrecio = "$$", menuDestacado = "", correo = ""
    )

    private fun promo(ocasion: Ocasion = Ocasion.AMIGOS, bajaDemanda: Boolean = false) = Promocion(
        id = 101, localId = 7, titulo = "2x1", etiqueta = "2x1", condiciones = "", dias = "1234567",
        horaInicio = 0, horaFin = 24, fechaTermino = "2099-12-31", cuposTotales = 10, cuposDisponibles = 10,
        precioTicket = 1000, bajaDemanda = bajaDemanda, ocasion = ocasion
    )

    private fun fechaEn(dias: Long, ocasion: Ocasion): FechaEspecial {
        val f = LocalDate.now().plusDays(dias)
        return FechaEspecial(correoUsuario = usuario.correo, nombre = "Junta", ocasion = ocasion, dia = f.dayOfMonth, mes = f.monthValue)
    }

    @Test
    fun puntaje_sinUsuarioEsCero() {
        assertEquals(0, Recomendador.puntaje(null, promo(), local, 1.0, DatosRecomendacion()))
    }

    @Test
    fun puntaje_categoriaYCercania() {
        assertEquals(50, Recomendador.puntaje(usuario, promo(), local, 1.0, DatosRecomendacion()))
        assertEquals(30, Recomendador.puntaje(usuario, promo(), local, 8.0, DatosRecomendacion()))
    }

    @Test
    fun puntaje_sumaTodosLosCriterios() {
        val ahora = System.currentTimeMillis()
        val datos = DatosRecomendacion(
            fechas = listOf(fechaEn(5, Ocasion.AMIGOS)),
            favoritos = setOf(7),
            actividad = listOf(Actividad(correoUsuario = usuario.correo, tipo = "BUSQUEDA", valor = "pizz", fecha = ahora))
        )
        assertEquals(125, Recomendador.puntaje(usuario, promo(bajaDemanda = true), local, 1.0, datos))
    }

    @Test
    fun puntaje_restaSiYaTieneElCupon() {
        val datos = DatosRecomendacion(promocionesObtenidas = setOf(101))
        assertEquals(0, Recomendador.puntaje(usuario, promo(), local, 1.0, datos))
    }

    @Test
    fun fechasCercanas_filtraYOrdena() {
        val cercanas = Recomendador.fechasCercanas(
            listOf(fechaEn(10, Ocasion.FAMILIAR), fechaEn(40, Ocasion.PAREJA), fechaEn(2, Ocasion.AMIGOS))
        )
        assertEquals(listOf(2L, 10L), cercanas.map { it.second })
    }

    @Test
    fun calzaConOcasion_aceptaCualquiera() {
        assertTrue(Recomendador.calzaConOcasion(promo(Ocasion.CUALQUIERA), Ocasion.PAREJA))
        assertTrue(Recomendador.calzaConOcasion(promo(Ocasion.PAREJA), Ocasion.PAREJA))
        assertFalse(Recomendador.calzaConOcasion(promo(Ocasion.FAMILIAR), Ocasion.PAREJA))
    }
}
