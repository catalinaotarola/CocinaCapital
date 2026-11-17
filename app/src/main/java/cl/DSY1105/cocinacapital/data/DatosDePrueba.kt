package cl.DSY1105.cocinacapital.data

import cl.DSY1105.cocinacapital.data.model.FechaEspecial
import cl.DSY1105.cocinacapital.data.model.Local
import cl.DSY1105.cocinacapital.data.model.Ocasion
import cl.DSY1105.cocinacapital.data.model.Preferencias
import cl.DSY1105.cocinacapital.data.model.Promocion
import cl.DSY1105.cocinacapital.data.model.Usuario
import cl.DSY1105.cocinacapital.utils.textoCondiciones
import java.time.LocalDate

object DatosDePrueba {
    val usuarioDemo = Usuario(
        nombre = "Sofía",
        correo = "sofia@correo.cl",
        clave = "cocina123",
        fechaConsentimiento = 0L,
        preferencias = Preferencias(
            categorias = setOf("Mariscos", "Sushi", "Café"),
            presupuesto = "\$10 a 20 mil",
            radioKm = 3
        )
    )

    val locales = listOf(
        Local(1, "Caleta Azul", "Mariscos", "Pescados y mariscos frescos con vista al mar.",
            "Av. Borgoño, Concón", -32.9270, -71.5180, "Todos los días · 12:30 a 23:00", "\$\$",
            "Reineta a la plancha · \$12.900\nChupe de jaibas · \$11.500\nCeviche mixto · \$9.900",
            "caletaazul@example.com"),
        Local(2, "Sushi Nami", "Sushi", "Rolls, ramen y cocina nikkei.",
            "Av. Las Gaviotas, Concón", -32.9235, -71.5120, "Todos los días · 13:00 a 23:30", "\$\$",
            "Roll acevichado · \$8.900\nRamen de cerdo · \$9.500\nGyozas (6) · \$5.900",
            "sushinami@example.com"),
        Local(3, "Café del Faro", "Café", "Café de especialidad, tortas y onces.",
            "Calle La Boca, Concón", -32.9330, -71.5245, "Lun a sáb · 9:00 a 20:00", "\$",
            "Capuchino · \$3.200\nTorta mil hojas · \$4.500\nOnce para dos · \$12.900",
            "cafedelfaro@example.com"),
        Local(4, "Fuego Criollo", "Parrilla", "Carnes a la parrilla y tablas para compartir.",
            "Av. Concón Reñaca, Concón", -32.9180, -71.5050, "Mar a dom · 13:00 a 23:00", "\$\$\$",
            "Lomo vetado 300 g · \$16.900\nTabla familiar · \$34.900\nChoripán · \$4.900",
            "fuegocriollo@example.com"),
        Local(5, "La Burguesía", "Hamburguesas", "Hamburguesas artesanales y papas rústicas.",
            "Av. Manantiales, Concón", -32.9300, -71.5100, "Todos los días · 12:00 a 23:00", "\$",
            "Burguesía clásica · \$7.900\nDoble queso · \$9.400\nPapas rústicas · \$3.900",
            "laburguesia@example.com"),
        Local(6, "Verde Raíz", "Vegana", "Cocina vegana de temporada.",
            "Calle Santa Laura, Concón", -32.9210, -71.5230, "Lun a sáb · 12:00 a 21:00", "\$\$",
            "Bowl de quinoa · \$8.500\nHamburguesa de lentejas · \$7.900\nBrownie sin azúcar · \$3.500",
            "verderaiz@example.com")
    )

    private fun promo(
        id: Int, localId: Int, titulo: String, etiqueta: String, dias: String,
        horaInicio: Int, horaFin: Int, diasVigencia: Long, cuposTotales: Int, cuposDisponibles: Int,
        precioTicket: Int, bajaDemanda: Boolean, ocasion: Ocasion
    ) = Promocion(
        id = id,
        localId = localId,
        titulo = titulo,
        etiqueta = etiqueta,
        condiciones = textoCondiciones(dias, horaInicio, horaFin),
        dias = dias,
        horaInicio = horaInicio,
        horaFin = horaFin,
        fechaTermino = LocalDate.now().plusDays(diasVigencia).toString(),
        cuposTotales = cuposTotales,
        cuposDisponibles = cuposDisponibles,
        precioTicket = precioTicket,
        bajaDemanda = bajaDemanda,
        ocasion = ocasion
    )

    val promociones = listOf(
        promo(1, 1, "20% de descuento en platos de fondo", "-20%", "1234", 15, 18, 30, 30, 12, 1990, true, Ocasion.PAREJA),
        promo(2, 1, "Postre de cortesía en cumpleaños", "Regalo", "1234567", 12, 23, 60, 50, 40, 990, false, Ocasion.FAMILIAR),
        promo(3, 2, "2x1 en rolls seleccionados", "2x1", "1234567", 16, 19, 45, 40, 25, 1490, true, Ocasion.AMIGOS),
        promo(4, 3, "15% en once para dos", "-15%", "12345", 16, 19, 45, 25, 20, 990, true, Ocasion.PAREJA),
        promo(5, 4, "25% en tabla familiar", "-25%", "1237", 13, 16, 30, 20, 8, 2990, true, Ocasion.FAMILIAR),
        promo(6, 5, "Hamburguesa y bebida a \$6.990", "\$6.990", "1234", 15, 18, 60, 40, 35, 990, true, Ocasion.AMIGOS),
        promo(7, 6, "Menú vegano 2x1 al almuerzo", "2x1", "12345", 15, 17, 40, 15, 2, 1490, true, Ocasion.AMIGOS)
    )

    fun fechasDemo(): List<FechaEspecial> {
        val enOchoDias = LocalDate.now().plusDays(8)
        val enQuinceDias = LocalDate.now().plusDays(15)
        return listOf(
            FechaEspecial(correoUsuario = usuarioDemo.correo, nombre = "Aniversario",
                ocasion = Ocasion.PAREJA, dia = enOchoDias.dayOfMonth, mes = enOchoDias.monthValue),
            FechaEspecial(correoUsuario = usuarioDemo.correo, nombre = "Cumpleaños de mamá",
                ocasion = Ocasion.FAMILIAR, dia = enQuinceDias.dayOfMonth, mes = enQuinceDias.monthValue)
        )
    }
}
