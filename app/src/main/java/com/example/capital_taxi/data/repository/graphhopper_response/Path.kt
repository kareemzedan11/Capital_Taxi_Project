package com.example.capital_taxi.data.repository.graphhopper_response

data class Path(
    val ascend: Double,
    val bbox: List<Double>,
    val descend: Double,
    val details: Details,
    val distance: Double,
    val instructions: List<Instruction>,
    val legs: List<Any>,
    val points: String,
    val points_encoded: Boolean,
    val points_encoded_multiplier: Double,
    val snapped_waypoints: String,
    val time: Int,
    val transfers: Int,
    val weight: Double
)