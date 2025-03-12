package com.example.capital_taxi.domain.shared
import android.util.Log
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.Callback
import retrofit2.Response
import okhttp3.OkHttpClient
import org.osmdroid.util.GeoPoint
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


data class RouteResponse(
    val paths: List<Path>?
)

data class Path(
    val distance: Double,
    val time: Int,
    val points: String // Temporarily set it to String to inspect the data
)

data class Point(
    val lat: Double,
    val lon: Double
)



interface GraphHopperApi {
    @GET("route")
    fun getDirections(
        @Query("point") origin: String,
        @Query("point") destination: String,
        @Query("vehicle") vehicle: String = "car",
  //      @Query("locale") locale: String = "en",
     //   @Query("calc_points") calcPoints: Boolean = true,
        @Query("key") apiKey: String // Use the API key as a query parameter
    ): Call<RouteResponse>
}


object RetrofitClient2 {
    private const val BASE_URL = "https://graphhopper.com/api/1/"

    private val client = OkHttpClient.Builder().build()

    val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val api: GraphHopperApi = retrofit.create(GraphHopperApi::class.java)
}
fun getDirections(origin: String, destination: String, apiKey: String) {
    val call = RetrofitClient2.api.getDirections(origin, destination, apiKey = apiKey)

    call.enqueue(object : Callback<RouteResponse> {
        override fun onResponse(call: Call<RouteResponse>, response: Response<RouteResponse>) {
            if (response.isSuccessful) {
                val rawResponse = response.body()?.toString() // Log the raw response
                Log.d("GraphHopper", "Raw response: $rawResponse")

                val data = response.body()
                data?.paths?.firstOrNull()?.let {
                    val distanceInKm = it.distance / 1000.0
                    val durationInMinutes = it.time / 1000 / 60.0
                    val points = it.points

                    Log.d("GraphHopper", "Distance: $distanceInKm km")
                    Log.d("GraphHopper", "Duration: $durationInMinutes min")
                    Log.d("GraphHopper", "Points: $points")
                }
            } else {
                Log.e("GraphHopper", "Error: ${response.code()}")
            }
        }


        override fun onFailure(call: Call<RouteResponse>, t: Throwable) {
            Log.e("GraphHopper", "Error: ${t.message}")
        }
    })
}

fun fetchRoute(
    startPoint: String, // Format: "lat1,lng1"
    endPoint: String,   // Format: "lat2,lng2"
    apiKey: String,
    onSuccess: (List<GeoPoint>) -> Unit,
    onError: (String) -> Unit
) {
    val retrofit = Retrofit.Builder()
        .baseUrl("https://graphhopper.com/api/1/") // GraphHopper API base URL
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService = retrofit.create(GraphHopperApi::class.java)
    val call = apiService.getDirections(startPoint, endPoint, "car", apiKey)

    call.enqueue(object : retrofit2.Callback<RouteResponse> {
        override fun onResponse(call: Call<RouteResponse>, response: Response<RouteResponse>) {
            if (response.isSuccessful) {
                val rawResponse = response.body()?.toString() // Log the raw response
                Log.d("GraphHopper", "Raw response: $rawResponse")

                val data = response.body()
                data?.paths?.firstOrNull()?.let {
                    val distanceInKm = it.distance / 1000.0
                    val durationInMinutes = it.time / 1000 / 60.0
                    val points = it.points

                    Log.d("GraphHopper", "Distance: $distanceInKm km")
                    Log.d("GraphHopper", "Duration: $durationInMinutes min")
                    Log.d("GraphHopper", "Points: $points")
                }
            } else {
                Log.e("GraphHopper", "Error: ${response.code()}")
            }
        }

        override fun onFailure(call: Call<RouteResponse>, t: Throwable) {
            onError("Network error: ${t.message}")
        }
    })
}

fun decodePolyline(encoded: String): List<GeoPoint> {
    val poly = mutableListOf<GeoPoint>()
    var index = 0
    val len = encoded.length
    var lat = 0
    var lng = 0

    while (index < len) {
        var b: Int
        var shift = 0
        var result = 0
        do {
            b = encoded[index++].code - 63
            result = result or (b and 0x1f shl shift)
            shift += 5
        } while (b >= 0x20)
        val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
        lat += dlat

        shift = 0
        result = 0
        do {
            b = encoded[index++].code - 63
            result = result or (b and 0x1f shl shift)
            shift += 5
        } while (b >= 0x20)
        val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
        lng += dlng

        poly.add(GeoPoint(lat / 1E5, lng / 1E5))
    }
    return poly
}