package com.example.capital_taxi.data.repository.graphhopper_response

data class graphhopper_response(
    val hints: Hints,
    val info: Info,
    val paths: List<Path>
)