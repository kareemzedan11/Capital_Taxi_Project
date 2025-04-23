package com.example.capital_taxi.Helper


import retrofit2.http.GET
import retrofit2.http.Query

interface GraphHopperService {
    @GET("route")
    suspend fun getRoute(
        @Query("point") startPoint: String,
        @Query("point") endPoint: String,
        @Query("vehicle") vehicle: String = "car",
        @Query("key") apiKey: String = "c69abe50-60d2-43bc-82b1-81cbdcebeddc",
        @Query("points_encoded") pointsEncoded: Boolean = true,
        @Query("locale") locale: String = "en"
    ): GraphHopperResponse
}data class GraphHopperResponse(
    val paths: List<Path>
)

data class Path(
    val points: String,
    val distance: Double,
    val time: Long
)