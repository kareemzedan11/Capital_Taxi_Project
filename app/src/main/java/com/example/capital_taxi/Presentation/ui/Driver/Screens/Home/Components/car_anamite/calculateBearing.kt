package com.example.capital_taxi.Presentation.ui.Driver.Screens.Home.Components.car_anamite

import org.osmdroid.util.GeoPoint
import java.util.Timer
import kotlin.math.*

  fun calculateBearing(start: GeoPoint, end: GeoPoint): Double {
  val lat1 = Math.toRadians(start.latitude)
  val lat2 = Math.toRadians(end.latitude)
  val dLon = Math.toRadians(end.longitude - start.longitude)

  val y = sin(dLon) * cos(lat2)
  val x = cos(lat1) * sin(lat2) - sin(lat1) * cos(lat2) * cos(dLon)

  var bearing = Math.toDegrees(atan2(y, x))
  bearing = (bearing + 360) % 360

  return bearing
}