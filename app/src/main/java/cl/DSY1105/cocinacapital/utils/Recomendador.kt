package cl.DSY1105.cocinacapital.utils

import cl.DSY1105.cocinacapital.data.model.FechaEspecial
import cl.DSY1105.cocinacapital.data.model.Local
import cl.DSY1105.cocinacapital.data.model.Ocasion
import cl.DSY1105.cocinacapital.data.model.Promocion
import cl.DSY1105.cocinacapital.data.model.Usuario
import cl.DSY1105.cocinacapital.data.repository.DatosRecomendacion
import java.time.Instant
import java.time.ZoneId

object Recomendador {
    private const val SIETE_DIAS_EN_MILIS = 7L * 24 * 60 * 60 * 1000

    fun fechasCercanas(fechas: List<FechaEspecial>): List<Pair<FechaEspecial, Long>> =
        fechas
            .map { fecha -> fecha to diasHasta(fecha.dia, fecha.mes) }
            .filter { (fecha, dias) -> dias <= fecha.diasAviso }
            .sortedBy { it.second }

    fun calzaConOcasion(promo: Promocion, ocasion: Ocasion): Boolean =
        promo.ocasion == ocasion || promo.ocasion == Ocasion.CUALQUIERA

    fun puntaje(
        usuario: Usuario?,
        promo: Promocion,
        local: Local,
        distanciaKm: Double,
        datos: DatosRecomendacion
    ): Int {
        if (usuario == null) return 0
        var puntos = 0

        if (local.categoria in usuario.preferencias.categorias) puntos += 30

        if (fechasCercanas(datos.fechas).any { (fecha, _) -> calzaConOcasion(promo, fecha.ocasion) }) puntos += 40

        if (distanciaKm <= usuario.preferencias.radioKm) puntos += 20

        val haceSieteDias = System.currentTimeMillis() - SIETE_DIAS_EN_MILIS
        val loBusco = datos.actividad.any { a ->
            a.tipo == "BUSQUEDA" && a.fecha >= haceSieteDias &&
                (local.categoria.contains(a.valor, ignoreCase = true) ||
                    local.nombre.contains(a.valor, ignoreCase = true))
        }
        if (loBusco) puntos += 15

        if (local.id in datos.favoritos) puntos += 10

        val horasHabituales = datos.actividad
            .map { Instant.ofEpochMilli(it.fecha).atZone(ZoneId.systemDefault()).hour }
            .toSet()
        if (promo.bajaDemanda && horasHabituales.any { it in promo.horaInicio until promo.horaFin }) puntos += 10

        if (promo.id in datos.promocionesObtenidas) puntos -= 50

        return puntos
    }
}
