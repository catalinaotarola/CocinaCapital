
package cl.DSY1105.cocinacapital.ui.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import cl.DSY1105.cocinacapital.navigation.Rutas
import cl.DSY1105.cocinacapital.viewmodel.AuthViewModel
import cl.DSY1105.cocinacapital.viewmodel.ResultadoAuth

@Composable
fun LoginScreen(navController: NavController, authViewModel: AuthViewModel) {
    // rememberSaveable: el texto escrito sobrevive al ir a otra pantalla y volver
    var correo by rememberSaveable { mutableStateOf("") }
    var clave by rememberSaveable { mutableStateOf("") }
    var verClave by rememberSaveable { mutableStateOf(false) }
    var error by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primaryContainer)
            .imePadding()  // deja espacio cuando aparece el teclado
    ) {
        // 1) Cabecera azul bebé con la marca
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 64.dp, bottom = 28.dp, start = 32.dp, end = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(MaterialTheme.colorScheme.secondaryContainer, RoundedCornerShape(24.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.Restaurant, contentDescription = null, modifier = Modifier.size(40.dp))
            }
            Spacer(Modifier.height(12.dp))
            Text("COCINA CAPITAL", style = MaterialTheme.typography.headlineMedium)
            Text(
                "Descubre promociones gastronómicas en la Región de Valparaíso",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(
                    MaterialTheme.colorScheme.surface,
                    RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
                )
                .verticalScroll(rememberScrollState())
                .navigationBarsPadding()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Inicia sesión", style = MaterialTheme.typography.titleLarge)

            OutlinedTextField(
                value = correo,
                onValueChange = { correo = it },
                label = { Text("Correo electrónico") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = clave,
                onValueChange = { clave = it },
                label = { Text("Contraseña") },
                singleLine = true,
                visualTransformation = if (verClave) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    IconButton(onClick = { verClave = !verClave }) {
                        Icon(
                            imageVector = if (verClave) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            contentDescription = if (verClave) "Ocultar contraseña" else "Mostrar contraseña"
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            // Enlace 1 = recuperar contraseña
            TextButton(
                onClick = { navController.navigate(Rutas.RECUPERAR) },
                modifier = Modifier.align(Alignment.End)
            ) { Text("¿Olvidaste tu contraseña?") }

            if (error.isNotEmpty()) {
                Text(error, color = MaterialTheme.colorScheme.error)
            }

            Button(
                onClick = {
                    when (val resultado = authViewModel.iniciarSesion(correo, clave)) {
                        ResultadoAuth.Exito -> {
                            error = ""
                            navController.navigate(Rutas.INICIO) {
                                popUpTo(Rutas.LOGIN) { inclusive = true }
                            }
                        }
                        is ResultadoAuth.Error -> error = resultado.mensaje
                    }
                },
                modifier = Modifier.fillMaxWidth().height(52.dp)
            ) { Text("Ingresar") }

            FilledTonalButton(
                onClick = { navController.navigate(Rutas.REGISTRO) },
                modifier = Modifier.fillMaxWidth().height(52.dp)
            ) { Text("Crear una cuenta") }

            // Enlace 2 = panel del local (perfil Oferente)
            TextButton(
                onClick = { navController.navigate(Rutas.PANEL_LOCAL) },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Icon(Icons.Filled.Storefront, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text("¿Tienes un local? Ingresa al panel")
            }

            // Enlace 3 = política de privacidad
            Text(
                "Tus datos se tratan según la Ley 19.628 y la Ley 21.719.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            TextButton(
                onClick = { navController.navigate(Rutas.POLITICA) },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) { Text("Política de privacidad") }
        }
    }
}
