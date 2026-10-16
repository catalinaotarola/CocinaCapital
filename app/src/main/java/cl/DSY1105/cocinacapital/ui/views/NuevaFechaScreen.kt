package cl.DSY1105.cocinacapital.ui.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import cl.DSY1105.cocinacapital.data.model.Ocasion
import cl.DSY1105.cocinacapital.utils.diasHasta
import cl.DSY1105.cocinacapital.utils.textoDiaMes
import cl.DSY1105.cocinacapital.viewmodel.AuthViewModel
import cl.DSY1105.cocinacapital.viewmodel.PerfilViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun NuevaFechaScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    perfilViewModel: PerfilViewModel
) {
    val correo = authViewModel.usuarioActual?.correo ?: ""
    var nombre by rememberSaveable { mutableStateOf("") }
    var ocasion by rememberSaveable { mutableStateOf(Ocasion.PAREJA) }
    var dia by rememberSaveable { mutableIntStateOf(0) }
    var mes by rememberSaveable { mutableIntStateOf(0) }
    var diasAviso by rememberSaveable { mutableIntStateOf(3) }
    var error by rememberSaveable { mutableStateOf("") }
    var verCalendario by remember { mutableStateOf(false) }
    val estadoCalendario = rememberDatePickerState()
    val opcionesAviso = listOf(1 to "1 día antes", 3 to "3 días antes", 7 to "1 semana antes")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nueva fecha especial") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.Close, contentDescription = "Cerrar")
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
                .padding(horizontal = 24.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("¿Qué se celebra?") },
                placeholder = { Text("Ej.: Cumpleaños de mamá") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Text("Tipo de ocasión", style = MaterialTheme.typography.titleMedium)
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Ocasion.entries.filter { it != Ocasion.CUALQUIERA }.forEach { opcion ->
                    val elegida = ocasion == opcion
                    FilterChip(
                        selected = elegida,
                        onClick = { ocasion = opcion },
                        label = { Text(opcion.etiqueta) },
                        leadingIcon = if (elegida) {
                            { Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(18.dp)) }
                        } else {
                            null
                        }
                    )
                }
            }

            Text("Fecha", style = MaterialTheme.typography.titleMedium)
            OutlinedButton(
                onClick = { verCalendario = true },
                modifier = Modifier.fillMaxWidth().height(52.dp)
            ) {
                Icon(Icons.Filled.CalendarMonth, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(if (dia == 0) "Elegir fecha" else textoDiaMes(dia, mes))
            }
            Text(
                "Se repite todos los años.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text("Avísame", style = MaterialTheme.typography.titleMedium)
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                opcionesAviso.forEach { (dias, texto) ->
                    FilterChip(
                        selected = diasAviso == dias,
                        onClick = { diasAviso = dias },
                        label = { Text(texto) }
                    )
                }
            }

            if (dia != 0) {
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.NotificationsActive, contentDescription = null)
                        Spacer(Modifier.width(12.dp))
                        Text(textoVistaPrevia(dia, mes, diasAviso, ocasion))
                    }
                }
            }

            if (error.isNotEmpty()) {
                Text(error, color = MaterialTheme.colorScheme.error)
            }

            Button(
                onClick = {
                    val problema = perfilViewModel.agregarFecha(correo, nombre, ocasion, dia, mes, diasAviso)
                    if (problema == null) navController.popBackStack() else error = problema
                },
                modifier = Modifier.fillMaxWidth().height(52.dp)
            ) { Text("Guardar fecha") }
        }
    }

    if (verCalendario) {
        DatePickerDialog(
            onDismissRequest = { verCalendario = false },
            confirmButton = {
                TextButton(onClick = {
                    estadoCalendario.selectedDateMillis?.let { millis ->
                        val elegida = Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDate()
                        dia = elegida.dayOfMonth
                        mes = elegida.monthValue
                    }
                    verCalendario = false
                }) { Text("Aceptar") }
            },
            dismissButton = {
                TextButton(onClick = { verCalendario = false }) { Text("Cancelar") }
            }
        ) {
            DatePicker(state = estadoCalendario)
        }
    }
}

private fun textoVistaPrevia(dia: Int, mes: Int, diasAviso: Int, ocasion: Ocasion): String {
    val faltan = diasHasta(dia, mes)
    val comoCelebrar = when (ocasion) {
        Ocasion.PAREJA -> "en pareja"
        Ocasion.FAMILIAR -> "en familia"
        Ocasion.AMIGOS -> "con amigos"
        else -> "tu día"
    }
    return if (faltan <= diasAviso) {
        "La fecha está cerca: desde hoy verás lugares con promociones para celebrar $comoCelebrar."
    } else {
        val aviso = LocalDate.now().plusDays(faltan - diasAviso)
        "El ${textoDiaMes(aviso.dayOfMonth, aviso.monthValue)} te sugeriremos lugares con promociones para celebrar $comoCelebrar."
    }
}
