package com.example.capital_taxi.utils

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.location.Location
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.capital_taxi.R
import com.example.capital_taxi.domain.shared.decodePolyline
import com.example.capital_taxi.domain.storedPoints
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapListener
import org.osmdroid.events.ScrollEvent
import org.osmdroid.events.ZoomEvent
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
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
        Location.distanceBetween(loc1.latitude, loc1.longitude, loc2.latitude, loc2.longitude, results)
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
            if (distance > 5) {
                val rawBearing = calculateBearing(previousLocation, currentLocation).toFloat()
                launch {
                    val currentBearing = animatedBearing.value
                    val targetBearing = rawBearing

                    val shortestAngle = ((targetBearing - currentBearing + 540) % 360) - 180
                    val finalBearing = currentBearing + shortestAngle

                    animatedBearing.animateTo(
                        targetValue = finalBearing,
                        animationSpec = tween(durationMillis = 800, easing = LinearEasing)
                    )
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

                    val originalDrawable = ContextCompat.getDrawable(context, R.drawable.ic_car)
                    val bitmap = (originalDrawable as BitmapDrawable).bitmap

                    // 👇 تصغير الأيقونة حسب الزووم
                    val zoom = map.zoomLevelDouble
                    val scaleFactor = (zoom / 18.0).coerceIn(0.5, 1.0)
                    val scaledBitmap = Bitmap.createScaledBitmap(
                        bitmap,
                        (70 * scaleFactor).toInt(),
                        (70 * scaleFactor).toInt(),
                        true
                    )

                    icon = BitmapDrawable(context.resources, scaledBitmap)

                    rotation = -animatedBearing.value // الأيقونة بتبص للاتجاه الصحيح
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
data class DriverAnimationState(
    var previous: GeoPoint,
    var current: GeoPoint,
    val position: MutableState<GeoPoint?> = mutableStateOf(null),
    val bearing: Animatable<Float, AnimationVector1D> = Animatable(0f),
    val progress: Animatable<Float, AnimationVector1D> = Animatable(1f)
)



@Composable
fun SearchMapView(
    pickupLocation: GeoPoint,
    dropoffLocation: GeoPoint,
    driverLocations: SnapshotStateList<Pair<String, GeoPoint>>

) {
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        Configuration.getInstance().load(context, context.getSharedPreferences("osmdroid", 0))
    }

    val mapView = remember { MapView(context) }
    val driverStates = remember { mutableStateMapOf<String, DriverAnimationState>() }


    fun calculateBearing(start: GeoPoint, end: GeoPoint): Float {
        val lat1 = Math.toRadians(start.latitude)
        val lon1 = Math.toRadians(start.longitude)
        val lat2 = Math.toRadians(end.latitude)
        val lon2 = Math.toRadians(end.longitude)
        val dLon = lon2 - lon1
        val y = sin(dLon) * cos(lat2)
        val x = cos(lat1) * sin(lat2) - sin(lat1) * cos(lat2) * cos(dLon)
        return ((Math.toDegrees(atan2(y, x)) + 360) % 360).toFloat()
    }
    LaunchedEffect(driverLocations.toList()) {
        driverLocations.forEach { (id, newLocation) ->
            val state = driverStates.getOrPut(id) {
                DriverAnimationState(newLocation, newLocation).apply {
                    position.value = newLocation
                }
            }

            if (state.current != newLocation) {
                state.previous = state.current
                state.current = newLocation

                val newBearing = calculateBearing(state.previous, state.current)

                launch {
                    val shortestAngle = ((newBearing - state.bearing.value + 540) % 360) - 180
                    val finalBearing = state.bearing.value + shortestAngle
                    state.bearing.animateTo(finalBearing, tween(durationMillis = 800, easing = LinearEasing))
                }

                state.progress.snapTo(0f)

                launch {
                    state.progress.animateTo(1f, tween(durationMillis = 1800, easing = LinearEasing))
                }
            }
        }
    }


    LaunchedEffect(driverStates.keys.toList()) {
        while (true) {
            driverStates.forEach { (_, state) ->
                if (state.progress.value in 0f..1f) {
                    val lat = (state.current.latitude - state.previous.latitude) * state.progress.value + state.previous.latitude
                    val lon = (state.current.longitude - state.previous.longitude) * state.progress.value + state.previous.longitude
                    state.position.value = GeoPoint(lat, lon)
                } else {
                    state.position.value = state.current
                }
            }
            kotlinx.coroutines.delay(16) // 60fps
        }
    }

    LaunchedEffect(mapView) {
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setMultiTouchControls(false) // تعطيل اللمس المتعدد
        mapView.setBuiltInZoomControls(false) // تعطيل أزرار الزووم
        mapView.controller.setZoom(14)
        mapView.controller.setCenter(pickupLocation)
    }

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { mapView },
        update = { map ->
            map.overlays.clear()
            map.setOnTouchListener { _, _ -> true } // يمنع أي تفاعل مع الخريطة


            // 🚩 Pickup Marker
            val pickupMarker = Marker(map).apply {
                position = pickupLocation
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                val original = ContextCompat.getDrawable(context, R.drawable.ic_pickup) as BitmapDrawable
                val scaled = Bitmap.createScaledBitmap(original.bitmap, 40, 40, true)
                icon = BitmapDrawable(context.resources, scaled)
            }

            // 🏁 Dropoff Marker
            val dropoffMarker = Marker(map).apply {
                position = dropoffLocation
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                val original = ContextCompat.getDrawable(context, R.drawable.ic_dropoff) as BitmapDrawable
                val scaled = Bitmap.createScaledBitmap(original.bitmap, 40, 40, true)
                icon = BitmapDrawable(context.resources, scaled)
            }

            // 🛣️ Route
            var encodedPolyline = storedPoints
            // إضافة المسار بين النقاط إذا كان موجودًا
            encodedPolyline?.let { encoded ->
                val routePoints = decodePolyline(encoded)
                val polyline = Polyline(mapView)
                polyline.setPoints(routePoints)
                mapView.overlays.add(polyline)
            }
            driverStates.forEach { (id, state) ->
                val location = state.position.value ?: return@forEach

                val driverMarker = Marker(map).apply {
                    position = location
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)

                    val original = ContextCompat.getDrawable(context, R.drawable.ic_car) as BitmapDrawable
                    val scaled = Bitmap.createScaledBitmap(original.bitmap, 100, 100, true)
                    icon = BitmapDrawable(context.resources, scaled)

                    rotation = -state.bearing.value
                    infoWindow = null
                }
                map.overlays.add(driverMarker)
            }

            // Add fixed markers
            map.overlays.add(Marker(map).apply {
                position = pickupLocation
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            })

            map.overlays.add(Marker(map).apply {
                position = dropoffLocation
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                val icon = ContextCompat.getDrawable(context, R.drawable.ic_dropoff) as BitmapDrawable
                this.icon = BitmapDrawable(context.resources, Bitmap.createScaledBitmap(icon.bitmap, 40, 40, true))
            })

            map.invalidate()
        }
    )
}
