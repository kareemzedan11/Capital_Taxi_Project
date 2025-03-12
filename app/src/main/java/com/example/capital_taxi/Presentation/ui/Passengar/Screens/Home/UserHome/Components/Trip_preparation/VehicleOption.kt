package com.example.capital_taxi.Presentation.ui.Passengar.Screens.Home.UserHome.Components.Trip_preparation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.capital_taxi.R
import com.google.android.gms.maps.model.LatLng

data class VehicleOption(
    val name: String,
    val price: Double,
    val imageRes: Int
)

