package com.example.capital_taxi.Presentation.ui.Driver.Screens.Home.Components.Home_Components

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import org.osmdroid.util.GeoPoint

class TripViewModel4 : ViewModel() {
    var tripLocation = mutableStateOf<GeoPoint?>(null)
        private set

    fun updateTripLocation(location: GeoPoint) {
        tripLocation.value = location
    }
}
