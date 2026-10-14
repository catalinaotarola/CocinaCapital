package cl.DSY1105.cocinacapital.ui.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

// Borrador de la política: título de cada sección y su texto
private val seccionesPolitica = listOf(
    "Qué datos pedimos" to
            "Tu nombre, tu correo, tu contraseña, tus gustos gastronómicos, tus fechas especiales y, " +
            "solo si lo autorizas, tu ubicación mientras usas la app.",
    "Para qué los usamos" to
            "Para mostrarte promociones según tus gustos y tu ubicación, recordarte tus fechas " +
            "especiales y administrar tus cupones.",
    "Qué no hacemos" to
            "No vendemos tus datos ni los compartimos con terceros para publicidad. Cuando canjeas un " +
            "cupón, el local solo ve el código y tu nombre.",
    "Tus derechos" to
            "Puedes acceder a tus datos, corregirlos, eliminarlos y oponerte a recibir sugerencias, " +
            "según la Ley 19.628 y la Ley 21.719. Puedes eliminar tu cuenta desde tu perfil.",
    "Permisos del teléfono" to
            "Pedimos la ubicación y las notificaciones solo cuando las necesitas, y puedes " +
            "desactivarlas cuando quieras."
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PoliticaScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Política de privacidad") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            seccionesPolitica.forEach { (titulo, texto) ->
                Text(titulo, style = MaterialTheme.typography.titleMedium)
                Text(texto, style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}