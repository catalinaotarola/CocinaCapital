package cl.DSY1105.cocinacapital.ui.views

import android.Manifest
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import cl.DSY1105.cocinacapital.data.AjustesAvisos
import cl.DSY1105.cocinacapital.data.model.FechaEspecial
import cl.DSY1105.cocinacapital.data.model.Ocasion
import cl.DSY1105.cocinacapital.navigation.Rutas
import cl.DSY1105.cocinacapital.notificaciones.Avisos
import cl.DSY1105.cocinacapital.ui.components.BarraInferior
import cl.DSY1105.cocinacapital.ui.components.Insignia
import cl.DSY1105.cocinacapital.utils.diasHasta
import cl.DSY1105.cocinacapital.utils.textoDiaMes
import cl.DSY1105.cocinacapital.utils.tienePermisoNotificaciones
import cl.DSY1105.cocinacapital.viewmodel.AuthViewModel
import cl.DSY1105.cocinacapital.viewmodel.PerfilViewModel
import cl.DSY1105.cocinacapital.viewmodel.ResultadoAuth

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PerfilScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    perfilViewModel: PerfilViewModel
) {
    val usuario = authViewModel.usuarioActual
    val correo = usuario?.correo ?: ""
    val fechas by remember(correo) { perfilViewModel.fechasDe(correo) }.collectAsState(initial = emptyList())
    val avisos by perfilViewModel.avisos.collectAsState()
    val contexto = LocalContext.current

    var editandoNombre by remember { mutableStateOf(false) }
    var confirmandoEliminar by remember { mutableStateOf(false) }

    var permisoNotificaciones by remember { mutableStateOf(tienePermisoNotificaciones(contexto)) }
    var rechazado by remember { mutableStateOf(false) }
    val lanzadorPermiso = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { aceptado ->
        permisoNotificaciones = aceptado
        rechazado = !aceptado
    }
    val lanzadorAjustes = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        permisoNotificaciones = tienePermisoNotificaciones(contexto)
    }

    fun pedirPermiso() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            lanzadorPermiso.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            rechazado = true
        }
    }

    fun cambiarAvisos(nuevos: AjustesAvisos, seEncendio: Boolean) {
        perfilViewModel.cambiarAvisos(nuevos)
        if (seEncendio && !permisoNotificaciones) pedirPermiso()
    }

    Scaffold(bottomBar = { BarraInferior(navController, Rutas.PERFIL) }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(start = 24.dp, end = 24.dp, top = 32.dp, bottom = 12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(MaterialTheme.colorScheme.primary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        iniciales(usuario?.nombre ?: ""),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
                Spacer(Modifier.height(10.dp))
                Text(usuario?.nombre ?: "", style = MaterialTheme.typography.headlineMedium)
                Text(correo, color = MaterialTheme.colorScheme.onSurfaceVariant)
                TextButton(onClick = { editandoNombre = true }) { Text("Editar nombre") }
            }

            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Mis gustos", style = MaterialTheme.typography.titleMedium)
                    TextButton(onClick = { navController.navigate(Rutas.PREFERENCIAS) }) { Text("Cambiar") }
                }
                val preferencias = usuario?.preferencias
                val gustos = if (preferencias == null) emptyList<String>() else
                    preferencias.categorias.toList() +
                        listOfNotNull(preferencias.presupuesto.ifBlank { null }) +
                        preferencias.restricciones.toList() +
                        "${preferencias.radioKm} km"
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    gustos.forEach { gusto -> EtiquetaGusto(gusto) }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                Text("Fechas especiales", style = MaterialTheme.typography.titleMedium)
                if (fechas.isEmpty()) {
                    Text("Aún no agregas fechas especiales.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                fechas.forEach { fecha ->
                    FilaFecha(fecha, alEliminar = { perfilViewModel.eliminarFecha(fecha) })
                }
                OutlinedButton(
                    onClick = { navController.navigate(Rutas.NUEVA_FECHA) },
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) {
                    Icon(Icons.Filled.Add, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Agregar fecha especial")
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                Text("Notificaciones", style = MaterialTheme.typography.titleMedium)
                Text(
                    "Elige qué avisos quieres recibir. Te enviaremos como máximo 2 al día.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                FilaInterruptor("Promociones según mis gustos", avisos.gustos) { activo ->
                    cambiarAvisos(avisos.copy(gustos = activo), activo)
                }
                FilaInterruptor("Sugerencias para mis fechas", avisos.fechas) { activo ->
                    cambiarAvisos(avisos.copy(fechas = activo), activo)
                }
                FilaInterruptor("Locales cerca de mí", avisos.cercania) { activo ->
                    cambiarAvisos(avisos.copy(cercania = activo), activo)
                }
                if ((avisos.gustos || avisos.fechas || avisos.cercania) && !permisoNotificaciones) {
                    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                "Para recibir estos avisos, permite que Cocina Capital te envíe notificaciones. " +
                                    "Solo las usamos para promociones, tus fechas especiales y locales cercanos."
                            )
                            if (rechazado) {
                                Button(onClick = {
                                    val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                                        .putExtra(Settings.EXTRA_APP_PACKAGE, contexto.packageName)
                                    lanzadorAjustes.launch(intent)
                                }) { Text("Abrir ajustes del teléfono") }
                            } else {
                                Button(onClick = { pedirPermiso() }) { Text("Permitir notificaciones") }
                            }
                        }
                    }
                }
                OutlinedButton(
                    onClick = {
                        if (permisoNotificaciones) Avisos.probarAhora(contexto) else pedirPermiso()
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) { Text("Probar aviso ahora") }

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                OutlinedButton(
                    onClick = {
                        authViewModel.cerrarSesion()
                        navController.navigate(Rutas.LOGIN) {
                            popUpTo(navController.graph.id) { inclusive = true }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Cerrar sesión")
                }
                TextButton(
                    onClick = { confirmandoEliminar = true },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) {
                    Icon(Icons.Filled.DeleteForever, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Eliminar cuenta")
                }
            }
        }
    }

    if (editandoNombre) {
        DialogoNombre(
            nombreActual = usuario?.nombre ?: "",
            alGuardar = { nuevo -> authViewModel.actualizarNombre(nuevo) },
            alCerrar = { editandoNombre = false }
        )
    }

    if (confirmandoEliminar) {
        AlertDialog(
            onDismissRequest = { confirmandoEliminar = false },
            icon = { Icon(Icons.Filled.DeleteForever, contentDescription = null) },
            title = { Text("¿Eliminar tu cuenta?") },
            text = {
                Text("Se borrarán tu perfil, tus gustos, tus fechas especiales, tus favoritos y tus cupones. No se puede deshacer.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        confirmandoEliminar = false
                        authViewModel.eliminarCuenta()
                        navController.navigate(Rutas.LOGIN) {
                            popUpTo(navController.graph.id) { inclusive = true }
                        }
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) { Text("Eliminar") }
            },
            dismissButton = { TextButton(onClick = { confirmandoEliminar = false }) { Text("Cancelar") } }
        )
    }
}

private fun iniciales(nombre: String): String =
    nombre.split(" ").filter { it.isNotBlank() }.take(2).joinToString("") { it.first().uppercase() }

private fun iconoDeOcasion(ocasion: Ocasion): ImageVector = when (ocasion) {
    Ocasion.PAREJA -> Icons.Filled.Favorite
    Ocasion.FAMILIAR -> Icons.Filled.FamilyRestroom
    Ocasion.AMIGOS -> Icons.Filled.Groups
    Ocasion.PERSONAL -> Icons.Filled.Cake
    Ocasion.CUALQUIERA -> Icons.Filled.Event
}

@Composable
private fun EtiquetaGusto(texto: String) {
    Text(
        texto,
        style = MaterialTheme.typography.labelLarge,
        modifier = Modifier
            .background(MaterialTheme.colorScheme.secondaryContainer, RoundedCornerShape(16.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    )
}

@Composable
private fun FilaFecha(fecha: FechaEspecial, alEliminar: () -> Unit) {
    val dias = diasHasta(fecha.dia, fecha.mes)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(16.dp))
            .padding(start = 14.dp, top = 4.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(iconoDeOcasion(fecha.ocasion), contentDescription = null)
        Column(modifier = Modifier.weight(1f).padding(horizontal = 12.dp)) {
            Text(fecha.nombre, fontWeight = FontWeight.Bold)
            Text(
                "${fecha.ocasion.etiqueta} · ${textoDiaMes(fecha.dia, fecha.mes)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        if (dias <= fecha.diasAviso) {
            Insignia(
                when (dias) {
                    0L -> "hoy"
                    1L -> "mañana"
                    else -> "en $dias días"
                }
            )
        }
        IconButton(onClick = alEliminar) {
            Icon(Icons.Filled.Delete, contentDescription = "Eliminar ${fecha.nombre}")
        }
    }
}

@Composable
private fun FilaInterruptor(texto: String, activo: Boolean, alCambiar: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .toggleable(value = activo, role = Role.Switch, onValueChange = alCambiar)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(texto, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
        Switch(checked = activo, onCheckedChange = null)
    }
}

@Composable
private fun DialogoNombre(
    nombreActual: String,
    alGuardar: (String) -> ResultadoAuth,
    alCerrar: () -> Unit
) {
    var nombre by remember { mutableStateOf(nombreActual) }
    var error by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = alCerrar,
        title = { Text("Editar nombre") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre") },
                    singleLine = true
                )
                if (error.isNotEmpty()) Text(error, color = MaterialTheme.colorScheme.error)
            }
        },
        confirmButton = {
            TextButton(onClick = {
                when (val resultado = alGuardar(nombre)) {
                    ResultadoAuth.Exito -> alCerrar()
                    is ResultadoAuth.Error -> error = resultado.mensaje
                }
            }) { Text("Guardar") }
        },
        dismissButton = { TextButton(onClick = alCerrar) { Text("Cancelar") } }
    )
}
