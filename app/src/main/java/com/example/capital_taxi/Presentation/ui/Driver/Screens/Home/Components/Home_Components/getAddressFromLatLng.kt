package com.example.capital_taxi.Presentation.ui.Driver.Screens.Home.Components.Home_Components

import android.content.Context
import android.location.Geocoder
import java.util.Locale

fun getAddressFromLatLng(context: Context, latitude: Double, longitude: Double): String {
    val geocoder = Geocoder(context, Locale.getDefault())
    return try {
        val addresses = geocoder.getFromLocation(latitude, longitude, 1)
        if (!addresses.isNullOrEmpty()) {
            addresses[0].getAddressLine(0) // أو addresses[0].locality للحصول على المدينة فقط
        } else {
            "Location not found"
        }
    } catch (e: Exception) {
        "Error: ${e.message}"
    }
}