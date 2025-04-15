package com.example.capital_taxi.domain.driver.model

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class acceptTripViewModel : ViewModel() {
    private val _isTripAccepted = mutableStateOf(false)
    private val _isTripStarted= mutableStateOf(false)
    private val _isTripCompleted= mutableStateOf(false)

    val isTripAccepted: State<Boolean> = _isTripAccepted
    val isTripStarted: State<Boolean> = _isTripStarted
    val isTripCompleted: State<Boolean> = _isTripCompleted

    fun acceptTrip() {
        _isTripAccepted.value = true
    }
    fun startTrip() {
        _isTripStarted.value = true
    }
    fun EndTrip() {
        _isTripCompleted.value = true
    }

    fun resetTrip() {
        _isTripAccepted.value = false
    }
}