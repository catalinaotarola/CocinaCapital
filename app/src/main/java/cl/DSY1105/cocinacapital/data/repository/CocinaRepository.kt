package cl.DSY1105.cocinacapital.data.repository

import android.content.Context
import cl.DSY1105.cocinacapital.data.AjustesManager
import cl.DSY1105.cocinacapital.data.DatosDePrueba
import cl.DSY1105.cocinacapital.data.model.Actividad
import cl.DSY1105.cocinacapital.data.model.Cupon
import cl.DSY1105.cocinacapital.data.model.EstadoCupon
import cl.DSY1105.cocinacapital.data.model.FechaEspecial
import cl.DSY1105.cocinacapital.data.model.Favorito
import cl.DSY1105.cocinacapital.data.model.Local
import cl.DSY1105.cocinacapital.data.model.Promocion
import cl.DSY1105.cocinacapital.data.model.Usuario
import cl.DSY1105.cocinacapital.data.remote.ClienteApi
import cl.DSY1105.cocinacapital.data.room.AppDatabase
import cl.DSY1105.cocinacapital.utils.Reglas
import cl.DSY1105.cocinacapital.utils.estaVencida
import cl.DSY1105.cocinacapital.utils.formatearFecha
import kotlinx.coroutines.flow.Flow
import java.time.LocalTime

sealed class ResultadoCupon {
    data class Exito(val cupon: Cupon) : ResultadoCupon()
    data class Error(val mensaje: String) : ResultadoCupon()
}

data class DatosRecomendacion(
    val fechas: List<FechaEspecial> = emptyList(),
    val favoritos: Set<Int> = emptySet(),
    val actividad: List<Actividad> = emptyList(),
    val promocionesObtenidas: Set<Int> = emptySet()
)

class CocinaRepository(context: Context) {
    private val db = AppDatabase.obtener(context)
    private val usuarioDao = db.usuarioDao()
    private val catalogoDao = db.catalogoDao()
    private val cuponDao = db.cuponDao()
    private val fechaDao = db.fechaEspecialDao()
    private val actividadDao = db.actividadDao()
    val ajustes = AjustesManager(context)

    suspend fun cargarDatosIniciales() {
        if (catalogoDao.contarLocales() == 0) {
            catalogoDao.insertarLocales(DatosDePrueba.locales)
            catalogoDao.insertarPromociones(DatosDePrueba.promociones)
            usuarioDao.insertar(DatosDePrueba.usuarioDemo)
            DatosDePrueba.fechasDemo().forEach { fechaDao.insertar(it) }
        }
    }

    suspend fun usuarios(): List<Usuario> = usuarioDao.obtenerTodos()
    suspend fun guardarUsuario(usuario: Usuario) = usuarioDao.insertar(usuario)
    suspend fun actualizarUsuario(usuario: Usuario) = usuarioDao.actualizar(usuario)

    suspend fun eliminarCuenta(usuario: Usuario) {
        cuponDao.borrarDe(usuario.correo)
        fechaDao.borrarDe(usuario.correo)
        actividadDao.borrarFavoritosDe(usuario.correo)
        actividadDao.borrarActividadDe(usuario.correo)
        usuarioDao.eliminar(usuario)
        ajustes.borrarSesion()
    }

    val locales: Flow<List<Local>> = catalogoDao.locales()
    val promociones: Flow<List<Promocion>> = catalogoDao.promociones()
    suspend fun localesAhora(): List<Local> = catalogoDao.localesAhora()
    suspend fun promocionesAhora(): List<Promocion> = catalogoDao.promocionesAhora()
    suspend fun crearPromocion(promocion: Promocion) = catalogoDao.insertarPromocion(promocion)
    suspend fun actualizarPromocion(promocion: Promocion) = catalogoDao.actualizarPromocion(promocion)

    suspend fun sincronizarCatalogo(): String? {
        if (!Reglas.USAR_BACKEND) return null
        return try {
            val locales = ClienteApi.servicio.locales()
            val promociones = ClienteApi.servicio.promociones()
            catalogoDao.insertarLocales(locales)
            catalogoDao.insertarPromociones(promociones)
            null
        } catch (e: Exception) {
            "Sin conexión con el servidor. Te mostramos las promociones guardadas en tu teléfono."
        }
    }

    suspend fun pedirRecuperacion(correo: String) {
        if (!Reglas.USAR_BACKEND) return
        try {
            ClienteApi.servicio.recuperarClave(mapOf("correo" to correo))
        } catch (e: Exception) {
        }
    }

    fun favoritosDe(correo: String): Flow<List<Int>> = actividadDao.favoritosDe(correo)

    suspend fun cambiarFavorito(correo: String, localId: Int, yaEsFavorito: Boolean) {
        val favorito = Favorito(correo, localId)
        if (yaEsFavorito) actividadDao.quitarFavorito(favorito) else actividadDao.agregarFavorito(favorito)
    }

    suspend fun registrarActividad(correo: String, tipo: String, valor: String) {
        actividadDao.registrar(
            Actividad(correoUsuario = correo, tipo = tipo, valor = valor, fecha = System.currentTimeMillis())
        )
    }

    fun vistasDelLocal(localId: Int): Flow<Int> = actividadDao.vistasDelLocal(localId.toString())

    fun fechasDe(correo: String): Flow<List<FechaEspecial>> = fechaDao.fechasDe(correo)
    suspend fun agregarFecha(fecha: FechaEspecial) = fechaDao.insertar(fecha)
    suspend fun eliminarFecha(fecha: FechaEspecial) = fechaDao.eliminar(fecha)

    fun cuponesDe(correo: String): Flow<List<Cupon>> = cuponDao.cuponesDe(correo)
    fun cupon(id: Long): Flow<Cupon?> = cuponDao.cupon(id)
    fun cuponesDelLocal(localId: Int): Flow<List<Cupon>> = cuponDao.cuponesDelLocal(localId)

    suspend fun obtenerCupon(correo: String, promocionId: Int): ResultadoCupon {
        val promo = catalogoDao.promocion(promocionId)
            ?: return ResultadoCupon.Error("La promoción ya no existe.")
        if (!promo.activa || estaVencida(promo.fechaTermino)) {
            return ResultadoCupon.Error("Esta promoción ya no está vigente.")
        }
        if (cuponDao.contarDe(correo, promocionId) > 0) {
            return ResultadoCupon.Error("Ya obtuviste este beneficio.")
        }
        if (catalogoDao.descontarCupo(promocionId) == 0) {
            return ResultadoCupon.Error("Se acabaron los cupos de esta promoción.")
        }
        val nuevo = Cupon(
            correoUsuario = correo,
            promocionId = promocionId,
            localId = promo.localId,
            codigo = generarCodigo(),
            fechaObtencion = System.currentTimeMillis()
        )
        val id = cuponDao.insertar(nuevo)
        return ResultadoCupon.Exito(nuevo.copy(id = id))
    }

    suspend fun validarCupon(codigo: String, localId: Int): ResultadoCupon {
        val cupon = cuponDao.buscarPorCodigo(codigo.trim().uppercase())
            ?: return ResultadoCupon.Error("No existe un cupón con ese código.")
        if (cupon.localId != localId) {
            return ResultadoCupon.Error("Este cupón es de otro local.")
        }
        if (cupon.estado == EstadoCupon.CANJEADO) {
            return ResultadoCupon.Error("Este cupón ya fue usado.")
        }
        val promo = catalogoDao.promocion(cupon.promocionId)
            ?: return ResultadoCupon.Error("La promoción ya no existe.")
        if (estaVencida(promo.fechaTermino)) {
            return ResultadoCupon.Error("Este cupón venció el ${formatearFecha(promo.fechaTermino)}.")
        }
        val hora = LocalTime.now().hour
        if (Reglas.VALIDAR_HORARIO && hora !in promo.horaInicio until promo.horaFin) {
            return ResultadoCupon.Error(
                "Este beneficio solo es válido de ${promo.horaInicio}:00 a ${promo.horaFin}:00."
            )
        }
        val canjeado = cupon.copy(estado = EstadoCupon.CANJEADO, fechaCanje = System.currentTimeMillis())
        cuponDao.actualizar(canjeado)
        return ResultadoCupon.Exito(canjeado)
    }

    private fun generarCodigo(): String {
        val caracteres = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"
        val parte = (1..4).map { caracteres.random() }.joinToString("")
        val numero = (10..99).random()
        return "CC-$parte-$numero"
    }

    suspend fun datosParaRecomendar(correo: String) = DatosRecomendacion(
        fechas = fechaDao.fechasDeAhora(correo),
        favoritos = actividadDao.favoritosDeAhora(correo).toSet(),
        actividad = actividadDao.actividadDeAhora(correo),
        promocionesObtenidas = cuponDao.cuponesDeAhora(correo).map { it.promocionId }.toSet()
    )
}
