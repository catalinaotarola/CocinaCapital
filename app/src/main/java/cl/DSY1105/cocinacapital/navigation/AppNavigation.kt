import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import cl.DSY1105.cocinacapital.navigation.Rutas
import cl.DSY1105.cocinacapital.ui.views.LoginScreen
import cl.DSY1105.cocinacapital.ui.views.PanelLocalScreen
import cl.DSY1105.cocinacapital.ui.views.PoliticaScreen
import cl.DSY1105.cocinacapital.ui.views.PreferenciasScreen
import cl.DSY1105.cocinacapital.ui.views.RecuperarScreen
import cl.DSY1105.cocinacapital.ui.views.RegistroScreen
import cl.DSY1105.cocinacapital.viewmodel.AuthViewModel

@Composable
fun AppNavigation(navController: NavHostController) {
    val authViewModel: AuthViewModel = viewModel()

    NavHost(navController = navController, startDestination = Rutas.LOGIN) {
        composable(Rutas.LOGIN) { LoginScreen(navController, authViewModel) }
        composable(Rutas.REGISTRO) { RegistroScreen(navController, authViewModel) }
        composable(Rutas.PREFERENCIAS) { PreferenciasScreen(navController, authViewModel) }
        composable(Rutas.INICIO) { InicioScreen(navController, authViewModel) }
        composable(Rutas.POLITICA) { PoliticaScreen(navController) }
        composable(Rutas.RECUPERAR) { RecuperarScreen(navController, authViewModel) }
        composable(Rutas.PANEL_LOCAL) { PanelLocalScreen(navController) }
    }
}