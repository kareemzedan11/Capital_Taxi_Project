package com.example.myapplication


import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.capital_taxi.Presentation.ui.Driver.Screens.Home.Components.car_anamite.calculateBearing
import com.example.capital_taxi.R
import com.example.capital_taxi.domain.shared.decodePolyline
import com.example.capital_taxi.domain.storedPoints
import com.google.android.gms.location.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.osmdroid.events.MapListener
import org.osmdroid.events.ScrollEvent
import org.osmdroid.events.ZoomEvent
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline

  fun interpolateLocation(
    start: GeoPoint,
    end: GeoPoint,
    fraction: Float
): GeoPoint {
    return GeoPoint(
        start.latitude + (end.latitude - start.latitude) * fraction,
        start.longitude + (end.longitude - start.longitude) * fraction
    )
}
@Composable
fun MapViewComposable(
    startPoint: GeoPoint? = null,
    endPoint: GeoPoint? = null,

) {
    val context = LocalContext.current
    val mapView = remember { MapView(context) }

    // تعيين المصدر لأول مرة
    LaunchedEffect(mapView) {
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.controller.setZoom(15)
        mapView.controller.setCenter(startPoint ?: GeoPoint(30.033, 31.233))
    }

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { mapView },
        update = { mapView ->
            mapView.overlays.clear()

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
}

@Composable
fun DriverMapView(
    currentLocation: GeoPoint?,
    previousLocation: GeoPoint?,
    bearing: Float? = null
) {
    val context = LocalContext.current
    val mapView = remember { MapView(context) }

    val animatedBearing = remember { Animatable(0f) }
    val animatedPosition = remember { mutableStateOf<GeoPoint?>(null) }
    val animationProgress = remember { Animatable(0f) }
    var cameraMovedByUser by remember { mutableStateOf(false) }

    // دالة لحساب المسافة بين نقطتين (بالأمتار)
    fun calculateDistance(loc1: GeoPoint, loc2: GeoPoint): Double {
        val results = FloatArray(3)
        Location.distanceBetween(
            loc1.latitude, loc1.longitude,
            loc2.latitude, loc2.longitude,
            results
        )
        return results[0].toDouble() // المسافة بالأمتار
    }

    // حساب الاتجاه والتحريك
    LaunchedEffect(currentLocation, previousLocation) {
        if (currentLocation != null && previousLocation != null) {
            val distance = calculateDistance(previousLocation, currentLocation)

            // تحقق من المسافة وإذا كانت أكبر من 5 متر نبدأ التحديث

                val newBearing = calculateBearing(previousLocation, currentLocation).toFloat()

                animationProgress.snapTo(0f)
                animatedPosition.value = previousLocation

                launch {
                    animationProgress.animateTo(
                        targetValue = 1f,
                        animationSpec = tween(durationMillis = 2000, easing = LinearEasing)
                    )
                }

                launch {
                    animatedBearing.animateTo(
                        targetValue = newBearing,
                        animationSpec = tween(durationMillis = 2000, easing = LinearOutSlowInEasing)
                    )
                }
            }

    }

    LaunchedEffect(animationProgress.value) {
        if (currentLocation != null && previousLocation != null) {
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

        // استمع لأي تحريك يدوي للخريطة
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

        currentLocation?.let { mapView.controller.setCenter(it) }
    }

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { mapView },
        update = { map ->
            map.overlays.clear()

            animatedPosition.value?.let { location ->
                val driverMarker = Marker(map).apply {
                    position = location
                    icon = ContextCompat.getDrawable(context, R.drawable.ic_car)?.apply {
                        setTint(0xff0000)
                    }
                    rotation = animatedBearing.value
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                }
                map.overlays.add(driverMarker)

                // منع التحريك التلقائي لو المستخدم لمس الخريطة
                if (!cameraMovedByUser) {
                    map.controller.animateTo(location)
                }
            }
        }
    )
}

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
            val byte = encoded[index++].code - 63
            result = result or ((byte and 0x1f) shl shift)
            shift += 5
            if (byte < 0x20) break
        }
        val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
        lat += dlat

        shift = 0
        result = 0
        while (true) {
            val byte = encoded[index++].code - 63
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
        mapView.setTileSource(TileSourceFactory.MAPNIK)

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
                    userMarker.icon = context.getDrawable(R.drawable.uber) // أيقونة السائق
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
