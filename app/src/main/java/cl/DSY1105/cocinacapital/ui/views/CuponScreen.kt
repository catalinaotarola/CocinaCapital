package cl.DSY1105.cocinacapital.ui.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import cl.DSY1105.cocinacapital.data.model.EstadoCupon
import cl.DSY1105.cocinacapital.navigation.Rutas
import cl.DSY1105.cocinacapital.ui.components.CodigoQR
import cl.DSY1105.cocinacapital.utils.compartirTexto
import cl.DSY1105.cocinacapital.utils.copiarTexto
import cl.DSY1105.cocinacapital.utils.formatearFechaHora
import cl.DSY1105.cocinacapital.utils.formatearMillis
import cl.DSY1105.cocinacapital.utils.formatearPesos
import cl.DSY1105.cocinacapital.viewmodel.CatalogoViewModel
import cl.DSY1105.cocinacapital.viewmodel.CuponesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CuponScreen(
    navController: NavController,
    cuponId: Long,
    catalogoViewModel: CatalogoViewModel,
    cuponesViewModel: CuponesViewModel
) {
    val cupon by remember(cuponId) { cuponesViewModel.cupon(cuponId) }.collectAsState(initial = null)
    val locales by catalogoViewModel.locales.collectAsState()
    val promociones by catalogoViewModel.promociones.collectAsState()
    val contexto = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tu cupón") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        val c = cupon
        val promo = promociones.find { it.id == c?.promocionId }
        val local = locales.find { it.id == c?.localId }
        if (c == null || promo == null || local == null) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }
        val estado = cuponesViewModel.estadoVisible(c, promo)
        val activo = estado == EstadoCupon.ACTIVO

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            val (fondo, aviso) = when (estado) {
                EstadoCupon.ACTIVO -> MaterialTheme.colorScheme.secondaryContainer to
                    "¡Ticket pagado! Tu reserva vence el ${formatearFechaHora(c.venceReserva)}."
                EstadoCupon.CANJEADO -> MaterialTheme.colorScheme.surfaceVariant to
                    "Este cupón ya fue usado el ${formatearMillis(c.fechaCanje ?: c.fechaObtencion)}."
                EstadoCupon.VENCIDO -> MaterialTheme.colorScheme.errorContainer to
                    "Este cupón venció el ${formatearFechaHora(c.venceReserva)}."
            }
            Card(
                colors = CardDefaults.cardColors(containerColor = fondo),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(aviso, fontWeight = FontWeight.Bold, modifier = Modifier.padding(16.dp))
            }

            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(local.nombre, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(promo.titulo, style = MaterialTheme.typography.titleLarge, textAlign = TextAlign.Center)
                    if (activo) {
                        CodigoQR(c.codigo, 200.dp)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            c.codigo,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 4.sp
                        )
                        if (activo) {
                            IconButton(onClick = { copiarTexto(contexto, "Código del cupón", c.codigo) }) {
                                Icon(Icons.Filled.ContentCopy, contentDescription = "Copiar código")
                            }
                        }
                    }
                    Text(
                        if (activo) "Muestra este código en el local para canjearlo."
                        else "Este código ya no se puede usar.",
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (activo) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(
                        onClick = { copiarTexto(contexto, "Código del cupón", c.codigo) },
                        modifier = Modifier.weight(1f).height(48.dp)
                    ) {
                        Icon(Icons.Filled.ContentCopy, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Copiar")
                    }
                    OutlinedButton(
                        onClick = {
                            compartirTexto(
                                contexto,
                                "Mi cupón de Cocina Capital en ${local.nombre}: ${promo.titulo}. Código: ${c.codigo}"
                            )
                        },
                        modifier = Modifier.weight(1f).height(48.dp)
                    ) {
                        Icon(Icons.Filled.Share, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Compartir")
                    }
                }
            }

            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilaDato("Válido", promo.condiciones)
                    FilaDato("Ticket pagado", formatearPesos(c.precioPagado))
                    FilaDato("Reserva hasta", formatearFechaHora(c.venceReserva))
                    FilaDato("Comprado", formatearMillis(c.fechaObtencion))
                }
            }

            Button(
                onClick = {
                    navController.navigate(Rutas.CUPONES) {
                        popUpTo(Rutas.INICIO)
                        launchSingleTop = true
                    }
                },
                modifier = Modifier.fillMaxWidth().height(52.dp)
            ) { Text("Ver mis cupones") }
        }
    }
}

@Composable
private fun FilaDato(etiqueta: String, valor: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(etiqueta, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(valor, fontWeight = FontWeight.Bold, textAlign = TextAlign.End, modifier = Modifier.padding(start = 12.dp))
    }
}
