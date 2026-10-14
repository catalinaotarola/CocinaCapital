package cl.DSY1105.cocinacapital.ui.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import cl.DSY1105.cocinacapital.datamodel.OpcionesPreferencias
import cl.DSY1105.cocinacapital.datamodel.Preferencias
import cl.DSY1105.cocinacapital.navigation.Rutas
import cl.DSY1105.cocinacapital.viewmodel.AuthViewModel
import kotlin.math.roundToInt

@Composable
fun PreferenciasScreen(navController: NavController, authViewModel: AuthViewModel) {
    // Partimos con lo que el usuario ya tenía guardado (vacío si es nuevo)
    val guardadas = authViewModel.usuarioActual?.preferencias ?: Preferencias()
    var categorias by remember { mutableStateOf(guardadas.categorias) }
    var presupuesto by remember { mutableStateOf(guardadas.presupuesto) }
    var restricciones by remember { mutableStateOf(guardadas.restricciones) }
    var radioKm by remember { mutableFloatStateOf(guardadas.radioKm.toFloat()) }

    fun irAInicio() {
        navController.navigate(Rutas.INICIO) {
            popUpTo(Rutas.PREFERENCIAS) { inclusive = true }
        }
    }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Cuéntanos qué te gusta", style = MaterialTheme.typography.headlineMedium)
            Text(
                "Con esto te mostraremos promociones que de verdad te interesen.",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text("Tipos de comida", style = MaterialTheme.typography.titleMedium)
            GrupoDeChips(
                opciones = OpcionesPreferencias.categorias,
                seleccionadas = categorias,
                alTocar = { opcion ->
                    categorias = if (opcion in categorias) categorias - opcion else categorias + opcion
                }
            )

            Text("Presupuesto por persona", style = MaterialTheme.typography.titleMedium)
            GrupoDeChips(
                opciones = OpcionesPreferencias.presupuestos,
                seleccionadas = setOf(presupuesto),
                alTocar = { opcion -> presupuesto = opcion }
            )

            Text("Restricciones alimentarias", style = MaterialTheme.typography.titleMedium)
            GrupoDeChips(
                opciones = OpcionesPreferencias.restricciones,
                seleccionadas = restricciones,
                alTocar = { opcion ->
                    restricciones = if (opcion in restricciones) restricciones - opcion else restricciones + opcion
                }
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Distancia máxima", style = MaterialTheme.typography.titleMedium)
                Text("${radioKm.roundToInt()} km", style = MaterialTheme.typography.titleMedium)
            }
            Slider(
                value = radioKm,
                onValueChange = { radioKm = it },
                valueRange = 1f..10f,
                steps = 8
            )

            Button(
                onClick = {
                    authViewModel.guardarPreferencias(
                        Preferencias(categorias, presupuesto, restricciones, radioKm.roundToInt())
                    )
                    irAInicio()
                },
                modifier = Modifier.fillMaxWidth().height(52.dp)
            ) { Text("Guardar y continuar") }

            TextButton(
                onClick = { irAInicio() },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) { Text("Omitir por ahora") }
        }
    }
}

// Componente reutilizable: una fila de chips que salta de línea si no cabe
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun GrupoDeChips(
    opciones: List<String>,
    seleccionadas: Set<String>,
    alTocar: (String) -> Unit
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        opciones.forEach { opcion ->
            val elegida = opcion in seleccionadas
            FilterChip(
                selected = elegida,
                onClick = { alTocar(opcion) },
                label = { Text(opcion) },
                leadingIcon = if (elegida) {
                    { Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(18.dp)) }
                } else {
                    null
                }
            )
        }
    }
}
