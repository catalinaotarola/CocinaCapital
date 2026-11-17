package cl.DSY1105.cocinacapital.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class IncentivosTest {

    private val lunes = LocalDate.of(2026, 11, 9)

    @Test
    fun porcentaje_segunDiasQueFaltan() {
        assertEquals(0, Incentivos.porcentaje(20))
        assertEquals(50, Incentivos.porcentaje(15))
        assertEquals(50, Incentivos.porcentaje(9))
        assertEquals(35, Incentivos.porcentaje(8))
        assertEquals(35, Incentivos.porcentaje(2))
        assertEquals(20, Incentivos.porcentaje(1))
        assertEquals(0, Incentivos.porcentaje(0))
    }

    @Test
    fun esDiaDeAviso_soloQuince_Ocho_Uno() {
        assertTrue(Incentivos.esDiaDeAviso(15))
        assertTrue(Incentivos.esDiaDeAviso(8))
        assertTrue(Incentivos.esDiaDeAviso(1))
        assertFalse(Incentivos.esDiaDeAviso(5))
    }

    @Test
    fun etapa_segunElDia() {
        assertEquals("Te informamos", Incentivos.etapa(15))
        assertEquals("Te recordamos", Incentivos.etapa(8))
        assertEquals("Última oportunidad", Incentivos.etapa(1))
        assertEquals("Se acerca tu fecha", Incentivos.etapa(4))
    }

    @Test
    fun calendario_conTiempoSuficienteTieneTresAvisos() {
        val avisos = Incentivos.calendario(1, 12, lunes)
        assertEquals(
            listOf("2026-11-16:50", "2026-11-23:35", "2026-11-30:20"),
            avisos.map { "${it.fecha}:${it.porcentaje}" }
        )
    }

    @Test
    fun calendario_conPocoTiempoSoloQuedaElUltimo() {
        assertEquals(listOf(1), Incentivos.calendario(14, 11, lunes).map { it.diasAntes })
        assertEquals(0, Incentivos.calendario(9, 11, lunes).size)
    }

    @Test
    fun calendario_cruzaElAnio() {
        val avisos = Incentivos.calendario(5, 1, LocalDate.of(2026, 12, 28))
        assertEquals(listOf("2026-12-28", "2027-01-04"), avisos.map { it.fecha.toString() })
    }

    @Test
    fun maxAvisosPorDia_seAdaptaAlUso() {
        assertEquals(1, Incentivos.maxAvisosPorDia(0))
        assertEquals(2, Incentivos.maxAvisosPorDia(5))
        assertEquals(3, Incentivos.maxAvisosPorDia(12))
    }
}
