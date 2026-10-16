package cl.DSY1105.cocinacapital.notificaciones

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import cl.DSY1105.cocinacapital.MainActivity
import cl.DSY1105.cocinacapital.R
import cl.DSY1105.cocinacapital.utils.tienePermisoNotificaciones

object Notificador {
    private const val CANAL = "sugerencias"

    private fun crearCanal(context: Context) {
        val canal = NotificationChannel(
            CANAL,
            "Sugerencias y promociones",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Promociones según tus gustos, tus fechas especiales y locales cercanos"
        }
        NotificationManagerCompat.from(context).createNotificationChannel(canal)
    }

    @SuppressLint("MissingPermission")
    fun mostrar(context: Context, id: Int, titulo: String, texto: String): Boolean {
        if (!tienePermisoNotificaciones(context)) return false
        crearCanal(context)

        val abrirApp = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val alTocar = PendingIntent.getActivity(context, 0, abrirApp, PendingIntent.FLAG_IMMUTABLE)

        val notificacion = NotificationCompat.Builder(context, CANAL)
            .setSmallIcon(R.drawable.ic_notificacion)
            .setContentTitle(titulo)
            .setContentText(texto)
            .setStyle(NotificationCompat.BigTextStyle().bigText(texto))
            .setContentIntent(alTocar)
            .setAutoCancel(true)
            .build()
        NotificationManagerCompat.from(context).notify(id, notificacion)
        return true
    }
}
