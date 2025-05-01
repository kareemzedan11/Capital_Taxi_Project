package com.example.capital_taxi.domain

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.capital_taxi.Presentation.ui.Passengar.Screens.Home.UserHome.Components.Trip_preparation.VehicleOption
import com.example.capital_taxi.R
import com.example.capital_taxi.data.source.remote.DirectionsResponse
import com.example.capital_taxi.domain.shared.LocationData
import com.example.capital_taxi.utils.Constants.ApiConstants
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.type.LatLng
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Header
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Path
import retrofit2.http.Query
import java.io.IOException
import java.util.concurrent.TimeUnit




data class FareRequest(
    val origin: Location,
    val destination: Location,
    val paymentMethod: String
)
interface TripApiService {

    @POST("trips/calculate-fare")
    suspend fun calculateFare(
        @Header("Authorization") authorization: String,
        @Query("origin") origin: String,
        @Query("destination") destination: String,
        @Query("paymentMethod") paymentMethod: String
    ): Response<FareResponse>

    @POST("trips/create")
    suspend fun createTrip(
        @Header("Authorization") token: String,
        @Body tripRequest: TripRequest
    ): Response<TripResponse>

    @GET("trips/get-directions")
    suspend fun getTripDirections(
        @Header("Authorization") token: String,
        @Query("origin") origin: String,  // استخدام String هنا
        @Query("destination") destination: String  // استخدام String هنا
    ): Response<DirectionsResponse>

    @PUT("trips/assign-driver")
    suspend fun assignDriver(
        @Header("Authorization") token: String,
        @Body request: Map<String, String>
    ): Response<GenericResponse>

    @PUT("trips/update-status")
    suspend fun updateTripStatus(
        @Header("Authorization") token: String,
        @Body request: Map<String, String>
    ): Response<GenericResponse>

    @GET("users/order-history")
    suspend fun getUserTrips(
        @Header("Authorization") token: String
    ): Response<List<TripResponse>>

    @GET("trips/driver-trips")
    suspend fun getDriverTrips(
        @Header("Authorization") token: String
    ): Response<List<TripResponse>>

    @PUT("drivers/update-location")
    suspend fun updateDriverLocation(
        @Header("Authorization") token: String,
        @Body request: UpdateLocationRequest // ✅ استخدام كائن البيانات
    ): Response<UpdateLocationResponse>

        @GET("drivers/:driverId/location")
        suspend fun getDriverLocation(
            @Header("Authorization") token: String, // ✅ إرسال التوكن يدويًا
            @Path("driverId") driverId: String
        ): Response<DriverLocation>

    @GET("users/profile")
    suspend fun getUserProfile(
        @Header("Authorization") token: String, // ✅ إرسال التوكن يدويًا

    ): Response<DriverProfileResponse>
}
const val url = ApiConstants.base_URL
data class DriverProfileResponse(
    @SerializedName("_id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("username") val username: String,
    @SerializedName("phone") val phone: String,
    @SerializedName("email") val email: String,
    @SerializedName("paymentMethods") val paymentMethods: List<String>,
    @SerializedName("orderHistory") val orderHistory: List<String>,
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("updatedAt") val updatedAt: String
)

object RetrofitClient {
    private const val BASE_URL = url

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    val apiService: TripApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TripApiService::class.java)
    }
}



data class Route(
    val distance: Double,
    val duration: String
)
data class TripResponse(
    val message: String,
    val trip: Trip
)


data class LocationData(
    val lat: Double,
    val lng: Double
)

data class TripRequest(
    val _id: String,
    val user: String,
    val origin: String,  // سيتم تخزينها كنص مفصول بفاصلة "lat,lng"
    val destination: String,  // سيتم تخزينها كنص مفصول بفاصلة "lat,lng"
    val paymentMethod: String,
    val fare: Double,
    val distanceInKm: Double
)
data class Trip(
    val _id: String,
    val user: String,
    val driver: Driver,
    val originMap: Map<String, Any>? = null,
    val destinationMap: Map<String, Any>? = null,

    val origin: String,  // JSON String
    val destination: String,  // JSON String
    val distanceInKm: Double,
    val fare: Double,
    val paymentMethod: String,
    val status: String,
    val createdAt: String,
    val updatedAt: String,
    val cancelledByDrivers: List<String>? = null
) {
    // Empty constructor for Firebase
    constructor() : this(
        _id = "",
        user = "",
        driver = Driver(),
        originMap = null,
        destinationMap = null,
        origin = "",
        destination = "",
        distanceInKm = 0.0,
        fare = 0.0,
        paymentMethod = "",
        status = "",
        createdAt = "",
        updatedAt = "",

    )
}


// موديل بيانات السائق
data class Driver(
    val _id: String,
    val name: String,
    val phone: String
) {
    // ✅ Constructor فارغ مطلوب من Firebase
    constructor() : this("", "", "")
}


data class Location(
    val lat: Double,
    val lng: Double
)

class DirectionsViewModel : ViewModel() {
    private val _distance = MutableStateFlow<Double?>(null)
    val distance: StateFlow<Double?> = _distance

    private val _duration = MutableStateFlow<Double?>(null)
    val duration: StateFlow<Double?> = _duration

    private val _points = MutableStateFlow<String?>(null)
    val points: StateFlow<String?> = _points

    fun updateDirectionsData(distance: Double?, duration: Double?, points: String?) {
        Log.d("ViewModel", "Updating directions data - Distance: $distance, Duration: $duration, Points: $points")
        _distance.value = distance
        _duration.value = duration
        _points.value = points
    }

}


class mapDirectionsViewModel : ViewModel() {
    private val _duration = MutableStateFlow<Int?>(null)
    val duration: StateFlow<Int?> = _duration

    private val _distance = MutableStateFlow<Double?>(null)
    val distance: StateFlow<Double?> = _distance

    fun updateDirections(newDuration: Int, newDistance: Double) {
        _duration.value = newDuration
        _distance.value = newDistance
    }
}

data class UpdateLocationRequest(
    val driverId: String,
    val lat: Double,
    val lng: Double
)


data class GenericResponse(
    val message: String
)

@Serializable
data class FareResponse(
    val fare: Double,
    val paymentMethod: String,
    val route: Route
)

fun parseFareResponse(jsonResponse: String): FareResponse {
    return Json { ignoreUnknownKeys = true }.decodeFromString(jsonResponse)
}
sealed class ApiResponse<out T> {
    data class Success<T>(val data: T) : ApiResponse<T>()
    data class Error(val message: String) : ApiResponse<Nothing>()
    object Loading : ApiResponse<Nothing>()
}

fun calculateFare(
    origin: Location,
    destination: Location,
    paymentMethod: String,
    token: String,
    coroutineScope: CoroutineScope,
    fareViewModel: FareViewModel,  // ✅ تمرير ViewModel
    onSuccess: (List<VehicleOption>) -> Unit,
    onError: (String) -> Unit
) {

    coroutineScope.launch {
        try {
            val originStr = "${origin.lat},${origin.lng}"
            val destinationStr = "${destination.lat},${destination.lng}"


            val response = RetrofitClient.apiService.calculateFare(
                authorization = "Bearer $token",
                origin = originStr,
                destination = destinationStr,
                paymentMethod = paymentMethod
            )

            if (response.isSuccessful) {
                response.body()?.let { fareResponse ->
                    val fareValue = fareResponse.fare
                    fareViewModel.setFare(fareValue) // ✅ تحديث قيمة fare في ViewModel

                    val updatedVehicleOptions = listOf(
                        VehicleOption("Standard", fareValue, R.drawable.uber),
                        VehicleOption("Comfort", fareValue * 1.2, R.drawable.uber),
                        VehicleOption("Luxury", fareValue * 2, R.drawable.uber)
                    )
                    onSuccess(updatedVehicleOptions)
                } ?: onError("Empty response body")
            } else {
                val errorBody = response.errorBody()?.string() ?: response.message()
                Log.e("API_ERROR", "Failed to calculate fare: $errorBody")
                onError("Failed to calculate fare: $errorBody")
            }
        } catch (e: IOException) {
            onError("Network error: ${e.localizedMessage}")
        } catch (e: Exception) {
            onError("Error: ${e.localizedMessage ?: "Unknown error"}")
        }
    }
}
class DriverViewModelFactory(private val apiService: TripApiService) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DriverViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DriverViewModel(apiService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class DriverViewModel(private val apiService: TripApiService) : ViewModel() {
    private val _driverProfile = MutableLiveData<DriverProfileResponse?>()
    val driverProfile: LiveData<DriverProfileResponse?> = _driverProfile

    fun fetchUserProfile(token: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.getUserProfile("Bearer $token")
                if (response.isSuccessful) {
                    _driverProfile.postValue(response.body())
                    Log.d("API", "✅ User Profile: ${response.body()}")
                } else {
                    Log.e("API", "❌ Error: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("API", "❌ Exception: ${e.localizedMessage}")
            }
        }
    }
    private val _updateLocationState = MutableStateFlow<ApiResponse<UpdateLocationResponse>>(ApiResponse.Loading)
    val updateLocationState: StateFlow<ApiResponse<UpdateLocationResponse>> = _updateLocationState

    private val _driverLocationState = MutableStateFlow<ApiResponse<DriverLocation>>(ApiResponse.Loading)
    val driverLocationState: StateFlow<ApiResponse<DriverLocation>> = _driverLocationState

    // ✅ تحديث موقع السائق
    fun updateDriverLocation(token: String, request: UpdateLocationRequest) {
        viewModelScope.launch {
            try {
                val response = apiService.updateDriverLocation("Bearer $token", request)
                if (response.isSuccessful) {
                    _updateLocationState.value = ApiResponse.Success(response.body()!!)
                } else {
                    _updateLocationState.value = ApiResponse.Error(response.errorBody()?.string() ?: "❌ خطأ غير معروف")
                }
            } catch (e: Exception) {
                _updateLocationState.value = ApiResponse.Error("❌ فشل الاتصال بالخادم: ${e.message}")
            }
        }
    }


    // ✅ جلب موقع السائق
    fun getDriverLocation(token: String, driverId: String) {
        viewModelScope.launch {
            try {
                val response = apiService.getDriverLocation("Bearer $token", driverId)
                if (response.isSuccessful) {
                    _driverLocationState.value = ApiResponse.Success(response.body()!!)
                } else {
                    _driverLocationState.value = ApiResponse.Error(response.errorBody()?.string() ?: "❌ خطأ غير معروف")
                }
            } catch (e: Exception) {
                _driverLocationState.value = ApiResponse.Error("❌ فشل الاتصال بالخادم: ${e.message}")
            }
        }
    }
}
class FareViewModel : ViewModel() {
    private val _fare = MutableLiveData<Double>()
    val fare: LiveData<Double> get() = _fare

    fun setFare(value: Double) {
        _fare.value = value
    }
}


data class FareRequestWithToken(
    val origin: LocationData,
    val destination: LocationData,
    val paymentMethod: String,
    val token: String
)


fun assignDriver(
    tripId: String,
    driverId: String,
    token: String,
    coroutineScope: CoroutineScope,
    onSuccess: (String) -> Unit,
    onError: (String) -> Unit
) {
    coroutineScope.launch {
        try {
            val response = RetrofitClient.apiService.assignDriver(
                "Bearer $token",
                mapOf("tripId" to tripId, "driverId" to driverId)
            )
            if (response.isSuccessful && response.body() != null) {
                onSuccess(response.body()!!.message)
            } else {
                onError("Failed to assign driver: ${response.message()}")
            }
        } catch (e: Exception) {
            onError("Error: ${e.message}")
        }
    }
}

fun updateTripStatus(
    tripId: String,
    status: String,
    token: String,
    coroutineScope: CoroutineScope,
    onSuccess: (String) -> Unit,
    onError: (String) -> Unit
) {
    coroutineScope.launch {
        try {
            val response = RetrofitClient.apiService.updateTripStatus(
                "Bearer $token",
                mapOf("tripId" to tripId, "status" to status)
            )
            if (response.isSuccessful && response.body() != null) {
                onSuccess(response.body()!!.message)
            } else {
                onError("Failed to update trip status: ${response.message()}")
            }
        } catch (e: Exception) {
            onError("Error: ${e.message}")
        }
    }
}
// Declaring variables to store the values globally
var storedDistance: Double? = null
var storedDuration: Double? = null
var storedPoints: String? = null

suspend fun fetchTripDirections(
    token: String,
    origin: Location,
    destination: Location,
    directionsViewModel: DirectionsViewModel,
    onSuccess: (DirectionsResponse) -> Unit,
    onError: (String) -> Unit
) {
    try {
        // Convert location to lat,lng format
        val originString = "${origin.lat},${origin.lng}"
        val destinationString = "${destination.lat},${destination.lng}"

        // Send request to the API
        val response = RetrofitClient.apiService.getTripDirections(
            "Bearer $token",
            originString,
            destinationString
        )

        // Log the raw response for debugging
        Log.d("API Raw Response", "Raw Response: ${response.raw()}")

        // Log the response body for debugging
        Log.d("API Full Response", "Full Response: ${response.body()}")

        // Check if the response is successful and the body is not null
        if (response.isSuccessful && response.body() != null) {
            val directionsResponse = response.body()!!

            // Log the details of the response for debugging
            Log.d("API Response", "Distance: ${directionsResponse.distance}")
            Log.d("API Response", "Duration: ${directionsResponse.duration}")
            Log.d("API Response", "Points: ${directionsResponse.points}")
            directionsViewModel.updateDirectionsData(
                directionsResponse.distance,
                directionsResponse.duration,
                directionsResponse.points
            )
            // Storing values for later use
//            storedDistance = directionsResponse.distance
            storedDuration = directionsResponse.duration
            storedPoints = directionsResponse.points

            // Check if the points list is valid
            if (directionsResponse.points.isNotEmpty()) {
                onSuccess(directionsResponse)
            } else {
                Log.e("API Error", "Points list is null or empty in the response")
                onError("No valid points found in the response.")
            }
        } else {
            // Handle API errors
            Log.e("API Error", "Failed to get directions: ${response.code()} - ${response.message()}")
            onError("Failed to get directions: ${response.message()}")
        }
    } catch (e: Exception) {
        // Handle exceptions
        Log.e("API Error", "Error fetching directions: ${e.message}")
        onError("Error fetching directions: ${e.message}")
    }
}





fun getUserTrips(
    token: String,
    coroutineScope: CoroutineScope,
    onSuccess: (List<Trip>) -> Unit,
    onError: (String) -> Unit
) {
    coroutineScope.launch {
        try {
            val response = RetrofitClient.apiService.getUserTrips("Bearer $token")
            if (response.isSuccessful && response.body() != null) {
                // Extract the list of Trip objects from the TripResponse objects
                val tripList = response.body()!!.mapNotNull { it.trip } // Use mapNotNull to filter out null trips

                if (tripList.isEmpty()) {
                    onError("No trips available")
                } else {
                    // Pass the list of trips to onSuccess
                    onSuccess(tripList)
                }
            } else {
                onError("Failed to fetch trips: ${response.message()}")
            }
        } catch (e: Exception) {
            onError("Error: ${e.message}")
        }
    }
}
data class DriverLocation(
    val lat: Double,
    val lng: Double
)
data class UpdateLocationResponse(
    val message: String,
    val location: DriverLocation
)

data class ErrorResponse(
    val error: String
)
fun getDriverTrips(
    token: String,
    coroutineScope: CoroutineScope,
    onSuccess: (List<Trip>) -> Unit,
    onError: (String) -> Unit
) {
    coroutineScope.launch {
        try {
            val response = RetrofitClient.apiService.getDriverTrips("Bearer $token")
            if (response.isSuccessful && response.body() != null) {
                val trips = response.body()!!.map { it.trip }
                onSuccess(trips)
            } else {
                onError("Failed to fetch driver trips: ${response.message()}")
            }
        } catch (e: Exception) {
            onError("Error: ${e.message}")
        }
    }
}

fun parseLocation(json: String): Location? {
    return try {
        Gson().fromJson(json, Location::class.java)
    } catch (e: Exception) {
        Log.e("parseLocation", "Failed to parse location: ${e.message}")
        null
    }}