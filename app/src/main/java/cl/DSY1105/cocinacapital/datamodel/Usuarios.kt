package cl.DSY1105.cocinacapital.data.model

import cl.DSY1105.cocinacapital.datamodel.Preferencias

data class Usuario(
    val nombre: String,
    val correo: String,
    val clave: String,
    val fechaConsentimiento: Long,
    val preferencias: Preferencias = Preferencias()
)
