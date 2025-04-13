package com.example.capital_taxi.Presentation.ui.Driver.Screens.Home.Components.Home_Components

import org.osmdroid.util.GeoPoint
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin



// The same bearing calculation converted to work with GeoPoint
private fun calculateBearing(start: GeoPoint, end: GeoPoint): Double {
    val lat1 = start.latitude * Math.PI / 180
    val lat2 = end.latitude * Math.PI / 180
    val dLon = (end.longitude - start.longitude) * Math.PI / 180

    val y = sin(dLon) * cos(lat2)
    val x = cos(lat1) * sin(lat2) - sin(lat1) * cos(lat2) * cos(dLon)

    return (atan2(y, x) * 180 / Math.PI + 360) % 360
}