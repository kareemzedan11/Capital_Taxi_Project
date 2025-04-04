package com.example.capital_taxi.Presentation.ui.Driver.Components

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class MapStateViewModel : ViewModel() {
    private val _shouldShowTracking = mutableStateOf(false)
    val shouldShowTracking: State<Boolean> = _shouldShowTracking

    fun enableTracking() { _shouldShowTracking.value = true }
    fun disableTracking() { _shouldShowTracking.value = false }
}