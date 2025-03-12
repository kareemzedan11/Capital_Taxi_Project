package com.example.capital_taxi.domain.shared

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.capital_taxi.domain.Trip
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.ktx.firestore


import com.google.firebase.ktx.Firebase

@SuppressLint("StaticFieldLeak")
val db = Firebase.firestore

fun requestRide(passengerId: String, pickupLocation: String, destination: String, estimatedPrice: Double) {
    val rideData = hashMapOf(
        "passengerId" to passengerId,
        "pickupLocation" to pickupLocation,
        "destination" to destination,
        "estimatedPrice" to estimatedPrice,
        "status" to "pending", // في انتظار السائق
        "driverId" to null, // لم يتم تعيين سائق بعد

    )

    db.collection("rides")
        .add(rideData)
        .addOnSuccessListener { documentReference ->
            Log.d("Ride", "تم طلب الرحلة بنجاح: ${documentReference.id}")
        }
        .addOnFailureListener { e ->
            Log.e("Ride", "خطأ أثناء طلب الرحلة: ${e.message}")
        }
}
fun listenForRideRequests(driverId: String) {
    db.collection("rides")
        .whereEqualTo("status", "pending") // الرحلات في الانتظار
        .addSnapshotListener { snapshots, error ->
            if (error != null) {
                Log.e("Ride", "خطأ في الاستماع للطلبات: ${error.message}")
                return@addSnapshotListener
            }

            for (doc in snapshots!!.documents) {
                val rideId = doc.id
                val pickup = doc.getString("pickupLocation")
                val destination = doc.getString("destination")
                val price = doc.getDouble("estimatedPrice")

                Log.d("Ride", "طلب جديد: من $pickup إلى $destination - السعر: $$price")

                // هنا يمكن عرض الطلب في واجهة السائق
            }
        }
}
fun acceptRide(rideId: String, driverId: String, driverName: String, driverCar: String) {
    val updateData = hashMapOf(
        "status" to "accepted",
        "driverId" to driverId,
        "driverName" to driverName,
        "driverCar" to driverCar
    )

    db.collection("rides").document(rideId)
        .update(updateData as Map<String, Any>)
        .addOnSuccessListener {
            Log.d("Ride", "تم قبول الرحلة بنجاح")
        }
        .addOnFailureListener { e ->
            Log.e("Ride", "خطأ أثناء قبول الرحلة: ${e.message}")
        }
}
fun listenForRideUpdates(rideId: String) {
    db.collection("rides").document(rideId)
        .addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e("Ride", "خطأ في تحديثات الرحلة: ${error.message}")
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                val status = snapshot.getString("status")
                val driverName = snapshot.getString("driverName")
                val driverCar = snapshot.getString("driverCar")

                if (status == "accepted") {
                    Log.d("Ride", "تم تعيين سائق: $driverName - السيارة: $driverCar")
                    // تحديث الواجهة لعرض بيانات السائق للراكب
                }
            }
        }
}
fun listenForPendingTrips(driverId: String, onTripReceived: (Trip2) -> Unit) {
    val db = Firebase.firestore

    db.collection("trips")
        .whereEqualTo("status", "pending") // جلب الرحلات التي لم يتم قبولها بعد
        .addSnapshotListener { snapshots, error ->
            if (error != null) {
                Log.e("Driver", "❌ خطأ في جلب الرحلات: ${error.message}")
                return@addSnapshotListener
            }

            snapshots?.documentChanges?.forEach { change ->
                if (change.type == DocumentChange.Type.ADDED) {
                    val trip = change.document.toObject(Trip2::class.java).copy(id = change.document.id)
                    onTripReceived(trip) // إرسال البيانات لتحديث الواجهة
                }
            }
        }
}

@SuppressLint("RememberReturnType")
@Composable
fun DriverTripScreen() {
    val trips = remember { mutableStateListOf<Trip2>() }
    val driverId = "1" // استبدله بالـ ID الحقيقي للسائق

    LaunchedEffect(Unit) {
        listenForPendingTrips(driverId) { trip ->
            if (!trips.any { it.id == trip.id }) {
                trips.add(trip)
            }
        }
    }

    LazyColumn {
        items(trips) { trip ->
            TripCard(trip) { acceptTrip(trip.id, driverId) }
        }
    }
}

fun acceptTrip(tripId: String, driverId: String) {
    val db = Firebase.firestore
    db.collection("trips").document(tripId)
        .update(
            "status", "accepted",
            "driverId", driverId
        )
        .addOnSuccessListener {
            Log.d("Driver", "تم قبول الرحلة")
        }
        .addOnFailureListener { e ->
            Log.e("Driver", "خطأ أثناء قبول الرحلة: ${e.message}")
        }
}
@Composable
fun TripCard(trip: Trip2, onAccept: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(text = "📍 من: ${trip.pickupLocation}", fontSize = 16.sp)
            Text(text = "📍 إلى: ${trip.destination}", fontSize = 16.sp)
            Text(text = "💰 السعر: ${trip.estimatedPrice} EGP", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = { onAccept() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Green)
            ) {
                Text(text = "✅ قبول الرحلة", color = Color.White)
            }
        }
    }
}
data class Trip2(
    val id: String = "",
    val pickupLocation: String = "",
    val destination: String = "",
    val estimatedPrice: Double = 0.0,
    val status: String = "pending",
    val driverId: String? = null
)
