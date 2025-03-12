package com.example.capital_taxi.data.source.remote
// Data classes لتخزين الاستجابة
data class DirectionsResponse(
    val distance: Double,
    val duration: Double,
    val points: String,
    val paths: List<Path>? = null
)

data class Path(
    val polyline: String,  // You can add more fields based on the response structure
    val distance: Double,
    val duration: Double
)
