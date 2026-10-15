package cl.DSY1105.cocinacapital.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import cl.DSY1105.cocinacapital.navigation.Rutas

private data class ItemBarra(val ruta: String, val etiqueta: String, val icono: ImageVector)

private val itemsBarra = listOf(
    ItemBarra(Rutas.INICIO, "Inicio", Icons.Filled.Home),
    ItemBarra(Rutas.CERCA, "Cerca", Icons.Filled.Place),
    ItemBarra(Rutas.CUPONES, "Cupones", Icons.Filled.ConfirmationNumber),
    ItemBarra(Rutas.PERFIL, "Perfil", Icons.Filled.Person)
)

@Composable
fun BarraInferior(navController: NavController, rutaActual: String) {
    NavigationBar {
        itemsBarra.forEach { item ->
            NavigationBarItem(
                selected = item.ruta == rutaActual,
                onClick = {
                    if (item.ruta != rutaActual) {
                        navController.navigate(item.ruta) {
                            popUpTo(Rutas.INICIO) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = { Icon(item.icono, contentDescription = null) },
                label = { Text(item.etiqueta) }
            )
        }
    }
}
