package com.example.capital_taxi.Presentation.ui.Passengar.Components

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.storage.TaskState

data class TripUiState(
    val tripStatus: String = "",
    val isTripBegin: Boolean = false,
    val isInitialPickup: Boolean = true, // ✅ البداية الإفتراضية

    val isConfirmed: Boolean = false,
    val isSearch: Boolean = false,
    val isAccepted: Boolean = false,
    val inProgress: Boolean = false,

    val isStart: Boolean = false,
    val isEnd: Boolean = false,
    val isCancelled: Boolean = false, // 🆕 أضفنا حالة الإلغاء


 )
class StateTripViewModel : ViewModel() {

    private val _uiState = mutableStateOf(TripUiState())
    val uiState: State<TripUiState> = _uiState

    fun updateTripStatus(status: String) {
        _uiState.value = _uiState.value.copy(tripStatus = status)
        when (status) {
            "accepted" -> setAccepted()
            "Started" -> setInProgress()
            "InProgress" -> beginTrip()
            "Completed" -> TripEnd()
            "Cancelled" -> setCancelled() // 🆕 استخدم دالة الإلغاء الجديدة
            else -> resetAll()
        }
    }

    fun setAccepted() {
        _uiState.value = TripUiState(
            tripStatus = "accepted",
            isAccepted = true
        )
    }

    fun confirmPickup() {
        _uiState.value = TripUiState(
            tripStatus = _uiState.value.tripStatus,
            isConfirmed = true
        )
    }

    fun searchDriver() {
        _uiState.value = TripUiState(
            tripStatus = _uiState.value.tripStatus,
            isSearch = true
        )
    }

    fun beginTrip() {
        _uiState.value = TripUiState(
            tripStatus = "InProgress",
            isTripBegin = true
        )
    }

    fun setInProgress() {
        _uiState.value = TripUiState(
            tripStatus = "Started",
            inProgress = true
        )
    }

    fun TripEnd() {
        _uiState.value = _uiState.value.copy(
            tripStatus = "Completed",
            isEnd = true,
            isTripBegin = false,
            inProgress = false,
            isInitialPickup = false,
            isConfirmed = false
        )
    }

    fun setStart(value: Boolean) {
        _uiState.value = _uiState.value.copy(
            isStart = value
        )
    }

    // 🆕 دالة خاصة لما الرحلة تتلغي
    fun setCancelled() {
        _uiState.value = TripUiState(
            tripStatus = "Cancelled",
            isCancelled = true
        )
    }

    fun resetAll() {
        _uiState.value = TripUiState()
    }
}
