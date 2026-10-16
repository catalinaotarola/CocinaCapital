package cl.DSY1105.cocinacapital.notificaciones

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import java.util.concurrent.TimeUnit

object Avisos {
    fun programar(context: Context) {
        val tarea = PeriodicWorkRequestBuilder<AvisosWorker>(1, TimeUnit.DAYS).build()
        WorkManager.getInstance(context)
            .enqueueUniquePeriodicWork("avisos_diarios", ExistingPeriodicWorkPolicy.KEEP, tarea)
    }

    fun probarAhora(context: Context) {
        val tarea = OneTimeWorkRequestBuilder<AvisosWorker>()
            .setInputData(workDataOf(AvisosWorker.ES_PRUEBA to true))
            .build()
        WorkManager.getInstance(context).enqueue(tarea)
    }
}
