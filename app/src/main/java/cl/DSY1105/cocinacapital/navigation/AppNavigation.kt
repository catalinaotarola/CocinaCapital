package cl.DSY1105.cocinacapital.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import cl.DSY1105.cocinacapital.ui.views.CercaScreen
import cl.DSY1105.cocinacapital.ui.views.CuponScreen
import cl.DSY1105.cocinacapital.ui.views.CuponesScreen
import cl.DSY1105.cocinacapital.ui.views.DetalleScreen
import cl.DSY1105.cocinacapital.ui.views.InicioScreen
import cl.DSY1105.cocinacapital.ui.views.LoginScreen
import cl.DSY1105.cocinacapital.ui.views.NuevaFechaScreen
import cl.DSY1105.cocinacapital.ui.views.PanelLocalScreen
import cl.DSY1105.cocinacapital.ui.views.PerfilScreen
import cl.DSY1105.cocinacapital.ui.views.PoliticaScreen
import cl.DSY1105.cocinacapital.ui.views.PreferenciasScreen
import cl.DSY1105.cocinacapital.ui.views.PromocionFormScreen
import cl.DSY1105.cocinacapital.ui.views.RecuperarScreen
import cl.DSY1105.cocinacapital.ui.views.RegistroScreen
import cl.DSY1105.cocinacapital.viewmodel.AuthViewModel
import cl.DSY1105.cocinacapital.viewmodel.CatalogoViewModel
import cl.DSY1105.cocinacapital.viewmodel.CuponesViewModel
import cl.DSY1105.cocinacapital.viewmodel.PanelViewModel
import cl.DSY1105.cocinacapital.viewmodel.PerfilViewModel

@Composable
fun AppNavigation(navController: NavHostController) {
    val authViewModel: AuthViewModel = viewModel()
    val catalogoViewModel: CatalogoViewModel = viewModel()
    val cuponesViewModel: CuponesViewModel = viewModel()
    val perfilViewModel: PerfilViewModel = viewModel()
    val panelViewModel: PanelViewModel = viewModel()

    if (!authViewModel.listo) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val rutaInicial = remember {
        if (authViewModel.usuarioActual != null) Rutas.INICIO else Rutas.LOGIN
    }

    NavHost(
        navController = navController,
        startDestination = rutaInicial,
        enterTransition = { fadeIn(tween(300)) + slideInHorizontally(tween(300)) { it / 4 } },
        exitTransition = { fadeOut(tween(300)) },
        popEnterTransition = { fadeIn(tween(300)) },
        popExitTransition = { fadeOut(tween(300)) + slideOutHorizontally(tween(300)) { it / 4 } }
    ) {
        composable(Rutas.LOGIN) { LoginScreen(navController, authViewModel) }
        composable(Rutas.REGISTRO) { RegistroScreen(navController, authViewModel) }
        composable(Rutas.PREFERENCIAS) { PreferenciasScreen(navController, authViewModel) }
        composable(Rutas.POLITICA) { PoliticaScreen(navController) }
        composable(Rutas.RECUPERAR) { RecuperarScreen(navController, authViewModel) }

        composable(Rutas.INICIO) { InicioScreen(navController, authViewModel, catalogoViewModel) }
        composable(Rutas.CERCA) { CercaScreen(navController, authViewModel, catalogoViewModel) }
        composable(Rutas.CUPONES) { CuponesScreen(navController, authViewModel, catalogoViewModel, cuponesViewModel) }
        composable(Rutas.PERFIL) { PerfilScreen(navController, authViewModel, perfilViewModel) }

        composable(
            route = Rutas.DETALLE,
            arguments = listOf(navArgument("localId") { type = NavType.IntType })
        ) { entrada ->
            val localId = entrada.arguments?.getInt("localId") ?: 0
            DetalleScreen(navController, localId, authViewModel, catalogoViewModel, cuponesViewModel)
        }
        composable(
            route = Rutas.CUPON,
            arguments = listOf(navArgument("cuponId") { type = NavType.LongType })
        ) { entrada ->
            val cuponId = entrada.arguments?.getLong("cuponId") ?: 0L
            CuponScreen(navController, cuponId, catalogoViewModel, cuponesViewModel)
        }
        composable(Rutas.NUEVA_FECHA) { NuevaFechaScreen(navController, authViewModel, perfilViewModel) }

        composable(Rutas.PANEL_LOCAL) { PanelLocalScreen(navController, panelViewModel) }
        composable(
            route = Rutas.PROMOCION_FORM,
            arguments = listOf(navArgument("promocionId") { type = NavType.IntType })
        ) { entrada ->
            val promocionId = entrada.arguments?.getInt("promocionId") ?: 0
            PromocionFormScreen(navController, promocionId, panelViewModel)
        }
    }
}
