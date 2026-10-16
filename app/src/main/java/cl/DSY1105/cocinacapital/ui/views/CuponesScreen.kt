package cl.DSY1105.cocinacapital.ui.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import cl.DSY1105.cocinacapital.data.model.Cupon
import cl.DSY1105.cocinacapital.data.model.EstadoCupon
import cl.DSY1105.cocinacapital.data.model.Local
import cl.DSY1105.cocinacapital.data.model.Promocion
import cl.DSY1105.cocinacapital.navigation.Rutas
import cl.DSY1105.cocinacapital.ui.components.BarraInferior
import cl.DSY1105.cocinacapital.ui.components.MiniaturaLocal
import cl.DSY1105.cocinacapital.utils.esHoy
import cl.DSY1105.cocinacapital.utils.formatearFecha
import cl.DSY1105.cocinacapital.utils.formatearMillis
import cl.DSY1105.cocinacapital.viewmodel.AuthViewModel
import cl.DSY1105.cocinacapital.viewmodel.CatalogoViewModel
import cl.DSY1105.cocinacapital.viewmodel.CuponesViewModel

@Composable
fun CuponesScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    catalogoViewModel: CatalogoViewModel,
    cuponesViewModel: CuponesViewModel
) {
    val correo = authViewModel.usuarioActual?.correo ?: ""
    val cupones by remember(correo) { cuponesViewModel.cuponesDe(correo) }.collectAsState(initial = emptyList())
    val locales by catalogoViewModel.locales.collectAsState()
    val promociones by catalogoViewModel.promociones.collectAsState()
    var pestana by rememberSaveable { mutableIntStateOf(0) }

    val estados = listOf(EstadoCupon.ACTIVO, EstadoCupon.CANJEADO, EstadoCupon.VENCIDO)
    val nombresPestanas = listOf("Activos", "Usados", "Vencidos")

    val conEstado = cupones.map { c ->
        c to cuponesViewModel.estadoVisible(c, promociones.find { it.id == c.promocionId })
    }
    val visibles = conEstado.filter { it.second == estados[pestana] }

    Scaffold(bottomBar = { BarraInferior(navController, Rutas.CUPONES) }) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            Text("Mis cupones", style = MaterialTheme.typography.headlineMedium, modifier = Modifier.padding(20.dp))

            TabRow(selectedTabIndex = pestana) {
                estados.forEachIndexed { indice, estado ->
                    Tab(
                        selected = pestana == indice,
                        onClick = { pestana = indice },
                        text = { Text("${nombresPestanas[indice]} · ${conEstado.count { it.second == estado }}") }
                    )
                }
            }

            if (visibles.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically)
                ) {
                    Text("No tienes cupones ${nombresPestanas[pestana].lowercase()}.")
                    if (pestana == 0) {
                        OutlinedButton(onClick = { navController.navigate(Rutas.INICIO) { launchSingleTop = true } }) {
                            Text("Buscar promociones")
                        }
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(visibles, key = { it.first.id }) { (cupon, estado) ->
                        val promo = promociones.find { it.id == cupon.promocionId }
                        val local = locales.find { it.id == cupon.localId }
                        if (promo != null && local != null) {
                            TarjetaCupon(cupon, estado, promo, local) {
                                navController.navigate(Rutas.cupon(cupon.id))
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TarjetaCupon(cupon: Cupon, estado: EstadoCupon, promo: Promocion, local: Local, alTocar: () -> Unit) {
    val venceHoy = estado == EstadoCupon.ACTIVO && esHoy(promo.fechaTermino)
    OutlinedCard(onClick = alTocar, modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            MiniaturaLocal(local.categoria, local.id, 56.dp)
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(local.nombre, style = MaterialTheme.typography.titleMedium)
                Text(promo.titulo, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                Text(
                    when (estado) {
                        EstadoCupon.ACTIVO -> "Vence el ${formatearFecha(promo.fechaTermino)}"
                        EstadoCupon.CANJEADO -> "Usado el ${formatearMillis(cupon.fechaCanje ?: cupon.fechaObtencion)}"
                        EstadoCupon.VENCIDO -> "Venció el ${formatearFecha(promo.fechaTermino)}"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                if (venceHoy) "Vence hoy" else estado.etiqueta,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = if (venceHoy) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .background(
                        when {
                            venceHoy -> MaterialTheme.colorScheme.errorContainer
                            estado == EstadoCupon.ACTIVO -> MaterialTheme.colorScheme.secondaryContainer
                            else -> MaterialTheme.colorScheme.surfaceVariant
                        },
                        RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}
