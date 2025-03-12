package com.example.capital_taxi.domain.shared

import android.util.Log
import com.google.firebase.database.FirebaseDatabase

fun saveDriverLocationToRealtimeDatabase(tripId: String, location: android.location.Location) {
    // بيانات الموقع
    val driverLocationData = hashMapOf(
        "tripId" to tripId,
        "latitude" to location.latitude,
        "longitude" to location.longitude,
        "timestamp" to System.currentTimeMillis() // يمكنك استخدام timestamp من الخادم إذا لزم الأمر
    )

    // تحديد المسار في Realtime Database
    val database = FirebaseDatabase.getInstance()
    val ref = database.getReference("driverLocations").child(tripId)

    // محاولة كتابة البيانات
    ref.setValue(driverLocationData)
        .addOnSuccessListener {
            Log.d("Firebase", "Driver location saved to Realtime Database successfully")
        }
        .addOnFailureListener { e ->
            Log.e("Firebase", "Error saving driver location to Realtime Database", e)
        }
}
