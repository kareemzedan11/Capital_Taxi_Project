package com.example.capital_taxi.data.repository.graphhopper_response

import com.google.gson.annotations.SerializedName

data class Hints(
    @SerializedName("visited_nodes.average") val visitedNodesAverage: Double,
    @SerializedName("visited_nodes.sum") val visitedNodesSum: Int
)