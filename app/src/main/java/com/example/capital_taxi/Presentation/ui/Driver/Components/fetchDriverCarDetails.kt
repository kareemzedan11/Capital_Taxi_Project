package com.example.capital_taxi.Presentation.ui.Driver.Components

import android.os.Handler
import android.os.Looper
import android.util.Log
import com.example.capital_taxi.domain.shared.db
import com.google.firebase.firestore.FirebaseFirestore
fun fetchDriverCarDetails(
    tripId: String,
    onResult: (
        carType: String,
        carNumber: String,
        username: String,
        rating: Double?,
        carColor: String,
        trips: Int
    ) -> Unit,
    maxRetries: Int = 5,
    delayMillis: Long = 1000L,
    attempt: Int = 1
)
 {
    Log.d("FirestoreDebug", "🔍 [Attempt $attempt] Fetching trip with _id = $tripId")

    db.collection("trips")
        .whereEqualTo("_id", tripId)
        .get()
        .addOnSuccessListener { tripSnapshot ->
            Log.d("FirestoreDebug", "✅ Trip query success. Documents found: ${tripSnapshot.size()}")

            if (!tripSnapshot.isEmpty) {
                val tripDoc = tripSnapshot.documents[0]
                val driverId = tripDoc.get("driver")
                Log.d("FirestoreDebug", "👤 Driver ID found in trip: $driverId")
                Log.d("FirestoreDebug", "🔎 Trip Data: ${tripDoc.data}")

                if (driverId != null) {
                    db.collection("drivers")
                        .whereEqualTo("id", driverId)
                        .get()
                        .addOnSuccessListener { snapshot ->
                            if (!snapshot.isEmpty) {
                                val driverDoc = snapshot.documents[0]

                                val carType = driverDoc.getString("carType") ?: "N/A"
                                val carNumber = driverDoc.getString("carNumber") ?: "N/A"
                                val username = driverDoc.getString("username") ?: "Unknown"
                                val carColor = driverDoc.getString("carColor") ?: "Unknown"
                                val trips = (driverDoc.get("trips") as? Number)?.toInt() ?: 0

                                val ratingMap = driverDoc.get("rating") as? Map<*, *>
                                val count = (ratingMap?.get("count") as? Number)?.toInt() ?: 0
                                val total = (ratingMap?.get("total") as? Number)?.toInt() ?: 0
                                val averageRating = if (count > 0) total.toDouble() / count else null

                                Log.d("Driver", "🚗 Driver data -> carType: $carType, carNumber: $carNumber, username: $username, color: $carColor, trips: $trips, rating: $averageRating")
                                onResult(carType, carNumber, username, averageRating, carColor, trips)
                            } else {
                                Log.e("Driver", "❌ No driver found with id = $driverId")
                                onResult("N/A", "N/A", "Unknown", null, "Unknown", 0)
                            }
                        }
                        .addOnFailureListener {
                            Log.e("Driver", "❌ Failed to fetch driver data: ${it.message}")
                            onResult("N/A", "N/A", "Unknown", null, "Unknown", 0)
                        }
                } else if (attempt < maxRetries) {
                    Log.w("FirestoreDebug", "⏳ Driver ID is null. Retrying after delay... [$attempt/$maxRetries]")
                    Handler(Looper.getMainLooper()).postDelayed({
                        fetchDriverCarDetails(tripId, onResult, maxRetries, delayMillis, attempt + 1)
                    }, delayMillis)
                } else {
                    Log.e("FirestoreDebug", "❌ Driver ID is still null after $maxRetries attempts.")
                    onResult("N/A", "N/A", "Unknown", null, "Unknown", 0)
                }
            } else {
                Log.e("FirestoreDebug", "❌ No trip found with _id = $tripId")
                onResult("N/A", "N/A", "Unknown", null, "Unknown", 0)
            }
        }
        .addOnFailureListener {
            Log.e("FirestoreDebug", "❌ Failed to fetch trip: ${it.message}")
            onResult("N/A", "N/A", "Unknown", null, "Unknown", 0)
        }
}
