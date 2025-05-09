package com.example.capital_taxi.domain.driver.model

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
class DriverStatusViewModel : ViewModel() {
    private val _isOnline = mutableStateOf(false)
    val isOnline: State<Boolean> = _isOnline

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    fun setOnlineStatus(status: Boolean) {
        _isOnline.value = status
    }

    fun setLoading(loading: Boolean) {
        _isLoading.value = loading

    }
    fun setInitialStatus(status: Boolean) {
        _isOnline.value = status
    }

    fun toggleStatus() {
        _isLoading.value = true
        _isOnline.value = !_isOnline.value
        _isLoading.value = false
    }
}