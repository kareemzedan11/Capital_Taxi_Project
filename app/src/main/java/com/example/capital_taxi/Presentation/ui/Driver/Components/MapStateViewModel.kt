package com.example.capital_taxi.Presentation.ui.Driver.Components

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class MapStateViewModel : ViewModel() {
    private val _shouldShowTracking = mutableStateOf(false)
    val shouldShowTracking: State<Boolean> = _shouldShowTracking

    private val _isTripInProgress = mutableStateOf(false)
    val isTripInProgress: State<Boolean> = _isTripInProgress
    private val _isSearching = mutableStateOf(false)
    val isSearching: State<Boolean> = _isSearching

    fun enableTracking() {
        _shouldShowTracking.value = true
    }

    fun disableTracking() {
        _shouldShowTracking.value = false
    }

    fun startTrip() {
        _isTripInProgress.value = true
    }

    fun startSearch() {
        _isSearching.value = true
    }

    fun stopSearch() {
        _isSearching.value = false
    }

    fun endTrip() {
        _isTripInProgress.value = false
    }
}
