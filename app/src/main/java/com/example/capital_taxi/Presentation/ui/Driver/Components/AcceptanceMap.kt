import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
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
import org.osmdroid.events.MapListener
import org.osmdroid.events.ScrollEvent
import org.osmdroid.events.ZoomEvent
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
    var cameraMovedByUser by remember { mutableStateOf(false) }

    AndroidView(
        factory = { context ->
            MapView(context).apply {
                setTileSource(TileSourceFactory.MAPNIK)
                setMultiTouchControls(true)
                controller.setZoom(15.0)
                mapOrientation = 0.0f // تأكد إن اتجاه الخريطة ثابت

            }
        },
        update = { mapView ->
            mapView.overlays.clear()

            mapView.addMapListener(object : MapListener {
                override fun onScroll(event: ScrollEvent?): Boolean {
                    cameraMovedByUser = true
                    return true
                }

                override fun onZoom(event: ZoomEvent?): Boolean {
                    cameraMovedByUser = true
                    return true
                }
            })

            // Marker السائق
            driverLocation?.let { newLocation ->
                if (marker == null) {
                    val originalDrawable = ContextCompat.getDrawable(mapView.context, R.drawable.ic_car)
                    val bitmap = (originalDrawable as BitmapDrawable).bitmap
                    val scaledBitmap = Bitmap.createScaledBitmap(bitmap, 60, 60, true)
                    val scaledDrawable = BitmapDrawable(mapView.context.resources, scaledBitmap)

                    marker = Marker(mapView).apply {
                        icon = scaledDrawable
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                        position = newLocation
                        rotation = 0f
                    }
                    mapView.overlays.add(marker)
                } else {
                    val distanceMoved = if (lastDriverLocation != null) {
                        SphericalUtil.computeDistanceBetween(
                            LatLng(lastDriverLocation!!.latitude, lastDriverLocation!!.longitude),
                            LatLng(newLocation.latitude, newLocation.longitude)
                        )
                    } else {
                        Double.MAX_VALUE
                    }

                    if (distanceMoved > 3) {
                        updateCarMarkerSmoothly(
                            marker!!,
                            lastDriverLocation ?: newLocation,
                            newLocation,
                            mapView
                        )
                    }

                    mapView.overlays.add(marker)
                }

                lastDriverLocation = newLocation
            }

            // Marker الراكب
            passengerLocation?.let {
                val passengerMarker = Marker(mapView).apply {
                    position = it
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                }
                mapView.overlays.add(passengerMarker)
            }

            // Polyline للمسار
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
                if (!cameraMovedByUser) {
                    mapView.controller.setCenter(it)
                }
            }

            mapView.invalidate()
        },
        modifier = modifier.fillMaxSize()
    )
}

// ================== HELPER FUNCTIONS =======================

fun updateCarMarkerSmoothly(
    marker: Marker,
    startLoc: GeoPoint,
    endLoc: GeoPoint,
    mapView: MapView
) {
    val handler = Handler(Looper.getMainLooper())
    val duration = 1000L
    val frameRate = 16L
    val steps = (duration / frameRate).toInt()
    var step = 0

    val targetBearing = calculateBearing(startLoc, endLoc)
    val startBearing = marker.rotation
    val bearingDiff = calculateBearingDifference(startBearing, targetBearing)

    val runnable = object : Runnable {
        override fun run() {
            if (step <= steps) {
                val fraction = step.toFloat() / steps.toFloat()
                val easedFraction = easeInOutCubic(fraction)

                // تحريك الموقع
                val newPos = interpolatePosition(startLoc, endLoc, easedFraction.toDouble())
                marker.position = newPos

                // تدوير السيارة بسلاسة
                val newBearing = (startBearing + (bearingDiff * easedFraction) + 360) % 360
                marker.rotation = newBearing

                mapView.invalidate()
                step++
                handler.postDelayed(this, frameRate)
            }
        }
    }

    handler.post(runnable)
}

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

fun calculateBearingDifference(start: Float, end: Float): Float {
    var diff = end - start
    if (diff > 180) diff -= 360
    else if (diff < -180) diff += 360
    return diff
}

fun interpolatePosition(start: GeoPoint, end: GeoPoint, fraction: Double): GeoPoint {
    val lat = start.latitude + (end.latitude - start.latitude) * fraction
    val lon = start.longitude + (end.longitude - start.longitude) * fraction
    return GeoPoint(lat, lon)
}

fun easeInOutCubic(t: Float): Float {
    return if (t < 0.5f) 4 * t * t * t else 1 - Math.pow((-2 * t + 2).toDouble(), 3.0).toFloat() / 2
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
