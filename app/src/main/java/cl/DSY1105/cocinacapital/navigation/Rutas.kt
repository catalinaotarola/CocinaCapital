package cl.DSY1105.cocinacapital.navigation

object Rutas {
    const val LOGIN = "login"
    const val REGISTRO = "registro"
    const val PREFERENCIAS = "preferencias"
    const val INICIO = "inicio"
    const val POLITICA = "politica"
    const val RECUPERAR = "recuperar"
    const val PANEL_LOCAL = "panel_local"
    const val CERCA = "cerca"
    const val CUPONES = "cupones"
    const val PERFIL = "perfil"
    const val NUEVA_FECHA = "nueva_fecha"

    const val DETALLE = "detalle/{localId}"
    const val CUPON = "cupon/{cuponId}"

    fun detalle(localId: Int) = "detalle/$localId"
    fun cupon(cuponId: Long) = "cupon/$cuponId"
}
