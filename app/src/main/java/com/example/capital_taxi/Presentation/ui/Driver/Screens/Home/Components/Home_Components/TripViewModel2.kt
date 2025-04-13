package com.example.capital_taxi.Presentation.ui.Driver.Screens.Home.Components.Home_Components

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TripViewModel2 : ViewModel() {
    private val _selectedTripId = MutableLiveData<String>()
    val selectedTripId: MutableLiveData<String?> = MutableLiveData(null)

    fun setSelectedTripId(tripId: String) {
        _selectedTripId.value = tripId
    }
}

