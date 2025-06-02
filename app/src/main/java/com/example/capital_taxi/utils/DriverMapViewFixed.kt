package com.example.capital_taxi.utils

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.location.Location
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.capital_taxi.R
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapListener
import org.osmdroid.events.ScrollEvent
import org.osmdroid.events.ZoomEvent
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import java.lang.Math.toDegrees
import java.lang.Math.toRadians
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

fun interpolateLocation(start: GeoPoint, end: GeoPoint, fraction: Float): GeoPoint {
    val lat = (end.latitude - start.latitude) * fraction + start.latitude
    val lon = (end.longitude - start.longitude) * fraction + start.longitude
    return GeoPoint(lat, lon)
}

@Composable
fun DriverMapView(
    currentLocation: GeoPoint?,
    previousLocation: GeoPoint?
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        Configuration.getInstance().load(context, context.getSharedPreferences("osmdroid", 0))
    }

    val mapView = remember { MapView(context) }
    val animatedBearing = remember { Animatable(0f) }
    val animatedPosition = remember { mutableStateOf<GeoPoint?>(null) }
    val animationProgress = remember { Animatable(0f) }
    var cameraMovedByUser by remember { mutableStateOf(false) }

    fun calculateDistance(loc1: GeoPoint, loc2: GeoPoint): Double {
        val results = FloatArray(1)
        Location.distanceBetween(
            loc1.latitude, loc1.longitude,
            loc2.latitude, loc2.longitude,
            results
        )
        return results[0].toDouble()
    }

    fun calculateBearing(start: GeoPoint, end: GeoPoint): Double {
        val lat1 = Math.toRadians(start.latitude)
        val lon1 = Math.toRadians(start.longitude)
        val lat2 = Math.toRadians(end.latitude)
        val lon2 = Math.toRadians(end.longitude)

        val dLon = lon2 - lon1
        val y = sin(dLon) * cos(lat2)
        val x = cos(lat1) * sin(lat2) - sin(lat1) * cos(lat2) * cos(dLon)
        return (Math.toDegrees(atan2(y, x)) + 360) % 360
    }

    LaunchedEffect(currentLocation, previousLocation) {
        if (currentLocation != null && previousLocation != null && currentLocation != previousLocation) {
            val distance = calculateDistance(previousLocation, currentLocation)
            if (distance > 2.5) {
                val rawBearing = calculateBearing(previousLocation, currentLocation).toFloat()

                // تدوير ناعم مثل rotateMarker
                val startRotation = animatedBearing.value
                val endRotation = rawBearing

                launch {
                    animate(
                        initialValue = 0f,
                        targetValue = 1f,
                        animationSpec = tween(durationMillis = 800, easing = LinearEasing)
                    ) { value, _ ->
                        val rotation = (1 - value) * startRotation + value * endRotation
                        launch {
                            animatedBearing.animateTo(
                                targetValue = rawBearing,
                                animationSpec = tween(durationMillis = 800, easing = LinearEasing)
                            )
                        }

                    }
                }

                animationProgress.snapTo(0f)
                animatedPosition.value = previousLocation

                launch {
                    animationProgress.animateTo(
                        targetValue = 1f,
                        animationSpec = tween(durationMillis = 1800, easing = LinearEasing)
                    )
                    animatedPosition.value = currentLocation
                }

                cameraMovedByUser = false
            }
        } else if (currentLocation != null && animatedPosition.value == null) {
            animatedPosition.value = currentLocation
            mapView.controller.setCenter(currentLocation)
        }
    }

    LaunchedEffect(animationProgress.value) {
        if (currentLocation != null && previousLocation != null &&
            animationProgress.value in 0f..1f
        ) {
            animatedPosition.value = interpolateLocation(
                previousLocation,
                currentLocation,
                animationProgress.value
            )
        }
    }

    LaunchedEffect(mapView) {
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.controller.setZoom(18.0)
        mapView.setMultiTouchControls(true)

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

        currentLocation?.let {
            mapView.controller.setCenter(it)
        }
    }

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { mapView },
        update = { map ->
            map.overlays.clear()

            animatedPosition.value?.let { location ->
                val driverMarker = Marker(map).apply {
                    position = location

                    val originalDrawable = ContextCompat.getDrawable(context, R.drawable.ic_car)
                    val bitmap = (originalDrawable as BitmapDrawable).bitmap
                    val scaledBitmap = Bitmap.createScaledBitmap(bitmap, 70, 70, true)

                    icon = BitmapDrawable(context.resources, scaledBitmap)

                    // تصحيح الاتجاه (لو الأيقونة وشها يمين)
                    rotation = -animatedBearing.value

                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                    infoWindow = null
                }

                map.overlays.add(driverMarker)

                if (!cameraMovedByUser) {
                    map.controller.animateTo(location)
                }
            }

            map.invalidate()
        }
    )
}
