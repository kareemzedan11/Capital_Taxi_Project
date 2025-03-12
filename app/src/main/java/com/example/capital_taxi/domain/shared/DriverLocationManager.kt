package com.example.capital_taxi.domain.shared

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.util.Log
import com.google.android.gms.location.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class DriverLocationManager(private val context: Context, private val driverId: String) {

    private val firestore = FirebaseFirestore.getInstance()
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    fun startLocationUpdates() {
        val locationRequest = LocationRequest.create().apply {
            interval = 5000 // تحديث كل 5 ثواني
            fastestInterval = 2000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { location ->
                    updateDriverLocation(location)
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
    }

    private fun updateDriverLocation(location: Location) {
        val driverRef = firestore.collection("drivers").document(driverId)

        val locationData = mapOf(
            "latitude" to location.latitude,
            "longitude" to location.longitude,
            "status" to "available" // السائق متاح
        )

        driverRef.set(locationData, SetOptions.merge())
            .addOnSuccessListener { Log.d("Location", "Location updated successfully") }
            .addOnFailureListener { e -> Log.e("Location", "Failed to update location: ${e.message}") }
    }
}
