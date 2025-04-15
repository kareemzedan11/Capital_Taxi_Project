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


 )
class StateTripViewModel : ViewModel() {

    private val _uiState = mutableStateOf(TripUiState())
    val uiState: State<TripUiState> = _uiState

    // تحديث حالة الرحلة العامة
    fun updateTripStatus(status: String) {
        _uiState.value = _uiState.value.copy(tripStatus = status)
        when (status) {
            "accepted" -> setAccepted()
            "Started" -> setInProgress()
            "InProgress" -> beginTrip()
            "Completed"-> TripEnd()
            "Cancelled" -> resetAll()
            else -> resetAll()
        }
    }

    // عند قبول الرحلة
    fun setAccepted() {
        _uiState.value = TripUiState(
            tripStatus = "accepted",
            isAccepted = true
        )
    }

    // تأكيد الالتقاط
    fun confirmPickup() {
        _uiState.value = TripUiState(
            tripStatus = _uiState.value.tripStatus,
            isConfirmed = true
        )
    }

    // بدء البحث عن سائق
    fun searchDriver() {
        _uiState.value = TripUiState(
            tripStatus = _uiState.value.tripStatus,
            isSearch = true
        )
    }

    // بدء الرحلة (عرض DriverArrivalCard)
    fun beginTrip() {
        _uiState.value = TripUiState(
            tripStatus = "InProgress",
            isTripBegin = true
        )
    }

    // عرض RideInProgressScreen لما الرحلة فعليًا تبدأ
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
            isInitialPickup=false,
            isConfirmed=false
        )
    }


    // تعيين isStart = true مؤقتًا (لو لسه الرحلة ما بدأتش)
    fun setStart(value: Boolean) {
        _uiState.value = _uiState.value.copy(
            isStart = value
        )
    }

    // إعادة تعيين كل الحالات
    fun resetAll() {
        _uiState.value = TripUiState()
    }
}
