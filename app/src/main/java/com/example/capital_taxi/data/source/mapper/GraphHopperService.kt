package com.example.myapplication


import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.capital_taxi.R
import com.example.capital_taxi.domain.shared.decodePolyline
import com.example.capital_taxi.domain.storedPoints
import com.google.android.gms.location.*
import kotlinx.coroutines.delay
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline


@Composable
fun MapViewComposable(
    startPoint: GeoPoint? = null,
    endPoint: GeoPoint? = null,
    routePoints : List<GeoPoint>? = null,
    driverLocation: GeoPoint?=null
) {
    val context = LocalContext.current
    val mapView = remember { MapView(context) }

    // تعيين المصدر لأول مرة
    LaunchedEffect(mapView) {
        mapView.setTileSource(org.osmdroid.tileprovider.tilesource.TileSourceFactory.MAPNIK)
        mapView.controller.setZoom(15)
        mapView.controller.setCenter(startPoint ?: GeoPoint(30.033, 31.233))
    }

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { mapView },
        update = { mapView ->

            // إضافة نقطة البداية والنهاية
            startPoint?.let { start ->
                val startMarker = Marker(mapView)
                startMarker.position = start
                startMarker.title = "Start"
                mapView.overlays.add(startMarker)
            }

            endPoint?.let { end ->
                val endMarker = Marker(mapView)
                endMarker.position = end
                endMarker.title = "End"
                mapView.overlays.add(endMarker)
            }
            var encodedPolyline = storedPoints
            // إضافة المسار بين النقاط إذا كان موجودًا
            encodedPolyline?.let { encoded ->
                val routePoints = decodePolyline(encoded)
                val polyline = Polyline(mapView)
                polyline.setPoints(routePoints)
                mapView.overlays.add(polyline)
            }

            // تحديث الخريطة عند تغيير النقاط
            startPoint?.let { mapView.controller.setCenter(it) }
            endPoint?.let { mapView.controller.setZoom(15) }
        }
    )
}@Composable
fun DriverMapView(driverLocation: GeoPoint?, bearing: Float) {
    val context = LocalContext.current
    val mapView = remember { MapView(context) }

    LaunchedEffect(mapView) {
        mapView.setTileSource(org.osmdroid.tileprovider.tilesource.TileSourceFactory.MAPNIK)
        mapView.controller.setZoom(15)
        driverLocation?.let { mapView.controller.setCenter(it) }
    }

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { mapView },
        update = { mapView ->
            mapView.overlays.clear()

            driverLocation?.let { location ->
                val driverMarker = Marker(mapView).apply {
                    position = location
                    title = "Driver Location"
                    icon = ContextCompat.getDrawable(context, R.drawable.ic_car)
                    rotation = bearing // ضبط اتجاه السيارة
                }
                mapView.overlays.add(driverMarker)

                // تحريك الكاميرا بسلاسة مع السيارة
                mapView.controller.animateTo(location)
            }
        }
    )
}

/**
 * دالة لتحريك السيارة من نقطة البداية إلى نقطة النهاية خلال 5 دقائق (300 ثانية)
 */
suspend fun animateCarMovement(
    start: GeoPoint,
    end: GeoPoint,
    updateLocation: (GeoPoint) -> Unit
) {
    val duration = 300 // مدة الحركة بالثواني (5 دقائق)
    val steps = duration // عدد التحديثات (كل ثانية)

    val latDiff = (end.latitude - start.latitude) / steps
    val lonDiff = (end.longitude - start.longitude) / steps

    for (i in 1..steps) {
        val newLat = start.latitude + (latDiff * i)
        val newLon = start.longitude + (lonDiff * i)

        updateLocation(GeoPoint(newLat, newLon))
        delay(1000) // تأخير 1 ثانية لكل حركة
    }
}

fun decodePolyline(encoded: String): List<GeoPoint> {
    val polyline = mutableListOf<GeoPoint>()
    var index = 0
    val len = encoded.length
    var lat = 0
    var lng = 0

    while (index < len) {
        var shift = 0
        var result = 0
        while (true) {
            val byte = encoded[index++].toInt() - 63
            result = result or ((byte and 0x1f) shl shift)
            shift += 5
            if (byte < 0x20) break
        }
        val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
        lat += dlat

        shift = 0
        result = 0
        while (true) {
            val byte = encoded[index++].toInt() - 63
            result = result or ((byte and 0x1f) shl shift)
            shift += 5
            if (byte < 0x20) break
        }
        val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
        lng += dlng

        polyline.add(GeoPoint(lat / 1E5, lng / 1E5))
    }
    return polyline
}

@Composable
fun driverMapViewComposable(
    isDriver: Boolean, // هل المستخدم سائق؟
    startPoint: GeoPoint? = null,
    endPoint: GeoPoint? = null
) {
    val context = LocalContext.current
    val mapView = remember { MapView(context) }
    var userLocation by remember { mutableStateOf<GeoPoint?>(null) }
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    // طلب الأذونات
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                getUserLocation(fusedLocationClient) { location ->
                    userLocation = location
                }
            }
        }
    )

    LaunchedEffect(Unit) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            getUserLocation(fusedLocationClient) { location ->
                userLocation = location
            }
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }
    LaunchedEffect(mapView, userLocation) {
        mapView.setTileSource(org.osmdroid.tileprovider.tilesource.TileSourceFactory.MAPNIK)

        val zoomLevel = if (isDriver) 18.0 else 15.0  // تكبير أكثر للسائقين
        mapView.controller.setZoom(zoomLevel)

        mapView.controller.setCenter(userLocation ?: startPoint ?: GeoPoint(30.033, 31.233))
    }


    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { mapView },
        update = { mapView ->
            // إزالة جميع العلامات السابقة قبل تحديث الموقع
            mapView.overlays.removeIf { it is Marker }

            // إضافة نقطة البداية والنهاية
            startPoint?.let { start ->
                val startMarker = Marker(mapView)
                startMarker.position = start
                startMarker.title = "Start"
                mapView.overlays.add(startMarker)
            }

            endPoint?.let { end ->
                val endMarker = Marker(mapView)
                endMarker.position = end
                endMarker.title = "End"
                mapView.overlays.add(endMarker)
            }

            // تحديث الموقع الفعلي
            userLocation?.let { location ->
                val userMarker = Marker(mapView)
                userMarker.position = location
                userMarker.title = "Your Location"

                if (isDriver) {
                    userMarker.setIcon(context.getDrawable(R.drawable.uber)) // أيقونة السائق
                }

                mapView.overlays.add(userMarker)

                // تحديث الكاميرا إلى الموقع الجديد
                mapView.controller.animateTo(location)
            }

            // رسم المسار
            storedPoints?.let { encodedPolyline ->
                val routePoints = decodePolyline(encodedPolyline)
                val polyline = Polyline(mapView)
                polyline.setPoints(routePoints)
                mapView.overlays.add(polyline)
            }

            mapView.invalidate() // إعادة رسم الخريطة بعد التحديث
        }
    )

}

// دالة لجلب الموقع الفعلي بأمان
@SuppressLint("MissingPermission")
private fun getUserLocation(
    fusedLocationClient: FusedLocationProviderClient,
    onLocationReceived: (GeoPoint?) -> Unit
) {
    fusedLocationClient.lastLocation.addOnSuccessListener { location ->
        location?.let {
            onLocationReceived(GeoPoint(it.latitude, it.longitude))
        }
    }
}
