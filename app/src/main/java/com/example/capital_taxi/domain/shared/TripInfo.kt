package com.example.capital_taxi.domain.shared

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

data class TripInfo(
    val points: String?,
    val pointsEncoded: Boolean,
    val pointsEncodedMultiplier: Double,
    val time: Int,
    val distance: Double
)
class TripInfoViewModel : ViewModel() {
    // MutableLiveData to hold the trip information
    private val _tripInfo = MutableLiveData<TripInfo>()
    val tripInfo: LiveData<TripInfo> get() = _tripInfo

    // Function to update the trip info
    fun updateTripInfo(tripInfo: TripInfo) {
        _tripInfo.value = tripInfo
    }
}
