package cl.DSY1105.cocinacapital.ui.components

import android.graphics.Color
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import cl.DSY1105.cocinacapital.R
import cl.DSY1105.cocinacapital.data.model.Local
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polygon
import java.io.File

@Composable
fun MapaLocales(
    latitud: Double,
    longitud: Double,
    radioKm: Int,
    locales: List<Local>,
    alTocarLocal: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val contexto = LocalContext.current

    val mapa = remember {
        Configuration.getInstance().apply {
            userAgentValue = contexto.packageName
            osmdroidBasePath = File(contexto.cacheDir, "osmdroid")
            osmdroidTileCache = File(contexto.cacheDir, "osmdroid/tiles")
        }
        MapView(contexto).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            controller.setZoom(14.5)
        }
    }

    DisposableEffect(Unit) {
        mapa.onResume()
        onDispose { mapa.onPause() }
    }

    AndroidView(
        factory = { mapa },
        modifier = modifier,
        update = { vista ->
            val centro = GeoPoint(latitud, longitud)
            vista.overlays.clear()

            val circulo = Polygon()
            circulo.setPoints(Polygon.pointsAsCircle(centro, radioKm * 1000.0))
            circulo.fillPaint.color = Color.argb(40, 47, 111, 168)
            circulo.outlinePaint.color = Color.rgb(47, 111, 168)
            circulo.outlinePaint.strokeWidth = 3f
            vista.overlays.add(circulo)

            locales.forEach { local ->
                val marcador = Marker(vista)
                marcador.position = GeoPoint(local.latitud, local.longitud)
                marcador.title = local.nombre
                marcador.setIcon(ContextCompat.getDrawable(vista.context, R.drawable.ic_marcador_local))
                marcador.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                marcador.setOnMarkerClickListener { _, _ ->
                    alTocarLocal(local.id)
                    true
                }
                vista.overlays.add(marcador)
            }

            val yo = Marker(vista)
            yo.position = centro
            yo.title = "Estás aquí"
            yo.setIcon(ContextCompat.getDrawable(vista.context, R.drawable.ic_mi_ubicacion))
            yo.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
            yo.setOnMarkerClickListener { _, _ -> true }
            vista.overlays.add(yo)

            vista.controller.setCenter(centro)
            vista.invalidate()
        }
    )
}
