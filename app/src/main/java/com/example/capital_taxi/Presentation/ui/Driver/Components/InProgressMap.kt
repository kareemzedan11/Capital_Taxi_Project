package com.example.capital_taxi.Presentation.ui.Driver.Components
// Android Imports
import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.location.Location
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
import calculateBearing
import com.example.capital_taxi.R
import com.example.myapplication.interpolateLocation
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


// Your custom imports
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

    // إنشاء mapView وحفظه مع التنظيف عند إلغاء التركيب
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
            mapView.onDetach() // تنظيف MapView
        }
    }

    // وظيفة لحساب المسافة (ممكن تنقلها خارج)
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
        factory = { mapView },
        update = { map ->
            map.overlays.clear()

            // سيارة السائق
            animatedPosition.value?.let { pos ->
                val originalDrawable = ContextCompat.getDrawable(context, R.drawable.ic_car)
                val bitmap = (originalDrawable as BitmapDrawable).bitmap
                val scaledBitmap = Bitmap.createScaledBitmap(bitmap, 60, 60, true)
                val scaledDrawable = BitmapDrawable(context.resources, scaledBitmap)

                val driverMarker = Marker(map).apply {
                    position = pos
                    icon = scaledDrawable
                    rotation = -animatedBearing.value // التدوير معكوس في OSMDroid
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
