package com.example.capital_taxi.utils

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.capital_taxi.R
import com.google.android.gms.location.*
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapListener
import org.osmdroid.events.ScrollEvent
import org.osmdroid.events.ZoomEvent
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

fun calculateBearing(startPoint: GeoPoint, endPoint: GeoPoint): Double {
    val startLocation = Location("").apply {
        latitude = startPoint.latitude
        longitude = startPoint.longitude
    }
    val endLocation = Location("").apply {
        latitude = endPoint.latitude
        longitude = endPoint.longitude
    }
    return startLocation.bearingTo(endLocation).toDouble()
}

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
        val results = FloatArray(3)
        Location.distanceBetween(
            loc1.latitude, loc1.longitude,
            loc2.latitude, loc2.longitude,
            results
        )
        return results[0].toDouble()
    }

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
                    icon = ContextCompat.getDrawable(context, R.drawable.ic_car)
                    rotation = animatedBearing.value
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
