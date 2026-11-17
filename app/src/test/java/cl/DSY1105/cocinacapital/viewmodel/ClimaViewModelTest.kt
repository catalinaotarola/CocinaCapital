package cl.DSY1105.cocinacapital.viewmodel

import cl.DSY1105.cocinacapital.data.model.Clima
import cl.DSY1105.cocinacapital.data.repository.ClimaRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import java.net.UnknownHostException

class ClimaViewModelTest {

    @get:Rule
    val regla = ReglaDispatcherPrincipal()

    private val repo = mockk<ClimaRepository>()
    private val clima = Clima(19, "Despejado", "Buen día para salir a comer cerca de ti.")

    @Test
    fun estadoInicial_esCargando() {
        assertEquals(UiState.Cargando, ClimaViewModel(repo).estado.value)
    }

    @Test
    fun cargar_conRespuestaQuedaEnExito() {
        coEvery { repo.climaActual(-32.9, -71.5) } returns clima
        val vm = ClimaViewModel(repo)

        vm.cargar(-32.9, -71.5)

        assertEquals(UiState.Exito(clima), vm.estado.value)
    }

    @Test
    fun cargar_mismaUbicacionNoVuelveAPedir() {
        coEvery { repo.climaActual(-32.9, -71.5) } returns clima
        val vm = ClimaViewModel(repo)

        vm.cargar(-32.9, -71.5)
        vm.cargar(-32.9, -71.5)

        coVerify(exactly = 1) { repo.climaActual(-32.9, -71.5) }
    }

    @Test
    fun cargar_conErrorMuestraMensaje() {
        coEvery { repo.climaActual(-32.9, -71.5) } throws UnknownHostException()
        val vm = ClimaViewModel(repo)

        vm.cargar(-32.9, -71.5)

        assertEquals(UiState.Error("Sin conexión a internet. Revisa tu red e inténtalo de nuevo."), vm.estado.value)
    }

    @Test
    fun reintentar_vuelveAPedirLaUltimaUbicacion() {
        coEvery { repo.climaActual(-32.9, -71.5) } returns clima
        val vm = ClimaViewModel(repo)

        vm.reintentar()
        vm.cargar(-32.9, -71.5)
        vm.reintentar()

        coVerify(exactly = 2) { repo.climaActual(-32.9, -71.5) }
        assertEquals(UiState.Exito(clima), vm.estado.value)
    }
}
