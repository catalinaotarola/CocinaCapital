package cl.DSY1105.cocinacapital.viewmodel

import cl.DSY1105.cocinacapital.data.model.Recomendacion
import cl.DSY1105.cocinacapital.data.model.Resena
import cl.DSY1105.cocinacapital.data.repository.ResenasRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import java.net.ConnectException

class ResenasViewModelTest {

    @get:Rule
    val regla = ReglaDispatcherPrincipal()

    private val correo = "sofia@duocuc.cl"
    private val repo = mockk<ResenasRepository>()
    private val resena = Resena(20, 1, "Sofía", 5, "Muy rico todo", "2026-11-10", true)
    private val sinConexion = "No pudimos conectar con el servidor. ¿Están encendidos los microservicios?"

    @Test
    fun cargarResenas_conRespuestaQuedaEnExito() {
        coEvery { repo.resenasDe(1, correo) } returns listOf(resena)
        val vm = ResenasViewModel(repo)

        vm.cargarResenas(1, correo)

        assertEquals(UiState.Exito(listOf(resena)), vm.resenas.value)
    }

    @Test
    fun cargarResenas_conErrorYReintentar() {
        coEvery { repo.resenasDe(1, correo) } throws ConnectException()
        val vm = ResenasViewModel(repo)

        vm.cargarResenas(1, correo)
        assertEquals(UiState.Error(sinConexion), vm.resenas.value)

        coEvery { repo.resenasDe(1, correo) } returns emptyList()
        vm.reintentarResenas()
        assertEquals(UiState.Exito(emptyList<Resena>()), vm.resenas.value)
    }

    @Test
    fun guardar_resenaInvalidaNoLlamaAlServidor() {
        val vm = ResenasViewModel(repo)
        var problema: String? = null

        vm.guardar(1, correo, "Sofía", null, 0, "Muy rico todo") { problema = it }

        assertEquals("Elige de 1 a 5 estrellas.", problema)
        coVerify(exactly = 0) { repo.publicar(1, correo, "Sofía", 0, "Muy rico todo") }
    }

    @Test
    fun guardar_nuevaPublicaYRecarga() {
        coEvery { repo.publicar(1, correo, "Sofía", 5, "Muy rico todo") } returns resena
        coEvery { repo.resenasDe(1, correo) } returns listOf(resena)
        val vm = ResenasViewModel(repo)
        var llamado = false
        var problema: String? = "sin respuesta"

        vm.guardar(1, correo, "Sofía", null, 5, "Muy rico todo") { llamado = true; problema = it }

        assertEquals(true, llamado)
        assertNull(problema)
        assertFalse(vm.enviando)
        assertEquals(UiState.Exito(listOf(resena)), vm.resenas.value)
    }

    @Test
    fun guardar_editandoUsaEditar() {
        val editada = resena.copy(puntaje = 3, comentario = "Ahora regular")
        coEvery { repo.editar(20, correo, 3, "Ahora regular") } returns editada
        coEvery { repo.resenasDe(1, correo) } returns listOf(editada)
        val vm = ResenasViewModel(repo)
        var problema: String? = "sin respuesta"

        vm.guardar(1, correo, "Sofía", resena, 3, "Ahora regular") { problema = it }

        assertNull(problema)
        coVerify(exactly = 1) { repo.editar(20, correo, 3, "Ahora regular") }
    }

    @Test
    fun guardar_conErrorDevuelveElMensaje() {
        coEvery { repo.publicar(1, correo, "Sofía", 5, "Muy rico todo") } throws ConnectException()
        val vm = ResenasViewModel(repo)
        var problema: String? = null

        vm.guardar(1, correo, "Sofía", null, 5, "Muy rico todo") { problema = it }

        assertEquals(sinConexion, problema)
        assertFalse(vm.enviando)
    }

    @Test
    fun eliminar_borraYRecarga() {
        coEvery { repo.eliminar(20, correo) } returns Unit
        coEvery { repo.resenasDe(1, correo) } returns emptyList()
        val vm = ResenasViewModel(repo)
        var problema: String? = "sin respuesta"

        vm.eliminar(resena, correo) { problema = it }

        assertNull(problema)
        assertEquals(UiState.Exito(emptyList<Resena>()), vm.resenas.value)
    }

    @Test
    fun eliminar_conErrorDevuelveElMensaje() {
        coEvery { repo.eliminar(20, correo) } throws ConnectException()
        val vm = ResenasViewModel(repo)
        var problema: String? = null

        vm.eliminar(resena, correo) { problema = it }

        assertEquals(sinConexion, problema)
    }

    @Test
    fun cargarDestacados_noRepiteLaMismaConsulta() {
        val lista = listOf(Recomendacion(7, "Pizzería Brasa", "Pizza", 4.5, 2, "★ 4,5"))
        coEvery { repo.recomendaciones(-32.9, -71.5, 3, setOf("Pizza"), 5) } returns lista
        val vm = ResenasViewModel(repo)

        vm.cargarDestacados(-32.9, -71.5, 3, setOf("Pizza"))
        vm.cargarDestacados(-32.9, -71.5, 3, setOf("Pizza"))

        assertEquals(UiState.Exito(lista), vm.destacados.value)
        coVerify(exactly = 1) { repo.recomendaciones(-32.9, -71.5, 3, setOf("Pizza"), 5) }
    }

    @Test
    fun cargarDestacados_conErrorYReintentar() {
        coEvery { repo.recomendaciones(-32.9, -71.5, 3, emptySet(), 5) } throws ConnectException()
        val vm = ResenasViewModel(repo)

        vm.cargarDestacados(-32.9, -71.5, 3, emptySet())
        assertEquals(UiState.Error(sinConexion), vm.destacados.value)

        coEvery { repo.recomendaciones(-32.9, -71.5, 3, emptySet(), 5) } returns emptyList()
        vm.reintentarDestacados()
        assertEquals(UiState.Exito(emptyList<Recomendacion>()), vm.destacados.value)
    }
}
