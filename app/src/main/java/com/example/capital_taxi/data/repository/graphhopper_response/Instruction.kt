package com.example.capital_taxi.data.repository.graphhopper_response

data class Instruction(
    val distance: Double,
    val exit_number: Int,
    val exited: Boolean,
    val interval: List<Int>,
    val last_heading: Double,
    val sign: Int,
    val street_destination: String,
    val street_name: String,
    val text: String,
    val time: Int,
    val turn_angle: Double
)