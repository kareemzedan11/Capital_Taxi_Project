package com.example.capital_taxi.Presentation.ui.Passengar.Components

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
fun waitForDriverIdFromTrip(
    tripId: String,
    onDriverIdReady: (String) -> Unit,
    timeoutMillis: Long = 10000L
) {
    val db = FirebaseFirestore.getInstance()
    var registration: ListenerRegistration? = null

    registration = db.collection("trips")
        .whereEqualTo("_id", tripId)
        .addSnapshotListener { snapshot, error ->
            error?.let {
                Log.e("WaitDriverIdTrip", "❌ خطأ في الlistener: ${it.message}")
                return@addSnapshotListener
            }

            snapshot?.documents?.firstOrNull()?.getString("driver")?.let { driverId ->
                Log.d("WaitDriverIdTrip", "✅ جبنا driverId من الرحلة: $driverId")
                onDriverIdReady(driverId)
                registration?.remove()  // هنا بقت متاحة
            }
        }

    CoroutineScope(Dispatchers.IO).launch {
        delay(timeoutMillis)
        registration?.remove()
        Log.e("WaitDriverIdTrip", "❌ انتهى وقت الانتظار بدون الحصول على driverId")
    }
}

fun fetchDriverInfo(
    driverId: String,
    onSuccess: (name: String?, carType: String?) -> Unit,
    timeoutMillis: Long = 10000L
) {
    var registration: ListenerRegistration? = null

    registration = FirebaseFirestore.getInstance()
        .collection("drivers")
        .whereEqualTo("id", driverId)
        .limit(1)
        .addSnapshotListener { snapshot, error ->
            error?.let {
                Log.e("DriverInfo", "❌ خطأ في الlistener: ${it.message}")
                return@addSnapshotListener
            }

            snapshot?.documents?.firstOrNull()?.let { driverDoc ->
                val name = driverDoc.getString("name")
                val car = driverDoc.getString("carType")
                Log.d("DriverInfo", "✅ تم جلب بيانات السائق")
                onSuccess(name, car)
                registration?.remove()
            }
        }

    CoroutineScope(Dispatchers.IO).launch {
        delay(timeoutMillis)
        registration?.remove()
        Log.e("DriverInfo", "❌ انتهى وقت الانتظار بدون الحصول على بيانات السائق")
    }
}
