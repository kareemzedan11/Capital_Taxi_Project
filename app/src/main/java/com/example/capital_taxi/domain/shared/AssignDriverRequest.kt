package com.example.capital_taxi.domain.shared

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Looper
import android.util.Log
import androidx.compose.runtime.*
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModel
import com.example.capital_taxi.Presentation.ui.Driver.Screens.Home.getAddressFromLatLng
import com.example.capital_taxi.domain.Location
import com.example.capital_taxi.domain.RetrofitClient
import com.example.capital_taxi.domain.Trip
import com.example.capital_taxi.domain.TripRequest
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import java.util.UUID

data class LocationData(
    val lat: Double,
    val lng: Double
)



data class TripResponse(
    val message: String,
    val trip: TripDetails?
)

data class TripDetails(
    val user: String,
    val origin: LocationData,
    val destination: LocationData,
    val paymentMethod: String,
    val fare: Double,
    val distanceInKm: Double,
    val status: String
)

interface TripApiService {
    @POST("api/trips/create")
    suspend fun requestTrip(
        @Header("Authorization") token: String,
        @Body request: TripRequest
    ): TripResponse
}

object RetrofitClient {
    private val retrofit = Retrofit.Builder()
        .baseUrl("http://192.168.96.1:5000/") // Adjust the base URL
        .addConverterFactory(GsonConverterFactory.create())
        .client(OkHttpClient.Builder().build())
        .build()

    val apiService: TripApiService = retrofit.create(TripApiService::class.java)
}
class TripViewModel : ViewModel() {
    private val _availableTrips = mutableStateOf<List<Trip>>(emptyList())
    val availableTrips: State<List<Trip>> get() = _availableTrips
    fun getTripStatusById(
        tripId: String,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        db.collection("trips")
            .whereEqualTo("_id", tripId)  // ابحث عن المستند الذي يحتوي على الحقل 'id'
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    // الحصول على أول مستند في النتيجة
                    val document = querySnapshot.documents.first()
                    val status = document.getString("status") ?: "unknown"
                    onSuccess(status)
                } else {
                    onError("🚫 الرحلة غير موجودة!")
                }
            }
            .addOnFailureListener { e ->
                onError(e.message ?: "❌ خطأ غير معروف في جلب الحالة")
            }
    }
    fun getTripOriginById(
        tripId: String,
        onSuccess: (String) -> Unit, // ✅ يرجع العنوان كنص بدلاً من إحداثيات
        onError: (String) -> Unit
    ) {
        db.collection("trips")
            .whereEqualTo("_id", tripId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val document = querySnapshot.documents.first()
                    val origin = document.getString("origin")

                    if (!origin.isNullOrEmpty()) {
                        onSuccess(origin) // ✅ إرجاع العنوان كنص
                    } else {
                        onError("🚫 لم يتم العثور على عنوان المنشأ!")
                    }
                } else {
                    onError("🚫 الرحلة غير موجودة!")
                }
            }
            .addOnFailureListener { e ->
                onError(e.message ?: "❌ خطأ غير معروف في جلب الموقع")
            }
    }


    fun cancelTripForDriver(tripId: String) {
        _availableTrips.value = _availableTrips.value.filter { it._id != tripId }
    }
    fun acceptTrip(tripId: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        db.collection("trips")
            .whereEqualTo("_id", tripId) // ✅ البحث عن الرحلة التي تخص المستخدم
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    for (document in documents) {
                        document.reference.update("status", "accepted") // ✅ تحديث الحالة فقط
                            .addOnSuccessListener {
                                Log.d("TripViewModel", "✅ تم تحديث حالة الرحلة إلى 'accepted' بنجاح!")
                                onSuccess()
                            }
                            .addOnFailureListener { e ->
                                Log.e("TripViewModel", "❌ فشل في تحديث الحالة: ${e.message}")
                                onError(e.message ?: "حدث خطأ غير متوقع")
                            }
                    }
                } else {
                    val errorMessage = "❌ لا توجد رحلة لهذا المستخدم!"
                    Log.e("TripViewModel", errorMessage)
                    onError(errorMessage)
                }
            }
            .addOnFailureListener { e ->
                Log.e("TripViewModel", "❌ فشل في البحث عن الرحلة: ${e.message}")
                onError(e.message ?: "حدث خطأ غير متوقع")
            }
    }



    fun fetchTripsFromFirestore(onSuccess: (List<Trip>) -> Unit, onError: (String) -> Unit) {
        db.collection("trips")
            .whereEqualTo("status", "pending") // ✅ جلب الرحلات غير المقبولة فقط
            .get()
            .addOnSuccessListener { documents ->
                val trips = documents.mapNotNull { it.toObject(Trip::class.java) }
                onSuccess(trips)
            }
            .addOnFailureListener { e ->
                onError(e.message ?: "خطأ غير معروف")
            }
    }

    var responseMessage = mutableStateOf("")
    private val _tripDetails = mutableStateOf<com.example.capital_taxi.domain.TripResponse?>(null)
    val tripDetails: State<com.example.capital_taxi.domain.TripResponse?> = _tripDetails
    fun createTrip(
        context: Context, // ✅ مرّر الـ Context هنا
        userId: String,
        origin: Location,
        destination: Location,
        paymentMethod: String,
        fare: Double,
        distance: Double,
        token: String,
        coroutineScope: CoroutineScope,
        onSuccess: (com.example.capital_taxi.domain.TripResponse) -> Unit,
        onError: (String) -> Unit
    ) {

        val originName = getAddressFromLatLng(context, origin.lat, origin.lng) // ✅ استخدم context هنا
        val destinationName = getAddressFromLatLng(context, destination.lat, destination.lng)

        if (userId.isBlank()) {
            Log.e("TripViewModel", "❌ userId فارغ، لا يمكن إنشاء الرحلة!")
            return
        }

        val gson = Gson()

        val tripRequest = TripRequest(
            _id = "", // سيتم إنشاؤه تلقائيًا في السيرفر
            user = userId,
            origin = originName,
            destination = destinationName,
            paymentMethod = paymentMethod,
            fare = fare,
            distanceInKm = distance
        )
        Log.d("TripViewModel", "🚗 tripRequest: $tripRequest")

        coroutineScope.launch {
            try {
                val response = RetrofitClient.apiService.createTrip("Bearer $token", tripRequest)

                if (response.isSuccessful && response.body() != null) {
                    val tripResponse = response.body()!!
                    Log.d("TripViewModel", "✅ Trip created successfully: ${tripResponse.trip}")

                    val tripId = UUID.randomUUID().toString() // توليد ID فريد
                    val db = FirebaseFirestore.getInstance()
                    db.collection("trips").document(tripId).set(tripResponse.trip)
                        .addOnSuccessListener {
                            Log.d("TripViewModel", "✅ الرحلة أُضيفت إلى Firestore بنجاح!")
                        }
                        .addOnFailureListener { e ->
                            Log.e("TripViewModel", "❌ فشل في إضافة الرحلة: ${e.message}")
                        }


                    _tripDetails.value = tripResponse
                    onSuccess(tripResponse)
                } else {
                    Log.e("TripViewModel", "❌ Failed to create trip: ${response.message()}")
                    onError("Failed to create trip: ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("TripViewModel", "❌ Error creating trip: ${e.message}", e)
                onError("Error: ${e.message}")
            }
        }


    }
}


