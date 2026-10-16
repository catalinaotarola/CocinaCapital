package cl.DSY1105.cocinacapital.ui.views

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import cl.DSY1105.cocinacapital.data.model.Local
import cl.DSY1105.cocinacapital.data.model.Promocion
import cl.DSY1105.cocinacapital.navigation.Rutas
import cl.DSY1105.cocinacapital.ui.components.BarraInferior
import cl.DSY1105.cocinacapital.ui.components.MapaLocales
import cl.DSY1105.cocinacapital.ui.components.MiniaturaLocal
import cl.DSY1105.cocinacapital.utils.pedirUbicacionActual
import cl.DSY1105.cocinacapital.utils.textoDistancia
import cl.DSY1105.cocinacapital.utils.tienePermisoUbicacion
import cl.DSY1105.cocinacapital.viewmodel.AuthViewModel
import cl.DSY1105.cocinacapital.viewmodel.CatalogoViewModel

@Composable
fun CercaScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    catalogoViewModel: CatalogoViewModel
) {
    val contexto = LocalContext.current
    val radioKm = authViewModel.usuarioActual?.preferencias?.radioKm ?: 3
    val locales by catalogoViewModel.locales.collectAsState()
    val promociones by catalogoViewModel.promociones.collectAsState()
    var precio by rememberSaveable { mutableStateOf("") }
    var mensaje by remember { mutableStateOf<String?>(null) }

    var tienePermiso by remember { mutableStateOf(tienePermisoUbicacion(contexto)) }
    var rechazado by remember { mutableStateOf(false) }
    val lanzadorPermiso = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { respuestas ->
        tienePermiso = respuestas.values.any { it }
        rechazado = !tienePermiso
    }
    val lanzadorAjustes = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        tienePermiso = tienePermisoUbicacion(contexto)
    }

    fun buscarMiUbicacion() {
        pedirUbicacionActual(
            contexto,
            alRecibir = { lat, lon ->
                catalogoViewModel.actualizarUbicacion(lat, lon)
                mensaje = null
            },
            alFallar = { mensaje = "No pudimos leer tu ubicación. Te mostramos la última que conocemos." }
        )
    }

    LaunchedEffect(tienePermiso) {
        if (tienePermiso) buscarMiUbicacion()
    }

    val cercanos = catalogoViewModel.localesCercanos(locales, promociones, radioKm)
        .filter { (local, _) -> precio.isEmpty() || local.nivelPrecio == precio }

    Scaffold(bottomBar = { BarraInferior(navController, Rutas.CERCA) }) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            Box(modifier = Modifier.fillMaxWidth().height(300.dp)) {
                MapaLocales(
                    latitud = catalogoViewModel.latitud,
                    longitud = catalogoViewModel.longitud,
                    radioKm = radioKm,
                    locales = cercanos.map { it.first },
                    alTocarLocal = { id -> navController.navigate(Rutas.detalle(id)) },
                    modifier = Modifier.fillMaxSize()
                )
                if (tienePermiso) {
                    SmallFloatingActionButton(
                        onClick = { buscarMiUbicacion() },
                        modifier = Modifier.align(Alignment.BottomEnd).padding(12.dp)
                    ) {
                        Icon(Icons.Filled.MyLocation, contentDescription = "Usar mi ubicación")
                    }
                }
            }

            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    AssistChip(
                        onClick = { navController.navigate(Rutas.PREFERENCIAS) },
                        label = { Text("Hasta $radioKm km") },
                        leadingIcon = {
                            Icon(Icons.Filled.Tune, contentDescription = null, modifier = Modifier.size(18.dp))
                        }
                    )
                }
                item {
                    FilterChip(
                        selected = catalogoViewModel.soloConPromocion,
                        onClick = { catalogoViewModel.cambiarSoloConPromocion(!catalogoViewModel.soloConPromocion) },
                        label = { Text("Con promoción") }
                    )
                }
                items(listOf("\$", "\$\$", "\$\$\$")) { nivel ->
                    FilterChip(
                        selected = precio == nivel,
                        onClick = { precio = if (precio == nivel) "" else nivel },
                        label = { Text(nivel) }
                    )
                }
            }

            LazyColumn(
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (!tienePermiso) {
                    item {
                        TarjetaPermisoUbicacion(
                            rechazado = rechazado,
                            alPermitir = {
                                lanzadorPermiso.launch(
                                    arrayOf(
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.ACCESS_COARSE_LOCATION
                                    )
                                )
                            },
                            alAbrirAjustes = {
                                val intent = Intent(
                                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                    Uri.parse("package:${contexto.packageName}")
                                )
                                lanzadorAjustes.launch(intent)
                            }
                        )
                    }
                }
                mensaje?.let { texto ->
                    item { Text(texto, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                }
                item {
                    Text(
                        if (cercanos.size == 1) "1 local a menos de $radioKm km"
                        else "${cercanos.size} locales a menos de $radioKm km",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                items(cercanos, key = { it.first.id }) { (local, km) ->
                    val promo = promociones.firstOrNull { it.localId == local.id && catalogoViewModel.estaDisponible(it) }
                    FilaCercano(local, km, promo) { navController.navigate(Rutas.detalle(local.id)) }
                }
            }
        }
    }
}

@Composable
private fun TarjetaPermisoUbicacion(rechazado: Boolean, alPermitir: () -> Unit, alAbrirAjustes: () -> Unit) {
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("¿Usamos tu ubicación?", style = MaterialTheme.typography.titleMedium)
            Text(
                "La usamos solo mientras usas la app, para mostrarte los locales y promociones que tienes cerca. " +
                    "Mientras tanto, te mostramos el centro de Concón."
            )
            if (rechazado) {
                Button(onClick = alAbrirAjustes) { Text("Abrir ajustes del teléfono") }
            } else {
                Button(onClick = alPermitir) { Text("Permitir ubicación") }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilaCercano(local: Local, km: Double, promo: Promocion?, alTocar: () -> Unit) {
    OutlinedCard(onClick = alTocar, modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(10.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            MiniaturaLocal(local.categoria, local.id, 56.dp)
            Column(modifier = Modifier.weight(1f)) {
                Text(local.nombre, style = MaterialTheme.typography.titleMedium)
                Text(
                    "${textoDistancia(km)} · ${local.nivelPrecio} · ${promo?.titulo ?: "Sin promociones vigentes"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
        }
    }
}
