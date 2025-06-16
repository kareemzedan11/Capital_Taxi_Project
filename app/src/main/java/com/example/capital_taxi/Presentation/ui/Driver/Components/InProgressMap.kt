package com.example.capital_taxi.Presentation.ui.Driver.Components
// Android Imports
import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.location.Location
import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
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
import com.google.maps.android.SphericalUtil
import kotlinx.coroutines.launch
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
fun InProgressMap(
    currentLocation: GeoPoint? = null,
    previousLocation: GeoPoint? = null,
    destination: GeoPoint? = null,
    directions: List<GeoPoint> = emptyList(),
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val animatedBearing = remember { Animatable(0f) }
    val animatedPosition = remember { mutableStateOf<GeoPoint?>(null) }
    val animationProgress = remember { Animatable(0f) }
    var cameraMovedByUser by remember { mutableStateOf(false) }

    val mapView = remember {
        MapView(context).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            controller.setZoom(16.0)
        }
    }

    DisposableEffect(Unit) {
        val listener = object : MapListener {
            override fun onScroll(event: ScrollEvent?): Boolean {
                cameraMovedByUser = true
                return true
            }
            override fun onZoom(event: ZoomEvent?): Boolean {
                cameraMovedByUser = true
                return true
            }
        }
        mapView.addMapListener(listener)

        onDispose {
            mapView.removeMapListener(listener)
            mapView.onDetach()
        }
    }

    // حدد دوال مساعدة خارج Composable لو تفضل، لكن هنا للمثال داخلية

    fun calculateDistance(loc1: GeoPoint, loc2: GeoPoint): Double {
        val results = FloatArray(1)
        Location.distanceBetween(
            loc1.latitude, loc1.longitude,
            loc2.latitude, loc2.longitude,
            results
        )
        return results[0].toDouble()
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

    fun interpolateLocation(start: GeoPoint, end: GeoPoint, fraction: Float): GeoPoint {
        val lat = start.latitude + (end.latitude - start.latitude) * fraction
        val lon = start.longitude + (end.longitude - start.longitude) * fraction
        return GeoPoint(lat, lon)
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

    // تشغيل الأنيميشن عند تغير الموقع
    LaunchedEffect(currentLocation, previousLocation) {
        if (currentLocation != null && previousLocation != null && currentLocation != previousLocation) {
            val distance = calculateDistance(previousLocation, currentLocation)
            if (distance > 3.0) {
                val newBearing = calculateBearing(previousLocation, currentLocation)
                animationProgress.snapTo(0f)
                animatedPosition.value = previousLocation

                launch {
                    animationProgress.animateTo(
                        targetValue = 1f,
                        animationSpec = tween(durationMillis = 1900, easing = LinearEasing)
                    )
                    animatedPosition.value = currentLocation
                }

                launch {
                    val currentRotation = animatedBearing.value
                    var delta = newBearing - currentRotation
                    delta = (delta + 180) % 360 - 180
                    val targetRotation = currentRotation + delta

                    animatedBearing.animateTo(
                        targetValue = targetRotation,
                        animationSpec = tween(durationMillis = 500, easing = LinearOutSlowInEasing)
                    )
                }

                cameraMovedByUser = false
            } else {
                animatedPosition.value = currentLocation
            }
        } else if (currentLocation != null && animatedPosition.value == null) {
            animatedPosition.value = currentLocation
            mapView.controller.setCenter(currentLocation)
        }
    }

    // تحديث الموقع أثناء الأنيميشن
    LaunchedEffect(animationProgress.value) {
        if (currentLocation != null && previousLocation != null &&
            animationProgress.value > 0f && animationProgress.value < 1f
        ) {
            animatedPosition.value = interpolateLocation(
                previousLocation,
                currentLocation,
                animationProgress.value
            )
        }
    }

    AndroidView(
        factory = { mapView },
        update = { map ->
            map.overlays.clear()
            Log.d("InProgressMap", "Directions size: ${directions.size}")
            Log.d("InProgressMap", "Nearest index: ${findNearestIndex(currentLocation!!, directions)}")

            // ماركر سيارة السائق مع التدوير
            animatedPosition.value?.let { pos ->
                val originalDrawable = ContextCompat.getDrawable(context, R.drawable.ic_car)
                val bitmap = (originalDrawable as BitmapDrawable).bitmap
                val scaledBitmap = Bitmap.createScaledBitmap(bitmap, 60, 60, true)
                val scaledDrawable = BitmapDrawable(context.resources, scaledBitmap)

                val driverMarker = Marker(map).apply {
                    position = pos
                    icon = scaledDrawable
                    rotation = -animatedBearing.value // تدوير معكوس في OSMDroid
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                    infoWindow = null
                }
                map.overlays.add(driverMarker)

                if (!cameraMovedByUser) {
                    map.controller.animateTo(pos)
                }
            }

            // ماركر الوجهة
            destination?.let {
                val destMarker = Marker(map).apply {
                    position = it
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                }
                map.overlays.add(destMarker)
            }

            // رسم الخط المتبقي للمسار
            val remainingPath = if (currentLocation != null && directions.isNotEmpty()) {
                val nearestIndex = findNearestIndex(currentLocation, directions)
                directions.subList(nearestIndex, directions.size)
            } else directions

            if (remainingPath.isNotEmpty()) {
                val polyline = Polyline(map).apply {
                    setPoints(remainingPath)
                    outlinePaint.color = Color.GREEN
                    outlinePaint.strokeWidth = 8f
                }
                map.overlays.add(polyline)
            }

            map.invalidate()
        },
        modifier = modifier.fillMaxSize()
    )
}
