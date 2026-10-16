package cl.DSY1105.cocinacapital.ui.views

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import cl.DSY1105.cocinacapital.data.model.EstadoCupon
import cl.DSY1105.cocinacapital.data.model.Promocion
import cl.DSY1105.cocinacapital.navigation.Rutas
import cl.DSY1105.cocinacapital.utils.estaVencida
import cl.DSY1105.cocinacapital.utils.formatearFecha
import cl.DSY1105.cocinacapital.viewmodel.PanelViewModel
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PanelLocalScreen(navController: NavController, panelViewModel: PanelViewModel) {
    val locales by panelViewModel.locales.collectAsState()
    val promociones by panelViewModel.promociones.collectAsState()
    val localId = panelViewModel.localId
    val local = locales.find { it.id == localId }
    val promosDelLocal = promociones.filter { it.localId == localId }

    val cupones by remember(localId) { panelViewModel.cuponesDelLocal(localId) }.collectAsState(initial = emptyList())
    val vistas by remember(localId) { panelViewModel.vistasDelLocal(localId) }.collectAsState(initial = 0)

    var codigo by rememberSaveable { mutableStateOf("") }
    var resultado by remember { mutableStateOf<Pair<Boolean, String>?>(null) }

    fun validar(texto: String) {
        panelViewModel.validar(texto) { exito, mensaje -> resultado = exito to mensaje }
    }

    val lanzadorEscaner = rememberLauncherForActivityResult(ScanContract()) { lectura ->
        val leido = lectura.contents
        if (leido != null) {
            codigo = leido
            validar(leido)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Panel del local", style = MaterialTheme.typography.labelLarge)
                        Text(local?.nombre ?: "", style = MaterialTheme.typography.titleLarge)
                    }
                },
                actions = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Salir del panel")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Elige tu local", style = MaterialTheme.typography.titleMedium)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(locales, key = { it.id }) { opcion ->
                            FilterChip(
                                selected = opcion.id == localId,
                                onClick = {
                                    panelViewModel.elegirLocal(opcion.id)
                                    resultado = null
                                },
                                label = { Text(opcion.nombre) }
                            )
                        }
                    }
                }
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TarjetaDato("Visitas", vistas, Modifier.weight(1f))
                        TarjetaDato("Cupones", cupones.size, Modifier.weight(1f))
                        TarjetaDato("Canjeados", cupones.count { it.estado == EstadoCupon.CANJEADO }, Modifier.weight(1f))
                    }
                    Text(
                        "Totales desde que se publicaron las promociones.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            item {
                OutlinedCard(border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary)) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Validar un cupón", style = MaterialTheme.typography.titleMedium)
                        OutlinedTextField(
                            value = codigo,
                            onValueChange = { codigo = it.uppercase() },
                            label = { Text("Código del cliente") },
                            placeholder = { Text("CC-XXXX-00") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Button(
                            onClick = { validar(codigo) },
                            modifier = Modifier.fillMaxWidth().height(52.dp)
                        ) { Text("Validar") }
                        OutlinedButton(
                            onClick = {
                                lanzadorEscaner.launch(
                                    ScanOptions()
                                        .setDesiredBarcodeFormats(ScanOptions.QR_CODE)
                                        .setPrompt("Apunta al código QR del cupón")
                                        .setBeepEnabled(false)
                                        .setOrientationLocked(false)
                                )
                            },
                            modifier = Modifier.fillMaxWidth().height(52.dp)
                        ) {
                            Icon(Icons.Filled.QrCodeScanner, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Escanear código QR")
                        }
                        resultado?.let { (exito, mensaje) ->
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = if (exito) MaterialTheme.colorScheme.secondaryContainer
                                    else MaterialTheme.colorScheme.errorContainer
                                )
                            ) {
                                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        if (exito) Icons.Filled.CheckCircle else Icons.Filled.Error,
                                        contentDescription = null
                                    )
                                    Spacer(Modifier.width(10.dp))
                                    Text(mensaje)
                                }
                            }
                        }
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Mis promociones", style = MaterialTheme.typography.titleMedium)
                    FilledTonalButton(onClick = { navController.navigate(Rutas.promocionForm(0)) }) {
                        Icon(Icons.Filled.Add, contentDescription = null)
                        Spacer(Modifier.width(6.dp))
                        Text("Nueva")
                    }
                }
            }
            if (promosDelLocal.isEmpty()) {
                item {
                    Text("Este local todavía no tiene promociones.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            items(promosDelLocal, key = { it.id }) { promo ->
                FilaPromocion(
                    promo = promo,
                    alCambiarActiva = { activa -> panelViewModel.cambiarActiva(promo, activa) },
                    alEditar = { navController.navigate(Rutas.promocionForm(promo.id)) }
                )
            }
        }
    }
}

@Composable
private fun TarjetaDato(titulo: String, valor: Int, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text("$valor", style = MaterialTheme.typography.headlineMedium)
            Text(titulo, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun FilaPromocion(promo: Promocion, alCambiarActiva: (Boolean) -> Unit, alEditar: () -> Unit) {
    val estado = when {
        estaVencida(promo.fechaTermino) -> "Venció el ${formatearFecha(promo.fechaTermino)}"
        !promo.activa -> "Pausada"
        else -> "${promo.condiciones} · ${promo.cuposDisponibles} de ${promo.cuposTotales} cupos"
    }
    OutlinedCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(start = 14.dp, end = 8.dp, top = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(promo.titulo, fontWeight = FontWeight.Bold)
                Text(estado, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            IconButton(onClick = alEditar) {
                Icon(Icons.Filled.Edit, contentDescription = "Editar ${promo.titulo}")
            }
            Switch(checked = promo.activa, onCheckedChange = alCambiarActiva)
        }
    }
}
