package com.example.capital_taxi.Presentation.ui.Driver.Screens.Home.Components.car_anamite

import android.animation.TypeEvaluator
import android.animation.ValueAnimator
import android.view.animation.LinearInterpolator
import androidx.lifecycle.ViewModel

import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import kotlin.math.*
import org.osmdroid.views.overlay.Marker
class MarkerAnimator {
    private var currentAnimation: ValueAnimator? = null
    private var lastBearing: Float = 0f

    fun animateMarker(
        marker: Marker,
        newPosition: GeoPoint,
        newBearing: Float,
        mapView: MapView,
        duration: Long = 1000
    ) {
        currentAnimation?.cancel()

        val startPosition = marker.position
        val startBearing = marker.rotation

        currentAnimation = ValueAnimator.ofFloat(0f, 1f).apply {
            addUpdateListener { animation ->
                val fraction = animation.animatedValue as Float

                // حركة الموضع
                val lat = startPosition.latitude + fraction * (newPosition.latitude - startPosition.latitude)
                val lon = startPosition.longitude + fraction * (newPosition.longitude - startPosition.longitude)
                marker.position = GeoPoint(lat, lon)

                // تدوير السيارة (بسلاسة)
                val bearing = if (abs(newBearing - startBearing) > 180) {
                    if (newBearing > startBearing) startBearing + (newBearing - 360 - startBearing) * fraction
                    else startBearing + (newBearing + 360 - startBearing) * fraction
                } else {
                    startBearing + (newBearing - startBearing) * fraction
                }

                marker.rotation = bearing % 360
                mapView.invalidate()
            }
            this.duration = duration
            interpolator = LinearInterpolator()
            start()
        }
    }
}class RideViewModel : ViewModel() {
    private var lastPosition: GeoPoint? = null

    fun updateDriverBearing(newPosition: GeoPoint): Double {
        return lastPosition?.let {
            calculateBearing(it, newPosition)
        } ?: 0.0.also {
            lastPosition = newPosition
        }
    }
}