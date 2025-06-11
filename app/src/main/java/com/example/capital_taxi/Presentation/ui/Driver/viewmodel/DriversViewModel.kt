package com.example.capital_taxi.Presentation.ui.Driver.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import org.osmdroid.util.GeoPoint

class DriversViewModel : ViewModel() {

    val driverLocations = mutableStateListOf<Pair<String, GeoPoint>>()

    init {
        listenForDrivers()
    }

    private fun listenForDrivers() {
        val dbRef = FirebaseDatabase.getInstance().getReference("drivers")
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                driverLocations.clear()
                for (child in snapshot.children) {
                    val id = child.key ?: continue
                    val locationSnapshot = child.child("location")
                    val lat = locationSnapshot.child("latitude").getValue(Double::class.java)
                    val lng = locationSnapshot.child("longitude").getValue(Double::class.java)

                    if (lat != null && lng != null) {
                        println("📍 Driver updated: $id => $lat, $lng")
                        driverLocations.add(id to GeoPoint(lat, lng))
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                println("❌ Firebase error: ${error.message}")
            }
        })
    }
}