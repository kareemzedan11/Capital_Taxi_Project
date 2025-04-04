import android.graphics.Color
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.compose.ui.viewinterop.AndroidView
import com.example.capital_taxi.R
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline

@Composable
fun AcceptanceMap(
    driverLocation: GeoPoint? = null, // موقع السائق
    passengerLocation: GeoPoint? = null, // موقع الراكب
    directions: List<GeoPoint> = emptyList(), // قائمة الاتجاهات
    modifier: Modifier = Modifier
) {
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

            // ✅ إضافة موقع السائق (أيقونة سيارة)
            driverLocation?.let {
                val driverMarker = Marker(mapView).apply {
                    position = it
                    icon = ContextCompat.getDrawable(mapView.context, R.drawable.ic_car) // أيقونة سيارة
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                }
                mapView.overlays.add(driverMarker)
            }

            // ✅ إضافة موقع الراكب (Marker عادي)
            passengerLocation?.let {
                val passengerMarker = Marker(mapView).apply {
                    position = it
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                }
                mapView.overlays.add(passengerMarker)
            }

            // ✅ إضافة الاتجاهات (Polyline)
            if (directions.isNotEmpty()) {
                val polyline = Polyline(mapView).apply {
                    setPoints(directions)
                    outlinePaint.color = Color.BLUE
                    outlinePaint.strokeWidth = 8f
                }
                mapView.overlays.add(polyline)
            }

            // تحديث موقع الكاميرا إذا كان هناك موقع متاح
            val lastLocation = driverLocation ?: passengerLocation
            lastLocation?.let {
                mapView.controller.setCenter(it)
            }

            mapView.invalidate()
        },
        modifier = modifier.fillMaxSize()
    )
}