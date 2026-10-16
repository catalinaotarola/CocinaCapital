package cl.DSY1105.cocinacapital.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.DSY1105.cocinacapital.data.model.Cupon
import cl.DSY1105.cocinacapital.data.model.EstadoCupon
import cl.DSY1105.cocinacapital.data.model.Promocion
import cl.DSY1105.cocinacapital.data.repository.CocinaRepository
import cl.DSY1105.cocinacapital.data.repository.ResultadoCupon
import cl.DSY1105.cocinacapital.utils.estaVencida
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class CuponesViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = CocinaRepository(app)
    private var ocupado = false

    fun cuponesDe(correo: String): Flow<List<Cupon>> = repo.cuponesDe(correo)

    fun cupon(id: Long): Flow<Cupon?> = repo.cupon(id)

    fun obtenerCupon(correo: String, promocionId: Int, alTerminar: (ResultadoCupon) -> Unit) {
        if (ocupado) return
        ocupado = true
        viewModelScope.launch {
            val resultado = repo.obtenerCupon(correo, promocionId)
            ocupado = false
            alTerminar(resultado)
        }
    }

    fun estadoVisible(cupon: Cupon, promo: Promocion?): EstadoCupon =
        if (cupon.estado == EstadoCupon.ACTIVO && promo != null && estaVencida(promo.fechaTermino)) {
            EstadoCupon.VENCIDO
        } else {
            cupon.estado
        }
}
