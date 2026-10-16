package cl.DSY1105.cocinacapital.ui.views

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import cl.DSY1105.cocinacapital.data.model.Ocasion
import cl.DSY1105.cocinacapital.data.model.Promocion
import cl.DSY1105.cocinacapital.data.repository.ResultadoCupon
import cl.DSY1105.cocinacapital.navigation.Rutas
import cl.DSY1105.cocinacapital.ui.components.iconoDeCategoria
import cl.DSY1105.cocinacapital.utils.formatearFecha
import cl.DSY1105.cocinacapital.utils.textoDistancia
import cl.DSY1105.cocinacapital.viewmodel.AuthViewModel
import cl.DSY1105.cocinacapital.viewmodel.CatalogoViewModel
import cl.DSY1105.cocinacapital.viewmodel.CuponesViewModel

@Composable
fun DetalleScreen(
    navController: NavController,
    localId: Int,
    authViewModel: AuthViewModel,
    catalogoViewModel: CatalogoViewModel,
    cuponesViewModel: CuponesViewModel
) {
    val correo = authViewModel.usuarioActual?.correo ?: ""
    val locales by catalogoViewModel.locales.collectAsState()
    val promociones by catalogoViewModel.promociones.collectAsState()
    val favoritos by remember(correo) { catalogoViewModel.favoritosDe(correo) }
        .collectAsState(initial = emptyList())
    var mensaje by remember { mutableStateOf<String?>(null) }
    val contexto = LocalContext.current

    LaunchedEffect(localId) {
        if (correo.isNotEmpty()) catalogoViewModel.registrarVista(correo, localId)
    }

    val local = locales.find { it.id == localId }
    if (local == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }
    val esFavorito = localId in favoritos
    val escalaCorazon by animateFloatAsState(
        targetValue = if (esFavorito) 1.3f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioHighBouncy, stiffness = Spring.StiffnessLow),
        label = "corazon"
    )
    val promosDelLocal = promociones.filter { it.localId == localId && catalogoViewModel.estaDisponible(it) }
    val coloresBoton = IconButtonDefaults.filledIconButtonColors(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface
    )

    Scaffold { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(bottom = padding.calculateBottomPadding())) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .background(MaterialTheme.colorScheme.secondaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        iconoDeCategoria(local.categoria), contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.size(96.dp)
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.TopCenter)
                            .statusBarsPadding()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(onClick = { navController.popBackStack() }, colors = coloresBoton) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                        }
                        IconButton(
                            onClick = {
                                if (correo.isNotEmpty()) catalogoViewModel.cambiarFavorito(correo, localId, esFavorito)
                            },
                            colors = coloresBoton
                        ) {
                            Icon(
                                imageVector = if (esFavorito) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                contentDescription = if (esFavorito) "Quitar de favoritos" else "Agregar a favoritos",
                                modifier = Modifier.scale(escalaCorazon)
                            )
                        }
                    }
                }
            }

            item {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(local.nombre, style = MaterialTheme.typography.headlineMedium)
                    Text(
                        "${local.categoria} · ${local.nivelPrecio} · ${textoDistancia(catalogoViewModel.distanciaA(local))}",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(local.horario, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(local.direccion, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(local.descripcion, style = MaterialTheme.typography.bodyLarge)
                    OutlinedButton(
                        onClick = {
                            val uri = Uri.parse(
                                "geo:${local.latitud},${local.longitud}?q=${local.latitud},${local.longitud}(${Uri.encode(local.nombre)})"
                            )
                            try {
                                contexto.startActivity(Intent(Intent.ACTION_VIEW, uri))
                            } catch (e: ActivityNotFoundException) {
                                mensaje = "No encontramos una app de mapas en este teléfono."
                            }
                        },
                        modifier = Modifier.padding(top = 6.dp)
                    ) {
                        Icon(Icons.Filled.Directions, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Cómo llegar")
                    }
                }
            }

            item {
                Text(
                    "Promociones vigentes",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }
            if (promosDelLocal.isEmpty()) {
                item {
                    Text(
                        "Este local no tiene promociones vigentes.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                    )
                }
            }
            items(promosDelLocal, key = { it.id }) { promo ->
                TarjetaPromoDetalle(
                    promo = promo,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp),
                    alObtener = {
                        if (correo.isNotEmpty()) {
                            cuponesViewModel.obtenerCupon(correo, promo.id) { resultado ->
                                when (resultado) {
                                    is ResultadoCupon.Exito -> navController.navigate(Rutas.cupon(resultado.cupon.id))
                                    is ResultadoCupon.Error -> mensaje = resultado.mensaje
                                }
                            }
                        }
                    }
                )
            }

            item {
                Text(
                    "Menu destacado",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 16.dp)
                )
            }
            items(local.menuDestacado.lines()) { linea ->
                val partes = linea.split(" · ")
                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(partes.first(), style = MaterialTheme.typography.bodyLarge)
                        Text(partes.getOrElse(1) { "" }, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                    }
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                }
            }
            item { Spacer(Modifier.height(24.dp)) }
        }
    }

    mensaje?.let { texto ->
        AlertDialog(
            onDismissRequest = { mensaje = null },
            confirmButton = { TextButton(onClick = { mensaje = null }) { Text("Entendido") } },
            title = { Text("Aviso") },
            text = { Text(texto) }
        )
    }
}

@Composable
private fun TarjetaPromoDetalle(promo: Promocion, modifier: Modifier = Modifier, alObtener: () -> Unit) {
    OutlinedCard(
        modifier = modifier.fillMaxWidth(),
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                if (promo.bajaDemanda) Etiqueta("Horario de baja demanda", MaterialTheme.colorScheme.secondaryContainer)
                if (promo.ocasion != Ocasion.CUALQUIERA) {
                    Etiqueta("Ideal: ${promo.ocasion.etiqueta.lowercase()}", MaterialTheme.colorScheme.primaryContainer)
                }
            }
            Text(promo.titulo, style = MaterialTheme.typography.titleMedium)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Filled.Schedule, contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(promo.condiciones, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text(
                "Valido hasta el ${formatearFecha(promo.fechaTermino)} · 1 cupón por persona",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            val avance = if (promo.cuposTotales > 0) promo.cuposDisponibles.toFloat() / promo.cuposTotales else 0f
            LinearProgressIndicator(progress = { avance }, modifier = Modifier.fillMaxWidth())
            Text(
                "Quedan ${promo.cuposDisponibles} de ${promo.cuposTotales} cupos",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )
            Button(onClick = alObtener, modifier = Modifier.fillMaxWidth().height(52.dp)) {
                Icon(Icons.Filled.ConfirmationNumber, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Obtener cupón")
            }
        }
    }
}

@Composable
private fun Etiqueta(texto: String, fondo: Color) {
    Text(
        texto,
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .background(fondo, RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    )
}
