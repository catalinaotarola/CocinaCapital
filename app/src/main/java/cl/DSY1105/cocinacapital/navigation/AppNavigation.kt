package cl.DSY1105.cocinacapital.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import cl.DSY1105.cocinacapital.ui.views.EnConstruccionScreen
import cl.DSY1105.cocinacapital.ui.views.InicioScreen
import cl.DSY1105.cocinacapital.ui.views.LoginScreen
import cl.DSY1105.cocinacapital.ui.views.PanelLocalScreen
import cl.DSY1105.cocinacapital.ui.views.PerfilScreen
import cl.DSY1105.cocinacapital.ui.views.PoliticaScreen
import cl.DSY1105.cocinacapital.ui.views.PreferenciasScreen
import cl.DSY1105.cocinacapital.ui.views.RecuperarScreen
import cl.DSY1105.cocinacapital.ui.views.RegistroScreen
import cl.DSY1105.cocinacapital.viewmodel.AuthViewModel
import cl.DSY1105.cocinacapital.viewmodel.CatalogoViewModel

@Composable
fun AppNavigation(navController: NavHostController) {
    val authViewModel: AuthViewModel = viewModel()
    val catalogoViewModel: CatalogoViewModel = viewModel()

    if (!authViewModel.listo) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val rutaInicial = remember {
        if (authViewModel.usuarioActual != null) Rutas.INICIO else Rutas.LOGIN
    }

    NavHost(navController = navController, startDestination = rutaInicial) {
        composable(Rutas.LOGIN) { LoginScreen(navController, authViewModel) }
        composable(Rutas.REGISTRO) { RegistroScreen(navController, authViewModel) }
        composable(Rutas.PREFERENCIAS) { PreferenciasScreen(navController, authViewModel) }
        composable(Rutas.POLITICA) { PoliticaScreen(navController) }
        composable(Rutas.RECUPERAR) { RecuperarScreen(navController, authViewModel) }
        composable(Rutas.PANEL_LOCAL) { PanelLocalScreen(navController) }

        composable(Rutas.INICIO) { InicioScreen(navController, authViewModel, catalogoViewModel) }
        composable(Rutas.CERCA) { EnConstruccionScreen(navController, "Cerca de ti", 14, Rutas.CERCA) }
        composable(Rutas.CUPONES) { EnConstruccionScreen(navController, "Mis cupones", 11, Rutas.CUPONES) }
        composable(Rutas.PERFIL) { PerfilScreen(navController, authViewModel) }

        composable(Rutas.DETALLE) { EnConstruccionScreen(navController, "Detalle del local", 10, null) }
        composable(Rutas.CUPON) { EnConstruccionScreen(navController, "Tu cupón", 11, null) }
        composable(Rutas.NUEVA_FECHA) { EnConstruccionScreen(navController, "Nueva fecha especial", 12, null) }
    }
}
