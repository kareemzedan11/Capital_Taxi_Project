import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.view.animation.LinearInterpolator
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.capital_taxi.R
import com.google.android.gms.maps.model.LatLng
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
    var lastDriverLocation by remember { mutableStateOf<GeoPoint?>(null) }
    val animatedBearing = remember { Animatable(0f) }
    var targetBearing by remember { mutableStateOf<Float?>(null) }

    // حساب التدوير المستهدف كل مرة الموقع يتغير
    LaunchedEffect(driverLocation) {
        driverLocation?.let { newLoc ->
            val lastLoc = lastDriverLocation
            if (lastLoc != null) {
                val target = calculateBearing(lastLoc, newLoc)
                val diff = calculateBearingDifference(animatedBearing.value, target)
                // تحديث التدوير بسلاسة
                animatedBearing.animateTo(
                    targetValue = (animatedBearing.value + diff + 360) % 360,
                    animationSpec = tween(durationMillis = 800)
                )
            } else {
                animatedBearing.snapTo(0f)
            }
            lastDriverLocation = newLoc
        }
    }

    AndroidView(
        factory = { context ->
            MapView(context).apply {
                setTileSource(TileSourceFactory.MAPNIK)
                setMultiTouchControls(true)
                controller.setZoom(15.0)
                mapOrientation = 0.0f
            }
        },
        update = { mapView ->
            mapView.overlays.clear()

            // Marker السائق
            driverLocation?.let { newLocation ->
                val marker = Marker(mapView).apply {
                    val originalDrawable = ContextCompat.getDrawable(mapView.context, R.drawable.ic_car)
                    val bitmap = (originalDrawable as BitmapDrawable).bitmap
                    val scaledBitmap = Bitmap.createScaledBitmap(bitmap, 100, 100, true)
                    val scaledDrawable = BitmapDrawable(mapView.context.resources, scaledBitmap)

                    icon = scaledDrawable
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                    position = newLocation
                    rotation = -animatedBearing.value  // هنا نطبق التدوير السلس
                }
                mapView.overlays.add(marker)
            }

            // باقي الكود (marker الراكب، polyline، تحريك الكاميرا) كما هو...
            passengerLocation?.let {
                val passengerMarker = Marker(mapView).apply {
                    position = it
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                }
                mapView.overlays.add(passengerMarker)
            }

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

            if (driverLocation != null) {
                mapView.controller.setCenter(driverLocation)
            } else if (passengerLocation != null) {
                mapView.controller.setCenter(passengerLocation)
            }

            mapView.invalidate()
        },
        modifier = modifier.fillMaxSize()
    )
}


// ================== HELPER FUNCTIONS =======================

fun updateCarMarkerSmoothlyPositionOnly(
    marker: Marker,
    startLoc: GeoPoint,
    endLoc: GeoPoint,
    mapView: MapView
) {
    val handler = Handler(Looper.getMainLooper())
    val duration = 1000L
    val startTime = SystemClock.uptimeMillis()
    val interpolator = LinearInterpolator()

    val runnable = object : Runnable {
        override fun run() {
            val elapsed = SystemClock.uptimeMillis() - startTime
            val t = interpolator.getInterpolation(elapsed.toFloat() / duration)

            if (t < 1.0) {
                val easedFraction = easeInOutCubic(t)
                val newPos = interpolatePosition(startLoc, endLoc, easedFraction.toDouble())
                marker.position = newPos

                mapView.invalidate()
                handler.postDelayed(this, 16L)
            } else {
                marker.position = endLoc
                mapView.invalidate()
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

    var bearing = Math.toDegrees(atan2(y, x))
    bearing = (bearing + 360) % 360
    return bearing.toFloat()
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
