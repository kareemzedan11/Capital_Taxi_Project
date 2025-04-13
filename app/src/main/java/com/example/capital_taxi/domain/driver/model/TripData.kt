package com.example.capital_taxi.domain.driver.model

import com.example.capital_taxi.data.repository.graphhopper_response.Instruction
import com.google.firebase.firestore.GeoPoint
import com.google.type.LatLng

data class TripData(
    val tripId: String,
    val startPoint: GeoPoint,  // نقطة البداية
    val endPoint: GeoPoint,    // نقطة النهاية
    val polylinePoints: List<LatLng>,  // نقاط المسار (مفكوكة من encoded polyline)
    val instructions: List<Instruction>  // التوجيهات
)