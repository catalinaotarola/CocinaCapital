package cl.DSY1105.cocinacapital.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.LunchDining
import androidx.compose.material.icons.filled.OutdoorGrill
import androidx.compose.material.icons.filled.RamenDining
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.SetMeal
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun iconoDeCategoria(categoria: String): ImageVector = when (categoria) {
    "Mariscos" -> Icons.Filled.SetMeal
    "Sushi" -> Icons.Filled.RamenDining
    "Café" -> Icons.Filled.LocalCafe
    "Parrilla" -> Icons.Filled.OutdoorGrill
    "Hamburguesas" -> Icons.Filled.LunchDining
    "Vegana" -> Icons.Filled.Eco
    else -> Icons.Filled.Restaurant
}

@Composable
fun MiniaturaLocal(categoria: String, localId: Int, tamano: Dp, modifier: Modifier = Modifier) {
    val fondos = listOf(
        MaterialTheme.colorScheme.secondaryContainer,
        MaterialTheme.colorScheme.primaryContainer,
        MaterialTheme.colorScheme.tertiaryContainer
    )
    Box(
        modifier = modifier
            .size(tamano)
            .background(fondos[localId % fondos.size], RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = iconoDeCategoria(categoria),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.size(tamano * 0.45f)
        )
    }
}
