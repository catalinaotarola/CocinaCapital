package cl.DSY1105.cocinacapital.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cl.DSY1105.cocinacapital.data.model.Resena
import cl.DSY1105.cocinacapital.utils.MAXIMO_COMENTARIO
import cl.DSY1105.cocinacapital.utils.formatearFecha
import cl.DSY1105.cocinacapital.utils.esFechaValida

private val COLOR_ESTRELLA = Color(0xFFF2A900)

@Composable
fun Estrellas(puntaje: Int, tamano: Dp = 16.dp) {
    Row {
        (1..5).forEach { numero ->
            Icon(
                imageVector = if (numero <= puntaje) Icons.Filled.Star else Icons.Filled.StarBorder,
                contentDescription = null,
                tint = COLOR_ESTRELLA,
                modifier = Modifier.size(tamano)
            )
        }
    }
}

@Composable
fun TarjetaResena(resena: Resena, alEditar: () -> Unit, alEliminar: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (resena.esMia) MaterialTheme.colorScheme.secondaryContainer
            else MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    if (resena.esMia) "${resena.autor} (tú)" else resena.autor,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Estrellas(resena.puntaje)
            }
            Text(resena.comentario, style = MaterialTheme.typography.bodyMedium)
            if (esFechaValida(resena.fecha)) {
                Text(
                    formatearFecha(resena.fecha),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (resena.esMia) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton(onClick = alEditar) { Text("Editar") }
                    TextButton(onClick = alEliminar) { Text("Eliminar", color = MaterialTheme.colorScheme.error) }
                }
            }
        }
    }
}

@Composable
fun DialogoResena(
    inicial: Resena?,
    enviando: Boolean,
    error: String,
    alGuardar: (puntaje: Int, comentario: String) -> Unit,
    alCerrar: () -> Unit
) {
    var puntaje by rememberSaveable(inicial?.id) { mutableIntStateOf(inicial?.puntaje ?: 0) }
    var comentario by rememberSaveable(inicial?.id) { mutableStateOf(inicial?.comentario ?: "") }

    AlertDialog(
        onDismissRequest = { if (!enviando) alCerrar() },
        title = { Text(if (inicial == null) "Escribe tu reseña" else "Edita tu reseña") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("¿Cuántas estrellas le das?")
                Row {
                    (1..5).forEach { numero ->
                        IconButton(onClick = { puntaje = numero }) {
                            Icon(
                                imageVector = if (numero <= puntaje) Icons.Filled.Star else Icons.Filled.StarBorder,
                                contentDescription = "$numero estrellas",
                                tint = COLOR_ESTRELLA,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }
                OutlinedTextField(
                    value = comentario,
                    onValueChange = { if (it.length <= MAXIMO_COMENTARIO) comentario = it },
                    label = { Text("Tu comentario") },
                    supportingText = { Text("${comentario.length}/$MAXIMO_COMENTARIO") },
                    isError = error.isNotEmpty(),
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )
                if (error.isNotEmpty()) {
                    Text(error, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { alGuardar(puntaje, comentario) }, enabled = !enviando) {
                Text(if (enviando) "Enviando…" else "Publicar")
            }
        },
        dismissButton = {
            TextButton(onClick = alCerrar, enabled = !enviando) { Text("Cancelar") }
        }
    )
}
