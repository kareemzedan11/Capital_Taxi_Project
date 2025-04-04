package com.example.capital_taxi.domain.driver.model

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class DriverStatusViewModel : ViewModel() {
    private val _isOnline = mutableStateOf(false)
    private val _isLoading = mutableStateOf(false)

    val isOnline: State<Boolean> = _isOnline
    val isLoading: State<Boolean> = _isLoading

    fun toggleStatus() {
        _isLoading.value = true
        // محاكاة عملية التحميل (استبدل هذا بالمنطق الفعلي)
        viewModelScope.launch {
            delay(2000) // انتظر لمدة 2 ثانية (محاكاة للعملية)
            _isOnline.value = !_isOnline.value
            _isLoading.value = false
        }
    }
}