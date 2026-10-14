package cl.DSY1105.cocinacapital.ui.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import cl.DSY1105.cocinacapital.navigation.Rutas
import cl.DSY1105.cocinacapital.viewmodel.AuthViewModel
import cl.DSY1105.cocinacapital.viewmodel.ResultadoAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroScreen(navController: NavController, authViewModel: AuthViewModel) {
    var nombre by rememberSaveable { mutableStateOf("") }
    var correo by rememberSaveable { mutableStateOf("") }
    var clave by rememberSaveable { mutableStateOf("") }
    var confirmar by rememberSaveable { mutableStateOf("") }
    var aceptaPrivacidad by rememberSaveable { mutableStateOf(false) }
    var error by rememberSaveable { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Crear cuenta") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
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
            Text(
                "Regístrate para guardar tus cupones y recibir sugerencias pensadas para ti.",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            OutlinedTextField(
                value = nombre, onValueChange = { nombre = it },
                label = { Text("Nombre") }, singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = correo, onValueChange = { correo = it },
                label = { Text("Correo electrónico") }, singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = clave, onValueChange = { clave = it },
                label = { Text("Contraseña") }, singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                supportingText = { Text("Mínimo 6 caracteres") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = confirmar, onValueChange = { confirmar = it },
                label = { Text("Confirmar contraseña") }, singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth()
            )

            // consentimiento del tratamiento de datos
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
                    .toggleable(
                        value = aceptaPrivacidad,
                        role = Role.Checkbox,
                        onValueChange = { aceptaPrivacidad = it }
                    )
                    .padding(12.dp),
                verticalAlignment = Alignment.Top
            ) {
                Checkbox(checked = aceptaPrivacidad, onCheckedChange = null)
                Text(
                    "Acepto la Política de privacidad y el tratamiento de mis datos para recibir " +
                            "recomendaciones, según la Ley 19.628 y la Ley 21.719.",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(start = 12.dp)
                )
            }
            // Enlace para leer la política antes de aceptarla
            TextButton(onClick = { navController.navigate(Rutas.POLITICA) }) {
                Text("Leer la política de privacidad")
            }

            if (error.isNotEmpty()) {
                Text(error, color = MaterialTheme.colorScheme.error)
            }

            Button(
                onClick = {
                    val resultado = authViewModel.registrar(nombre, correo, clave, confirmar, aceptaPrivacidad)
                    when (resultado) {
                        ResultadoAuth.Exito -> {
                            error = ""
                            // Cuenta creada: ahora elige sus gustos (RF-04)
                            navController.navigate(Rutas.PREFERENCIAS) {
                                popUpTo(Rutas.LOGIN) { inclusive = true }
                            }
                        }
                        is ResultadoAuth.Error -> error = resultado.mensaje
                    }
                },
                modifier = Modifier.fillMaxWidth().height(52.dp)
            ) { Text("Crear cuenta") }
        }
    }
}