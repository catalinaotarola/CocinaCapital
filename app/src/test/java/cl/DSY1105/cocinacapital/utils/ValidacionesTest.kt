package cl.DSY1105.cocinacapital.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ValidacionesTest {

    @Test
    fun esCorreoValido_aceptaCorreosBienEscritos() {
        assertTrue(esCorreoValido("sofia@duocuc.cl"))
        assertTrue(esCorreoValido("  sofia.r@gmail.com "))
    }

    @Test
    fun esCorreoValido_rechazaCorreosMalos() {
        assertFalse(esCorreoValido("sofia"))
        assertFalse(esCorreoValido("sofia@"))
        assertFalse(esCorreoValido("sofia@duoc"))
        assertFalse(esCorreoValido(""))
    }

    @Test
    fun validarResena_aceptaUnaResenaCompleta() {
        assertNull(validarResena(4, "  Muy rico todo  "))
    }

    @Test
    fun validarResena_explicaCadaError() {
        assertEquals("Elige de 1 a 5 estrellas.", validarResena(0, "Comentario largo"))
        assertEquals("Elige de 1 a 5 estrellas.", validarResena(6, "Comentario largo"))
        assertEquals("Cuéntanos un poco más: mínimo 10 caracteres.", validarResena(3, "  corto   "))
        assertEquals("Máximo 300 caracteres.", validarResena(3, "x".repeat(301)))
    }
}
