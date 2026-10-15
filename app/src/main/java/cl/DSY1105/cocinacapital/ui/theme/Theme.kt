package cl.DSY1105.cocinacapital.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
// Este archivo dice quecolor va en cada parte de la pantalla y cre
private val ColoresCocinaCapital = lightColorScheme(
    primary = AzulOceano,
    onPrimary = Blanco,
    primaryContainer = AzulBebe,
    onPrimaryContainer = AzulMarino,
    secondary = AzulMarino,
    onSecondary = Blanco,
    secondaryContainer = VerdeMenta,
    onSecondaryContainer = AzulMarino,
    tertiary = GrisAzulado,
    tertiaryContainer = VerdePistacho,
    onTertiaryContainer = AzulMarino,
    background = Blanco,
    onBackground = AzulMarino,
    surface = Blanco,
    onSurface = AzulMarino,
    surfaceVariant = AzulNube,
    onSurfaceVariant = GrisAzulado,
    surfaceContainer = AzulNube,
    surfaceContainerLow = AzulNube,
    surfaceContainerHigh = AzulNube,
    surfaceContainerHighest = AzulNube,
    outline = BordeMedio,
    outlineVariant = BordeSuave,
    error = RojoError,
    onError = Blanco
)

@Composable // ESTO ES PARTE DE LA PANTALLA.
fun CocinaCapitalTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = ColoresCocinaCapital,
        typography = Typography,
        content = content
    )
}
