package com.example.capital_taxi.domain.shared

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class RideRequestManager {

    private val firestore = FirebaseFirestore.getInstance()

    fun findNearbyDrivers(pickupLat: Double, pickupLng: Double, onDriversFound: (List<String>) -> Unit) {
        firestore.collection("drivers")
            .whereEqualTo("status", "available")
            .get()
            .addOnSuccessListener { result ->
                val nearbyDrivers = mutableListOf<String>()

                Log.d("RideRequestManager", "📌 عدد السائقين المتاحين: ${result.size()}")

                for (document in result) {
                    val driverLat = document.getDouble("latitude") ?: continue
                    val driverLng = document.getDouble("longitude") ?: continue
                    val driverId = document.id

                    val distance = calculateDistance(pickupLat, pickupLng, driverLat, driverLng)

                    Log.d("RideRequestManager", "🚗 سائق $driverId على بُعد $distance كم")

                    if (distance <= 300.0) { // زيادة النطاق التجريبي إلى 40 كم
                        nearbyDrivers.add(driverId)
                    }

                }

                Log.d("RideRequestManager", "✅ تم العثور على ${nearbyDrivers.size} سائقين بالقرب من المستخدم.")
                onDriversFound(nearbyDrivers.take(5))
            }

    }

    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val radius = 6371 // نصف قطر الأرض بالكيلومترات
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)

        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2)

        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        return radius * c
    }
}



