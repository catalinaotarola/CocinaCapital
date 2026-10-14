
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import cl.DSY1105.cocinacapital.navigation.Rutas
import cl.DSY1105.cocinacapital.viewmodel.AuthViewModel

@Composable
fun InicioScreen(navController: NavController, authViewModel: AuthViewModel) {
    val usuario = authViewModel.usuarioActual
    val gustos = usuario?.preferencias?.categorias.orEmpty()

    Column(
        modifier = Modifier.fillMaxSize().systemBarsPadding().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Hola, ${usuario?.nombre ?: ""}", style = MaterialTheme.typography.headlineMedium)
        Text(
            if (gustos.isEmpty()) "Aun no eliges tus gustos."
            else "Tus gustos: ${gustos.joinToString(", ")}",
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            "Aqui veras las promociones (laboratorio 9).",
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        OutlinedButton(onClick = {
            authViewModel.cerrarSesion()
            navController.navigate(Rutas.LOGIN) {
                popUpTo(Rutas.INICIO) { inclusive = true }
            }
        }) { Text("Cerrar sesión") }
    }
}
