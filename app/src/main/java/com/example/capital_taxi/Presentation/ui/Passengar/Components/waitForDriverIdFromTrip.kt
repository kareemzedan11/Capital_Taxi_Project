package com.example.capital_taxi.Presentation.ui.Passengar.Components

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

fun waitForDriverIdFromTrip(
    tripId: String,
    onDriverIdReady: (String) -> Unit,
    maxRetries: Int = 5,
    delayMillis: Long = 1000L
) {
    val db = FirebaseFirestore.getInstance()

    CoroutineScope(Dispatchers.IO).launch {
        var retries = 0
        var driverId: String? = null

        while (retries < maxRetries && driverId == null) {
            try {
                val querySnapshot = db.collection("trips")
                    .whereEqualTo("_id", tripId)
                    .get()
                    .await()

                val tripDoc = querySnapshot.documents.firstOrNull()

                driverId = tripDoc?.getString("driver")

                if (driverId == null) {
                    Log.w("WaitDriverIdTrip", "🔁 المحاولة ${retries + 1}: لسه مفيش driverId")
                    delay(delayMillis)
                    retries++
                }
            } catch (e: Exception) {
                Log.e("WaitDriverIdTrip", "❌ خطأ أثناء جلب الرحلة: ${e.message}")
                delay(delayMillis)
                retries++
            }
        }

        if (driverId != null) {
            Log.d("WaitDriverIdTrip", "✅ جبنا driverId من الرحلة: $driverId")
            withContext(Dispatchers.Main) {
                onDriverIdReady(driverId)
            }
        } else {
            Log.e("WaitDriverIdTrip", "❌ فشل في جلب driverId بعد $maxRetries محاولات")
        }
    }
}
fun fetchDriverInfoWithRetry(
    driverId: String,
    onSuccess: (name: String?, carType: String?) -> Unit,
    maxRetries: Int = 5,
    delayMillis: Long = 1000
) {
    CoroutineScope(Dispatchers.IO).launch {
        var retries = 0
        var success = false

        while (retries < maxRetries && !success) {
            try {
                val snapshot = FirebaseFirestore.getInstance()
                    .collection("drivers")
                    .whereEqualTo("id", driverId)
                    .limit(1)
                    .get()
                    .await()

                val driverDoc = snapshot.documents.firstOrNull()
                if (driverDoc != null) {
                    val name = driverDoc.getString("name")
                    val car = driverDoc.getString("carType")

                    withContext(Dispatchers.Main) {
                        onSuccess(name, car)
                    }

                    success = true
                } else {
                    Log.w("DriverInfo", "🔁 السائق مش لاقينه، نحاول تاني...")
                }
            } catch (e: Exception) {
                Log.e("DriverInfo", "❌ Error fetching driver: ${e.message}")
            }

            retries++
            delay(delayMillis)
        }

        if (!success) {
            Log.e("DriverInfo", "❌ فشل بعد $maxRetries محاولات")
        }
    }
}
