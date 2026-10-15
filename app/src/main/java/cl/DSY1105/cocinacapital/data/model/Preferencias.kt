package cl.DSY1105.cocinacapital.data.model

data class Preferencias(
    val categorias: Set<String> = emptySet(),
    val presupuesto: String = "",
    val restricciones: Set<String> = emptySet(),
    val radioKm: Int = 3  // RN-08: radio de cercanía por defecto
)
object OpcionesPreferencias {
    val categorias = listOf(
        "Mariscos", "Sushi", "Parrilla", "Hamburguesas", "Café",
        "Pizza", "Peruana", "Vegana", "Postres"
    )
    val presupuestos = listOf("Hasta \$10 mil", "\$10 a 20 mil", "Más de \$20 mil")
    val restricciones = listOf("Vegetariana", "Sin gluten", "Sin lactosa")
}
//Usamos Set (conjunto) para las categorías
// porque una misma categoría no puede elegirse dos veces.
