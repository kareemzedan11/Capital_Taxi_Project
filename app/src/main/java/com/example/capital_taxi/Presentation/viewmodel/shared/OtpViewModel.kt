package com.example.capital_taxi.Presentation.viewmodel.shared

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class OtpViewModel : ViewModel() {
    var timer by mutableStateOf(30)
    var otpValues = mutableStateListOf("", "", "", "")

    // Timer countdown logic
    fun startTimer() {
        viewModelScope.launch {
            while (timer > 0) {
                delay(1000L)
                timer--
            }
        }
    }

    // Function to handle OTP value changes
    fun onOtpValueChange(index: Int, newValue: String) {
        if (newValue.length <= 1) {
            otpValues[index] = newValue
        }
    }

    // Function to handle focusing behavior for OTP fields
    fun moveFocus(index: Int, next: Boolean) {
        if (next && index < otpValues.lastIndex) {
            // Move focus to the next field
            otpValues[index + 1] = otpValues[index]
        } else if (!next && index > 0) {
            // Move focus to the previous field
            otpValues[index - 1] = otpValues[index]
        }
    }
}
