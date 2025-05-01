package com.example.capital_taxi.utils

import com.example.capital_taxi.R

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

// دالة لحساب الاتجاه (bearing) بين نقطتين
fun calculateBearing(startPoint: GeoPoint, endPoint: GeoPoint): Double {
    val startLocation = Location("").apply {
        latitude = startPoint.latitude
        longitude = startPoint.longitude
    }
    val endLocation = Location("").apply {
        latitude = endPoint.latitude
        longitude = endPoint.longitude
    }
    // استخدام دالة bearingTo من Android SDK لحساب الاتجاه
    // ترجع القيمة بالدرجات من -180 إلى 180
    return startLocation.bearingTo(endLocation).toDouble()
}

// دالة لتقدير موقع وسيط بين نقطتين بناءً على نسبة التقدم (للتحريك السلس)
fun interpolateLocation(start: GeoPoint, end: GeoPoint, fraction: Float): GeoPoint {
    val lat = (end.latitude - start.latitude) * fraction + start.latitude
    val lon = (end.longitude - start.longitude) * fraction + start.longitude
    return GeoPoint(lat, lon)
}

@Composable
fun DriverMapViewContainer() {
    val context = LocalContext.current
    var previousLocation by remember { mutableStateOf<GeoPoint?>(null) }
    var currentLocation by remember { mutableStateOf<GeoPoint?>(null) }
    // يمكنك استخدام حالة أكثر تعقيدًا لإدارة الموقع إذا لزم الأمر
    // var smoothedLocation by remember { mutableStateOf<GeoPoint?>(null) }
    // var rawLocation by remember { mutableStateOf<GeoPoint?>(null) }

    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    // طلب تحديثات الموقع
    LaunchedEffect(Unit) {
        // تأكد من وجود إذن تحديد الموقع
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val locationRequest = LocationRequest.create().apply {
                interval = 2000 // تحديث كل ثانيتين
                fastestInterval = 1000 // أسرع تحديث كل ثانية
                priority = Priority.PRIORITY_HIGH_ACCURACY
            }

            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    locationResult.locations.lastOrNull()?.let { location ->
                        val newPoint = GeoPoint(location.latitude, location.longitude)
                        // تحديث الموقع السابق والحالي
                        previousLocation = currentLocation
                        currentLocation = newPoint

                        // يمكنك إضافة منطق التنعيم هنا إذا أردت
                        // val smoothed = if (smoothedLocation == null) newPoint else interpolateLocation(smoothedLocation!!, newPoint, 0.3f)
                        // previousLocation = smoothedLocation
                        // currentLocation = smoothed
                        // smoothedLocation = smoothed
                    }
                }
            }

            // بدء طلب تحديثات الموقع
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )

            // يمكنك إضافة منطق لإيقاف التحديثات عند الخروج من الشاشة
            // currentCoroutineContext().job.invokeOnCompletion { fusedLocationClient.removeLocationUpdates(locationCallback) }
        } else {
            // طلب الإذن إذا لم يكن ممنوحًا
            // يجب التعامل مع حالة عدم منح الإذن
            println("Location permission not granted")
        }
    }

    // عرض الخريطة مع تمرير المواقع
    DriverMapView(
        currentLocation = currentLocation,
        previousLocation = previousLocation
    )
}


@Composable
fun DriverMapView(
    currentLocation: GeoPoint?,
    previousLocation: GeoPoint?
) {
    val context = LocalContext.current
    // تهيئة osmdroid (تحتاج إلى القيام بها مرة واحدة في التطبيق)
    LaunchedEffect(Unit) {
        Configuration.getInstance().load(context, context.getSharedPreferences("osmdroid", 0))
    }

    val mapView = remember { MapView(context) }

    // حالة لتخزين الاتجاه المتحرك
    val animatedBearing = remember { Animatable(0f) }
    // حالة لتخزين الموقع المتحرك
    val animatedPosition = remember { mutableStateOf<GeoPoint?>(null) }
    // حالة لتتبع تقدم الأنيميشن للموقع
    val animationProgress = remember { Animatable(0f) }
    // حالة لتتبع إذا ما حرك المستخدم الخريطة يدويًا
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

    // تأثير لتشغيل الأنيميشن عند تغير الموقع
    LaunchedEffect(currentLocation, previousLocation) {
        if (currentLocation != null && previousLocation != null && currentLocation != previousLocation) {
            val distance = calculateDistance(previousLocation, currentLocation)

            // فقط قم بتحديث الاتجاه والتحريك إذا كانت المسافة معقولة (لتجنب الاهتزاز)
            if (distance > 1.0) { // يمكن تعديل هذه القيمة (مثلاً 1 متر)
                val newBearing = calculateBearing(previousLocation, currentLocation).toFloat()

                // إيقاف الأنيميشن الحالي وبدء أنيميشن جديد للموقع
                animationProgress.snapTo(0f)
                animatedPosition.value = previousLocation // ابدأ من الموقع السابق

                // تشغيل أنيميشن الموقع
                launch {
                    animationProgress.animateTo(
                        targetValue = 1f,
                        animationSpec = tween(durationMillis = 1900, easing = LinearEasing) // مدة أقل بقليل من الفاصل الزمني للتحديث
                    )
                    // بعد انتهاء الأنيميشن، ثبت الموقع على الموقع الحالي الفعلي
                    animatedPosition.value = currentLocation
                }

                // تشغيل أنيميشن الاتجاه
                launch {
                    // حساب فرق الزاوية الأقصر للدوران
                    val currentRotation = animatedBearing.value
                    var delta = newBearing - currentRotation
                    delta = (delta + 180) % 360 - 180 // تأكد من أن الدوران في أقصر اتجاه
                    val targetRotation = currentRotation + delta

                    animatedBearing.animateTo(
                        targetValue = targetRotation,
                        animationSpec = tween(durationMillis = 500, easing = LinearOutSlowInEasing) // مدة أقصر لتدوير أسرع
                    )
                }
                // إعادة تعيين حالة تحريك المستخدم عند وصول تحديث جديد
                cameraMovedByUser = false
            } else {
                // إذا كانت المسافة صغيرة جدًا، فقط حدث الموقع بدون أنيميشن كامل
                animatedPosition.value = currentLocation
            }
        } else if (currentLocation != null && animatedPosition.value == null) {
            // تعيين الموقع الأولي بدون أنيميشن
            animatedPosition.value = currentLocation
            mapView.controller.setCenter(currentLocation)
        }
    }

    // تأثير لتحديث الموقع المعروض أثناء الأنيميشن
    LaunchedEffect(animationProgress.value) {
        if (currentLocation != null && previousLocation != null && animationProgress.value > 0f && animationProgress.value < 1f) {
            animatedPosition.value = interpolateLocation(
                previousLocation,
                currentLocation,
                animationProgress.value
            )
        }
    }

    // إعداد الخريطة عند إنشائها لأول مرة
    LaunchedEffect(mapView) {
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.controller.setZoom(18.0)
        mapView.setMultiTouchControls(true)

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

        // تعيين الموقع الأولي إذا كان متاحًا
        currentLocation?.let {
            mapView.controller.setCenter(it)
        }
    }

    // عرض الخريطة وتحديث العلامة
    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { mapView },
        update = { map ->
            map.overlays.clear() // امسح العلامات القديمة

            animatedPosition.value?.let { location ->
                val driverMarker = Marker(map).apply {
                    position = location
                    // استخدم أيقونة السيارة التي أرفقتها أو أي أيقونة أخرى
                    // تأكد من أن اسم الملف صحيح وموجود في res/drawable
                    icon = ContextCompat.getDrawable(context, R.drawable.ic_car) // استبدل بـ R.drawable.images إذا كان هذا هو اسم الملف
                    // icon = ContextCompat.getDrawable(context, R.drawable.ic_navigation) // مثال أيقونة أخرى

                    // تطبيق الدوران المحسوب
                    rotation = animatedBearing.value
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER) // اجعل مركز الأيقونة هو نقطة الإرساء
                    infoWindow = null // تعطيل نافذة المعلومات عند النقر
                }
                map.overlays.add(driverMarker)

                // تحريك الكاميرا لمتابعة السيارة فقط إذا لم يقم المستخدم بتحريك الخريطة
                if (!cameraMovedByUser) {
                    map.controller.animateTo(location)
                }
            }
            map.invalidate() // إعادة رسم الخريطة
        }
    )
}

// تأكد من إضافة الأيقونة ic_car_top_view.png (أو بالاسم الذي تختاره) إلى مجلد res/drawable
// تأكد من إضافة الأذونات اللازمة في AndroidManifest.xml:
// <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
// <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
// <uses-permission android:name="android.permission.INTERNET" />
// <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />

// تأكد من إضافة الاعتماديات اللازمة في build.gradle (app):
// implementation "org.osmdroid:osmdroid-android:6.1.17"
// implementation "androidx.compose.ui:ui-viewbinding:1.x.x" // أو أحدث إصدار
// implementation "com.google.android.gms:play-services-location:21.x.x" // أو أحدث إصدار

// استبدل R.drawable.ic_car_top_view باسم ملف الأيقونة الصحيح لديك.
// قد تحتاج إلى تعديل قيم الأنيميشن (durationMillis, easing) للحصول على التأثير المطلوب.

