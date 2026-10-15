package cl.DSY1105.cocinacapital.ui.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import cl.DSY1105.cocinacapital.navigation.Rutas
import cl.DSY1105.cocinacapital.ui.components.BarraInferior
import cl.DSY1105.cocinacapital.viewmodel.AuthViewModel

@Composable
fun PerfilScreen(navController: NavController, authViewModel: AuthViewModel) {
    val usuario = authViewModel.usuarioActual
    Scaffold(bottomBar = { BarraInferior(navController, Rutas.PERFIL) }) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(usuario?.nombre ?: "", style = MaterialTheme.typography.headlineMedium)
            Text(usuario?.correo ?: "", color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("El perfil completo se construye en el laboratorio 12.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            OutlinedButton(onClick = {
                authViewModel.cerrarSesion()
                navController.navigate(Rutas.LOGIN) {
                    popUpTo(navController.graph.id) { inclusive = true }
                }
            }) { Text("Cerrar sesión") }
        }
    }
}
