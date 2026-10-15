package cl.DSY1105.cocinacapital.ui.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import cl.DSY1105.cocinacapital.navigation.Rutas
import cl.DSY1105.cocinacapital.ui.components.BarraInferior
import cl.DSY1105.cocinacapital.ui.components.TarjetaPromocion
import cl.DSY1105.cocinacapital.utils.textoCuando
import cl.DSY1105.cocinacapital.viewmodel.AuthViewModel
import cl.DSY1105.cocinacapital.viewmodel.CatalogoViewModel
import cl.DSY1105.cocinacapital.viewmodel.Sugerencia

@Composable
fun InicioScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    catalogoViewModel: CatalogoViewModel
) {
    val usuario = authViewModel.usuarioActual
    val locales by catalogoViewModel.locales.collectAsState()
    val promociones by catalogoViewModel.promociones.collectAsState()
    val foco = LocalFocusManager.current

    LaunchedEffect(usuario?.correo) {
        usuario?.let { catalogoViewModel.refrescarDatosUsuario(it.correo) }
    }

    val tarjetas = catalogoViewModel.tarjetas(usuario, locales, promociones)
    val sugerencia = catalogoViewModel.sugerencia(usuario, tarjetas)
    val categorias = listOf("Todos") + locales.map { it.categoria }.distinct()

    Scaffold(bottomBar = { BarraInferior(navController, Rutas.INICIO) }) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Filled.Place, contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            " Concón · hasta ${usuario?.preferencias?.radioKm ?: 3} km",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text("Hola, ${usuario?.nombre ?: ""}", style = MaterialTheme.typography.headlineMedium)
                }
            }

            item {
                OutlinedTextField(
                    value = catalogoViewModel.busqueda,
                    onValueChange = { catalogoViewModel.cambiarBusqueda(it) },
                    placeholder = { Text("Busca un local o tipo de comida") },
                    leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                    trailingIcon = {
                        if (catalogoViewModel.busqueda.isNotEmpty()) {
                            IconButton(onClick = { catalogoViewModel.cambiarBusqueda("") }) {
                                Icon(Icons.Filled.Close, contentDescription = "Borrar búsqueda")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(28.dp),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = {
                        usuario?.let { catalogoViewModel.registrarBusqueda(it.correo) }
                        foco.clearFocus()
                    }),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(categorias) { opcion ->
                        FilterChip(
                            selected = catalogoViewModel.categoria == opcion,
                            onClick = { catalogoViewModel.cambiarCategoria(opcion) },
                            label = { Text(opcion) }
                        )
                    }
                }
            }

            catalogoViewModel.mensajeConexion?.let { mensaje ->
                item {
                    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)) {
                        Text(mensaje, modifier = Modifier.padding(16.dp))
                    }
                }
            }

            sugerencia?.let { s ->
                item {
                    TarjetaSugerencia(s) { localId -> navController.navigate(Rutas.detalle(localId)) }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Promociones para ti", style = MaterialTheme.typography.titleMedium)
                    TextButton(onClick = { navController.navigate(Rutas.CERCA) { launchSingleTop = true } }) {
                        Text("Ver mapa")
                    }
                }
            }
            if (tarjetas.isEmpty()) {
                item {
                    Text("No encontramos promociones con esos filtros.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            items(tarjetas, key = { it.promocion.id }) { tarjeta ->
                TarjetaPromocion(tarjeta) { navController.navigate(Rutas.detalle(tarjeta.local.id)) }
            }
        }
    }
}

@Composable
private fun TarjetaSugerencia(sugerencia: Sugerencia, alElegir: (Int) -> Unit) {
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.CalendarMonth, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("SUGERENCIA PARA TI", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
            }
            Text(
                "Tu ${sugerencia.fecha.nombre.lowercase()} ${textoCuando(sugerencia.diasFaltantes)}",
                style = MaterialTheme.typography.titleMedium
            )
            Text("Elegimos estos lugares para la ocasión:", style = MaterialTheme.typography.bodyMedium)
            sugerencia.opciones.forEach { opcion ->
                TextButton(onClick = { alElegir(opcion.local.id) }) {
                    Text("${opcion.local.nombre}: ${opcion.promocion.titulo}")
                }
            }
        }
    }
}
