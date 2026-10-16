package cl.DSY1105.cocinacapital.notificaciones

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import cl.DSY1105.cocinacapital.data.repository.CocinaRepository
import cl.DSY1105.cocinacapital.utils.Recomendador
import cl.DSY1105.cocinacapital.utils.Reglas
import cl.DSY1105.cocinacapital.utils.distanciaKm
import cl.DSY1105.cocinacapital.utils.estaVencida
import cl.DSY1105.cocinacapital.utils.textoCuando
import cl.DSY1105.cocinacapital.utils.textoDistancia
import cl.DSY1105.cocinacapital.viewmodel.TarjetaPromo
import kotlinx.coroutines.flow.first

class AvisosWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val repo = CocinaRepository(applicationContext)
        val ajustes = repo.ajustes
        val esPrueba = inputData.getBoolean(ES_PRUEBA, false)

        val correo = ajustes.leerSesion() ?: return Result.success()
        val usuario = repo.usuarios().find { it.correo == correo } ?: return Result.success()
        val avisos = ajustes.avisos.first()

        val datos = repo.datosParaRecomendar(correo)
        val (latitud, longitud) = ajustes.leerUbicacion() ?: (Reglas.LATITUD_CONCON to Reglas.LONGITUD_CONCON)
        val locales = repo.localesAhora()
        val tarjetas = repo.promocionesAhora()
            .filter { it.activa && it.cuposDisponibles > 0 && !estaVencida(it.fechaTermino) }
            .mapNotNull { promo ->
                val local = locales.find { it.id == promo.localId } ?: return@mapNotNull null
                val km = distanciaKm(latitud, longitud, local.latitud, local.longitud)
                TarjetaPromo(promo, local, km, Recomendador.puntaje(usuario, promo, local, km, datos))
            }
            .sortedByDescending { it.puntaje }

        val mensajes = mutableListOf<Pair<String, String>>()

        val cercana = Recomendador.fechasCercanas(datos.fechas).firstOrNull()
        if (avisos.fechas && cercana != null) {
            val (fecha, dias) = cercana
            val opciones = tarjetas.filter { Recomendador.calzaConOcasion(it.promocion, fecha.ocasion) }.take(2)
            if (opciones.isNotEmpty()) {
                val lugares = opciones.joinToString(" o ") { "${it.promocion.etiqueta} en ${it.local.nombre}" }
                mensajes.add(
                    "Tu ${fecha.nombre.lowercase()} ${textoCuando(dias)}" to
                        "Celebra con $lugares. Toca para ver todas las opciones."
                )
            }
        }

        val mejor = tarjetas.firstOrNull {
            it.puntaje >= Reglas.UMBRAL_NOTIFICACION && it.local.categoria in usuario.preferencias.categorias
        }
        if (avisos.gustos && mejor != null) {
            mensajes.add("Te puede gustar: ${mejor.local.nombre}" to "${mejor.promocion.titulo} · ${mejor.promocion.condiciones}")
        }

        val cerca = tarjetas.filter { it.distanciaKm <= usuario.preferencias.radioKm }.minByOrNull { it.distanciaKm }
        if (avisos.cercania && cerca != null) {
            mensajes.add(
                "Promoción cerca de ti" to
                    "${cerca.local.nombre} está a ${textoDistancia(cerca.distanciaKm)}: ${cerca.promocion.titulo}."
            )
        }

        if (esPrueba && mensajes.isEmpty()) {
            mensajes.add("Cocina Capital" to "Por ahora no hay sugerencias nuevas. Revisa qué avisos tienes encendidos en tu perfil.")
        }

        mensajes.forEachIndexed { indice, (titulo, texto) ->
            if (esPrueba || ajustes.puedeAvisarHoy(Reglas.MAX_AVISOS_POR_DIA)) {
                val mostrada = Notificador.mostrar(applicationContext, indice + 1, titulo, texto)
                if (mostrada && !esPrueba) ajustes.contarAviso()
            }
        }
        return Result.success()
    }

    companion object {
        const val ES_PRUEBA = "es_prueba"
    }
}
