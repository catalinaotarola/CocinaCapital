package cl.DSY1105.cocinacapital.utils

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

fun tienePermisoUbicacion(context: Context): Boolean =
    ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) ==
        PackageManager.PERMISSION_GRANTED ||
        ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
        PackageManager.PERMISSION_GRANTED

@SuppressLint("MissingPermission")
fun pedirUbicacionActual(
    context: Context,
    alRecibir: (Double, Double) -> Unit,
    alFallar: () -> Unit
) {
    if (!tienePermisoUbicacion(context)) {
        alFallar()
        return
    }
    LocationServices.getFusedLocationProviderClient(context)
        .getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
        .addOnSuccessListener { ubicacion ->
            if (ubicacion != null) alRecibir(ubicacion.latitude, ubicacion.longitude) else alFallar()
        }
        .addOnFailureListener { alFallar() }
}
