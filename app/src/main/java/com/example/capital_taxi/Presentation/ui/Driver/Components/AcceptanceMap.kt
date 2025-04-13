import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.compose.ui.viewinterop.AndroidView
import com.example.capital_taxi.R
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.FirebaseFirestore
import com.google.maps.android.SphericalUtil
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin


@Composable
fun AcceptanceMap(
    driverLocation: GeoPoint? = null,
    passengerLocation: GeoPoint? = null,
    directions: List<GeoPoint> = emptyList(),
    modifier: Modifier = Modifier
) {
    var marker: Marker? by remember { mutableStateOf(null) }
    var lastDriverLocation: GeoPoint? by remember { mutableStateOf(null) }

    AndroidView(
        factory = { context ->
            MapView(context).apply {
                setTileSource(TileSourceFactory.MAPNIK)
                setMultiTouchControls(true)
                controller.setZoom(15.0)
            }
        },
        update = { mapView ->
            mapView.overlays.clear()

            // إضافة موقع السائق (أيقونة سيارة)
            driverLocation?.let { newLocation ->
                if (marker == null) {
                    marker = Marker(mapView).apply {
                        icon = ContextCompat.getDrawable(mapView.context, R.drawable.ic_car)
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                        position = newLocation
                        rotation = 0f
                    }
                    mapView.overlays.add(marker)
                } else {
                    if (lastDriverLocation == null || lastDriverLocation != newLocation) {
                        updateCarMarkerSmoothly(
                            marker!!,
                            lastDriverLocation ?: newLocation,
                            newLocation,
                            mapView
                        )
                    }
                    mapView.overlays.add(marker) // إعادة إضافته بعد clear
                }

                lastDriverLocation = newLocation
            }

            // إضافة موقع الراكب
            passengerLocation?.let {
                val passengerMarker = Marker(mapView).apply {
                    position = it
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                }
                mapView.overlays.add(passengerMarker)
            }

            // إضافة الاتجاهات
            val remainingPath = if (driverLocation != null && directions.isNotEmpty()) {
                val nearestIndex = findNearestIndex(driverLocation, directions)
                directions.subList(nearestIndex, directions.size)
            } else {
                directions
            }

            if (remainingPath.isNotEmpty()) {
                val polyline = Polyline(mapView).apply {
                    setPoints(remainingPath)
                    outlinePaint.color = Color.BLUE
                    outlinePaint.strokeWidth = 8f
                }
                mapView.overlays.add(polyline)
            }


            // تحديث مركز الخريطة
            val lastLocation = driverLocation ?: passengerLocation
            lastLocation?.let {
                mapView.controller.setCenter(it)
            }

            mapView.invalidate()
        },
        modifier = modifier.fillMaxSize()
    )
}
fun findNearestIndex(current: GeoPoint, path: List<GeoPoint>): Int {
    var minDistance = Double.MAX_VALUE
    var nearestIndex = 0

    path.forEachIndexed { index, point ->
        val distance = SphericalUtil.computeDistanceBetween(
            LatLng(current.latitude, current.longitude),
            LatLng(point.latitude, point.longitude)
        )
        if (distance < minDistance) {
            minDistance = distance
            nearestIndex = index
        }
    }

    return nearestIndex
}

fun updateCarMarkerSmoothly(
    marker: Marker,
    startLoc: GeoPoint,
    endLoc: GeoPoint,
    mapView: MapView
) {
    val handler = Handler(Looper.getMainLooper())
    val duration = 1000L // زمن التحريك 1 ثانية
    val frameRate = 16L // ~60 إطار في الثانية
    val steps = (duration / frameRate).toInt()
    var step = 0

    // حساب الاتجاه المبدئي
    val initialBearing = calculateBearing(startLoc, endLoc)
    val startBearing = marker.rotation
    val bearingDiff = calculateBearingDifference(startBearing, initialBearing)

    val runnable = object : Runnable {
        override fun run() {
            if (step <= steps) {
                val fraction = step.toFloat() / steps.toFloat()

                // استخدام دالة ease-in-out لجعل الحركة أكثر سلاسة
                val easedFraction = easeInOutCubic(fraction)

                // تحديث الموقع
                val newPos = interpolatePosition(startLoc, endLoc, easedFraction.toDouble())
                marker.position = newPos

                // تحديث الاتجاه تدريجياً
                val newBearing = startBearing + (bearingDiff * easedFraction)
                marker.rotation = newBearing

                mapView.invalidate()
                step++
                handler.postDelayed(this, frameRate)
            }
        }
    }

    handler.post(runnable)
}

// دالة لحساب الفرق بين اتجاهين مع التعامل مع الزوايا الدائرية
fun calculateBearingDifference(start: Float, end: Float): Float {
    var diff = end - start
    when {
        diff > 180 -> diff -= 360
        diff < -180 -> diff += 360
    }
    return diff
}

// دالة لإنشاء تأثير ease-in-out للحركة
fun easeInOutCubic(t: Float): Float {
    return if (t < 0.5f) 4 * t * t * t else 1 - Math.pow((-2 * t + 2).toDouble(), 3.0).toFloat() / 2
}
// حساب الاتجاه بين نقطتين (bearing)
fun calculateBearing(start: GeoPoint, end: GeoPoint): Float {
    val lat1 = Math.toRadians(start.latitude)
    val lon1 = Math.toRadians(start.longitude)
    val lat2 = Math.toRadians(end.latitude)
    val lon2 = Math.toRadians(end.longitude)

    val dLon = lon2 - lon1
    val y = sin(dLon) * cos(lat2)
    val x = cos(lat1) * sin(lat2) - sin(lat1) * cos(lat2) * cos(dLon)
    val bearing = Math.toDegrees(atan2(y, x))
    return ((bearing + 360) % 360).toFloat()
}

// حساب الموقع بين نقطتين مع نسبة معينة (interpolation)
fun interpolatePosition(start: GeoPoint, end: GeoPoint, fraction: Double): GeoPoint {
    val lat = start.latitude + (end.latitude - start.latitude) * fraction
    val lon = start.longitude + (end.longitude - start.longitude) * fraction
    return GeoPoint(lat, lon)
}
