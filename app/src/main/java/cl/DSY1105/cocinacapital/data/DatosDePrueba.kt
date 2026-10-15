package cl.DSY1105.cocinacapital.data

import cl.DSY1105.cocinacapital.data.model.FechaEspecial
import cl.DSY1105.cocinacapital.data.model.Local
import cl.DSY1105.cocinacapital.data.model.Ocasion
import cl.DSY1105.cocinacapital.data.model.Preferencias
import cl.DSY1105.cocinacapital.data.model.Promocion
import cl.DSY1105.cocinacapital.data.model.Usuario
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
            "Reineta a la plancha · \$12.900\nChupe de jaibas · \$11.500\nCeviche mixto · \$9.900"),
        Local(2, "Sushi Nami", "Sushi", "Rolls, ramen y cocina nikkei.",
            "Av. Las Gaviotas, Concón", -32.9235, -71.5120, "Todos los días · 13:00 a 23:30", "\$\$",
            "Roll acevichado · \$8.900\nRamen de cerdo · \$9.500\nGyozas (6) · \$5.900"),
        Local(3, "Café del Faro", "Café", "Café de especialidad, tortas y onces.",
            "Calle La Boca, Concón", -32.9330, -71.5245, "Lun a sáb · 9:00 a 20:00", "\$",
            "Capuchino · \$3.200\nTorta mil hojas · \$4.500\nOnce para dos · \$12.900"),
        Local(4, "Fuego Criollo", "Parrilla", "Carnes a la parrilla y tablas para compartir.",
            "Av. Concón Reñaca, Concón", -32.9180, -71.5050, "Mar a dom · 13:00 a 23:00", "\$\$\$",
            "Lomo vetado 300 g · \$16.900\nTabla familiar · \$34.900\nChoripán · \$4.900"),
        Local(5, "La Burguesía", "Hamburguesas", "Hamburguesas artesanales y papas rústicas.",
            "Av. Manantiales, Concón", -32.9300, -71.5100, "Todos los días · 12:00 a 23:00", "\$",
            "Burguesía clásica · \$7.900\nDoble queso · \$9.400\nPapas rústicas · \$3.900"),
        Local(6, "Verde Raíz", "Vegana", "Cocina vegana de temporada.",
            "Calle Santa Laura, Concón", -32.9210, -71.5230, "Lun a sáb · 12:00 a 21:00", "\$\$",
            "Bowl de quinoa · \$8.500\nHamburguesa de lentejas · \$7.900\nBrownie sin azúcar · \$3.500")
    )

    val promociones = listOf(
        Promocion(id = 1, localId = 1, titulo = "20% de descuento en platos de fondo", etiqueta = "-20%",
            condiciones = "Lun a jue · 15:00 a 18:00", horaInicio = 15, horaFin = 18,
            fechaTermino = "2026-11-30", cuposTotales = 30, cuposDisponibles = 12,
            bajaDemanda = true, ocasion = Ocasion.PAREJA),
        Promocion(id = 2, localId = 1, titulo = "Postre de cortesía en cumpleaños", etiqueta = "Regalo",
            condiciones = "Grupos de 4 o más · todos los días", horaInicio = 12, horaFin = 23,
            fechaTermino = "2026-12-31", cuposTotales = 50, cuposDisponibles = 40,
            bajaDemanda = false, ocasion = Ocasion.FAMILIAR),
        Promocion(id = 3, localId = 2, titulo = "2x1 en rolls seleccionados", etiqueta = "2x1",
            condiciones = "Todos los días · 16:00 a 19:00", horaInicio = 16, horaFin = 19,
            fechaTermino = "2026-12-15", cuposTotales = 40, cuposDisponibles = 25,
            bajaDemanda = true, ocasion = Ocasion.AMIGOS),
        Promocion(id = 4, localId = 3, titulo = "15% en once para dos", etiqueta = "-15%",
            condiciones = "Lun a vie · 16:00 a 19:00", horaInicio = 16, horaFin = 19,
            fechaTermino = "2026-12-15", cuposTotales = 25, cuposDisponibles = 20,
            bajaDemanda = true, ocasion = Ocasion.PAREJA),
        Promocion(id = 5, localId = 4, titulo = "25% en tabla familiar", etiqueta = "-25%",
            condiciones = "Dom a mié · 13:00 a 16:00", horaInicio = 13, horaFin = 16,
            fechaTermino = "2026-11-30", cuposTotales = 20, cuposDisponibles = 8,
            bajaDemanda = true, ocasion = Ocasion.FAMILIAR),
        Promocion(id = 6, localId = 5, titulo = "Hamburguesa y bebida a \$6.990", etiqueta = "\$6.990",
            condiciones = "Lun a jue · 15:00 a 18:00", horaInicio = 15, horaFin = 18,
            fechaTermino = "2026-12-31", cuposTotales = 40, cuposDisponibles = 35,
            bajaDemanda = true, ocasion = Ocasion.AMIGOS),
        Promocion(id = 7, localId = 6, titulo = "Menú vegano 2x1 al almuerzo", etiqueta = "2x1",
            condiciones = "Lun a vie · 15:00 a 17:00", horaInicio = 15, horaFin = 17,
            fechaTermino = "2026-12-20", cuposTotales = 15, cuposDisponibles = 2,
            bajaDemanda = true, ocasion = Ocasion.AMIGOS)
    )

    fun fechasDemo(): List<FechaEspecial> {
        val enDosDias = LocalDate.now().plusDays(2)
        return listOf(
            FechaEspecial(correoUsuario = usuarioDemo.correo, nombre = "Aniversario",
                ocasion = Ocasion.PAREJA, dia = enDosDias.dayOfMonth, mes = enDosDias.monthValue),
            FechaEspecial(correoUsuario = usuarioDemo.correo, nombre = "Cumpleaños de mamá",
                ocasion = Ocasion.FAMILIAR, dia = 14, mes = 11)
        )
    }
}
