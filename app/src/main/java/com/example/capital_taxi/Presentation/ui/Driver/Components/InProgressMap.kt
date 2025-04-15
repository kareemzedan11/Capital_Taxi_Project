package com.example.capital_taxi.Presentation.ui.Driver.Components
// Android Imports
import android.graphics.Color
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.compose.ui.viewinterop.AndroidView
import com.example.capital_taxi.R
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.SphericalUtil
import findNearestIndex
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


@Composable
fun InProgressMap(
    driverLocation: GeoPoint? = null,
    Destination: GeoPoint? = null,
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

            // إضافة موقع السائق (أيقونة سيارة)
            driverLocation?.let { newLocation ->
                if (marker == null) {
                    marker = Marker(mapView).apply {
                        icon = ContextCompat.getDrawable(mapView.context, R.drawable.ic_car)
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

            // إضافة موقع الراكب
            Destination?.let {
                val passengerMarker = Marker(mapView).apply {
                    position = it
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    // هذا هو الماركر الافتراضي الذي توفره مكتبة OpenStreetMap (تأتي مع الأيقونات الأصلية).
                 }
                mapView.overlays.add(passengerMarker)
            }



            // إضافة الاتجاهات
            val remainingPath = if (driverLocation != null && directions.isNotEmpty()) {
                val nearestIndex = findNearestIndex(driverLocation, directions)
                directions.subList(nearestIndex, directions.size)
            } else {
                directions
            }

            if (remainingPath.isNotEmpty()) {
                val polyline = Polyline(mapView).apply {
                    setPoints(remainingPath)
                    outlinePaint.color = Color.GREEN // اللون الجديد للاتجاهات
                    outlinePaint.strokeWidth = 8f
                }
                mapView.overlays.add(polyline)
            }

            // تحديث مركز الخريطة
            val lastLocation = driverLocation ?: Destination
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
