package cl.DSY1105.cocinacapital.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast

fun copiarTexto(context: Context, etiqueta: String, texto: String) {
    val portapapeles = context.getSystemService(ClipboardManager::class.java)
    portapapeles?.setPrimaryClip(ClipData.newPlainText(etiqueta, texto))
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
        Toast.makeText(context, "Copiado: $texto", Toast.LENGTH_SHORT).show()
    }
}

fun compartirTexto(context: Context, texto: String) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, texto)
    }
    context.startActivity(Intent.createChooser(intent, "Compartir cupón"))
}
