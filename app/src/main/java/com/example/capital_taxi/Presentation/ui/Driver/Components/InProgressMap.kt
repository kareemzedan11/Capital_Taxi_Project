package com.example.capital_taxi.Presentation.ui.Driver.Components
// Android Imports
import android.annotation.SuppressLint
import android.graphics.Color
import android.location.Location
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.compose.ui.viewinterop.AndroidView
import calculateBearing
import com.example.capital_taxi.R
import com.example.myapplication.interpolateLocation
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.SphericalUtil
import findNearestIndex
import kotlinx.coroutines.launch
import org.osmdroid.events.MapListener
import org.osmdroid.events.ScrollEvent
import org.osmdroid.events.ZoomEvent
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import updateCarMarkerSmoothly


// Your custom imports

@SuppressLint("RememberReturnType")
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

    val mapView = remember { MapView(context) }

    fun calculateDistance(loc1: GeoPoint, loc2: GeoPoint): Double {
        val results = FloatArray(1)
        Location.distanceBetween(
            loc1.latitude, loc1.longitude,
            loc2.latitude, loc2.longitude,
            results
        )
        return results[0].toDouble()
    }

    // تشغيل الأنيميشن عند تغير الموقع
    LaunchedEffect(currentLocation, previousLocation) {
        if (currentLocation != null && previousLocation != null && currentLocation != previousLocation) {
            val distance = calculateDistance(previousLocation, currentLocation)
            if (distance > 1.0) {
                val newBearing = calculateBearing(previousLocation, currentLocation).toFloat()
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
        factory = { mapView.apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            controller.setZoom(16.0)

            addMapListener(object : MapListener {
                override fun onScroll(event: ScrollEvent?) = run {
                    cameraMovedByUser = true
                    true
                }

                override fun onZoom(event: ZoomEvent?) = run {
                    cameraMovedByUser = true
                    true
                }
            })
        }},
        update = { map ->
            map.overlays.clear()

            // سيارة السائق
            animatedPosition.value?.let { pos ->
                val driverMarker = Marker(map).apply {
                    position = pos
                    icon = ContextCompat.getDrawable(context, R.drawable.ic_car)
                    rotation = animatedBearing.value
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                    infoWindow = null
                }
                map.overlays.add(driverMarker)

                if (!cameraMovedByUser) {
                    map.controller.animateTo(pos)
                }
            }

            // ماركر الراكب
            destination?.let {
                val marker = Marker(map).apply {
                    position = it
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                }
                map.overlays.add(marker)
            }

            // رسم الاتجاهات المتبقية
            val path = if (currentLocation != null && directions.isNotEmpty()) {
                val nearest = findNearestIndex(currentLocation, directions)
                directions.subList(nearest, directions.size)
            } else directions

            if (path.isNotEmpty()) {
                val polyline = Polyline(map).apply {
                    setPoints(path)
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
