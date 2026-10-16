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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
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
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import cl.DSY1105.cocinacapital.data.model.Ocasion
import cl.DSY1105.cocinacapital.utils.formatearFecha
import cl.DSY1105.cocinacapital.viewmodel.PanelViewModel
import java.time.Instant
import java.time.ZoneOffset

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun PromocionFormScreen(navController: NavController, promocionId: Int, panelViewModel: PanelViewModel) {
    val promociones by panelViewModel.promociones.collectAsState()
    val original = promociones.find { it.id == promocionId }
    val clave = original?.id

    var titulo by rememberSaveable(clave) { mutableStateOf(original?.titulo ?: "") }
    var etiqueta by rememberSaveable(clave) { mutableStateOf(original?.etiqueta ?: "") }
    var condiciones by rememberSaveable(clave) { mutableStateOf(original?.condiciones ?: "") }
    var horaInicio by rememberSaveable(clave) { mutableStateOf(original?.horaInicio?.toString() ?: "") }
    var horaFin by rememberSaveable(clave) { mutableStateOf(original?.horaFin?.toString() ?: "") }
    var fechaTermino by rememberSaveable(clave) { mutableStateOf(original?.fechaTermino ?: "") }
    var cupos by rememberSaveable(clave) { mutableStateOf(original?.cuposTotales?.toString() ?: "") }
    var ocasion by rememberSaveable(clave) { mutableStateOf(original?.ocasion ?: Ocasion.CUALQUIERA) }
    var bajaDemanda by rememberSaveable(clave) { mutableStateOf(original?.bajaDemanda ?: true) }
    var error by remember { mutableStateOf("") }
    var verCalendario by remember { mutableStateOf(false) }
    val estadoCalendario = rememberDatePickerState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (original == null) "Nueva promoción" else "Editar promoción") },
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = titulo, onValueChange = { titulo = it },
                label = { Text("Título") },
                placeholder = { Text("20% de descuento en platos de fondo") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = etiqueta, onValueChange = { etiqueta = it },
                label = { Text("Etiqueta de la insignia") },
                placeholder = { Text("-20%") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = condiciones, onValueChange = { condiciones = it },
                label = { Text("Condiciones") },
                placeholder = { Text("Lun a jue · 15:00 a 18:00") },
                modifier = Modifier.fillMaxWidth()
            )

            Text("Horario válido", style = MaterialTheme.typography.titleMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = horaInicio,
                    onValueChange = { horaInicio = it.filter { c -> c.isDigit() }.take(2) },
                    label = { Text("Desde (hora)") },
                    placeholder = { Text("15") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = horaFin,
                    onValueChange = { horaFin = it.filter { c -> c.isDigit() }.take(2) },
                    label = { Text("Hasta (hora)") },
                    placeholder = { Text("18") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
            }

            OutlinedButton(
                onClick = { verCalendario = true },
                modifier = Modifier.fillMaxWidth().height(52.dp)
            ) {
                Icon(Icons.Filled.CalendarMonth, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(if (fechaTermino.isBlank()) "Elegir fecha de término" else "Válida hasta el ${formatearFecha(fechaTermino)}")
            }
            OutlinedTextField(
                value = cupos,
                onValueChange = { cupos = it.filter { c -> c.isDigit() }.take(4) },
                label = { Text("Cupos totales") },
                placeholder = { Text("30") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Text("Ideal para", style = MaterialTheme.typography.titleMedium)
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Ocasion.entries.forEach { opcion ->
                    FilterChip(
                        selected = ocasion == opcion,
                        onClick = { ocasion = opcion },
                        label = { Text(opcion.etiqueta) }
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .toggleable(value = bajaDemanda, role = Role.Switch, onValueChange = { bajaDemanda = it })
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Horario de baja demanda", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
                Switch(checked = bajaDemanda, onCheckedChange = null)
            }

            if (error.isNotEmpty()) {
                Text(error, color = MaterialTheme.colorScheme.error)
            }

            Button(
                onClick = {
                    val problema = panelViewModel.guardarPromocion(
                        original, titulo, etiqueta, condiciones,
                        horaInicio, horaFin, fechaTermino, cupos, ocasion, bajaDemanda
                    )
                    if (problema == null) navController.popBackStack() else error = problema
                },
                modifier = Modifier.fillMaxWidth().height(52.dp)
            ) { Text("Guardar promoción") }
        }
    }

    if (verCalendario) {
        DatePickerDialog(
            onDismissRequest = { verCalendario = false },
            confirmButton = {
                TextButton(onClick = {
                    estadoCalendario.selectedDateMillis?.let { millis ->
                        fechaTermino = Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDate().toString()
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
