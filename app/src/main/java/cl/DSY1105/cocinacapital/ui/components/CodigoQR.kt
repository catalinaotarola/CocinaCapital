package cl.DSY1105.cocinacapital.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.Dp
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter

@Composable
fun CodigoQR(texto: String, tamano: Dp, modifier: Modifier = Modifier) {
    val matriz = remember(texto) {
        QRCodeWriter().encode(texto, BarcodeFormat.QR_CODE, 0, 0, mapOf(EncodeHintType.MARGIN to 1))
    }
    val color = MaterialTheme.colorScheme.onSurface
    Canvas(modifier = modifier.size(tamano)) {
        val celda = size.width / matriz.width
        for (x in 0 until matriz.width) {
            for (y in 0 until matriz.height) {
                if (matriz[x, y]) {
                    drawRect(color = color, topLeft = Offset(x * celda, y * celda), size = Size(celda, celda))
                }
            }
        }
    }
}
