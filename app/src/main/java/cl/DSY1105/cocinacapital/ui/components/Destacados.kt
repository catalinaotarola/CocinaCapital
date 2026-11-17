package cl.DSY1105.cocinacapital.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cl.DSY1105.cocinacapital.data.model.Recomendacion
import cl.DSY1105.cocinacapital.viewmodel.UiState

@Composable
fun SeccionDestacados(
    estado: UiState<List<Recomendacion>>,
    alElegir: (localId: Int) -> Unit,
    alReintentar: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Lo mejor evaluado cerca de ti", style = MaterialTheme.typography.titleMedium)
        when (estado) {
            is UiState.Cargando -> {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    Spacer(Modifier.width(10.dp))
                    Text("Buscando recomendaciones de la comunidad…", style = MaterialTheme.typography.bodyMedium)
                }
            }
            is UiState.Error -> {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(estado.mensaje, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
                    TextButton(onClick = alReintentar) { Text("Reintentar") }
                }
            }
            is UiState.Exito -> {
                if (estado.datos.isEmpty()) {
                    Text("No hay locales evaluados dentro de tu radio.", style = MaterialTheme.typography.bodyMedium)
                } else {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(estado.datos, key = { it.localId }) { recomendacion ->
                            Card(onClick = { alElegir(recomendacion.localId) }, modifier = Modifier.width(220.dp)) {
                                Column(
                                    modifier = Modifier.padding(14.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(recomendacion.nombre, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    Estrellas(Math.round(recomendacion.promedio).toInt())
                                    Text(
                                        recomendacion.motivo,
                                        style = MaterialTheme.typography.bodySmall,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
