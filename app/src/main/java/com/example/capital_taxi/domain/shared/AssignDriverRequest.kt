package com.example.capital_taxi.domain.shared

import android.content.Context
import android.location.Geocoder
import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import com.example.capital_taxi.domain.RetrofitClient
import com.example.capital_taxi.domain.Trip
import com.example.capital_taxi.domain.TripApiService
import com.example.capital_taxi.domain.TripRequest
import com.example.capital_taxi.domain.TripResponse
import com.example.capital_taxi.utils.Constants.ApiConstants
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ListenerRegistration
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Locale
import java.util.UUID
import java.util.concurrent.TimeUnit

data class LocationData(
    val lat: Double,
    val lng: Double
)





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


    fun observePendingTrips(
        driverId: String,
        onUpdate: (List<Trip>) -> Unit,
        onError: (String) -> Unit
    ): ListenerRegistration {
        val db = FirebaseFirestore.getInstance()

        return db.collection("trips")
            .whereEqualTo("status", "pending")
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    onError(error.message ?: "خطأ غير معروف")
                    return@addSnapshotListener
                }

                val trips = snapshots?.documents?.mapNotNull { doc ->
                    val trip = doc.toObject(Trip::class.java)
                    val cancelled = trip?.cancelledByDrivers ?: emptyList()
                    if (trip != null && !cancelled.contains(driverId)) trip else null
                } ?: emptyList()

                onUpdate(trips)
            }
    }



      val url = ApiConstants.base_URL
    private   val BASE_URL = url
    var responseMessage = mutableStateOf("")
    private val _tripDetails = mutableStateOf<TripResponse?>(null)
    val tripDetails: State<TripResponse?> = _tripDetails

    fun createTrip(
        context: Context,
        userId: String,
        origin: String,
        destination: String,
        paymentMethod: String,
        fare: Double,
        distance: Double,
        token: String,
        coroutineScope: CoroutineScope,
        onSuccess: (TripResponse) -> Unit,
        onError: (String) -> Unit
    ) {
        if (userId.isBlank()) {
            Log.e("TripViewModel", "❌ userId فارغ، لا يمكن إنشاء الرحلة!")
            return
        }

        val tripRequest = TripRequest(
            userId = userId, // ده هيتحول لـ "id" في JSON
            origin = origin,
            destination = destination,
            paymentMethod = paymentMethod,
            fare = fare,
            distanceInKm = distance
        )


        Log.d("TripViewModel", "🚗 tripRequest: $tripRequest")

        coroutineScope.launch {
            try {
                val baseUrl = url


                Log.d("TripViewModel", "Sending to: $baseUrl")
                Log.d("TripViewModel", "TripRequest JSON: ${Gson().toJson(tripRequest)}")

                val customApiService = Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .client(
                        OkHttpClient.Builder()
                            .connectTimeout(30, TimeUnit.SECONDS)
                            .readTimeout(30, TimeUnit.SECONDS)
                            .writeTimeout(30, TimeUnit.SECONDS)
                            .build()
                    )
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(TripApiService::class.java)

                val response = customApiService.createTrip("Bearer $token", tripRequest)

                if (response.isSuccessful && response.body() != null) {
                    val tripResponse = response.body()!!
                    Log.d("TripViewModel", "✅ Trip created successfully: ${tripResponse.trip}")

                    val tripId = UUID.randomUUID().toString()
                    val db = FirebaseFirestore.getInstance()
                    val gson = Gson()
                    val tripJson = gson.toJson(tripResponse.trip)
                    val tripData: Map<String, Any> = gson.fromJson(tripJson, object : TypeToken<Map<String, Any>>() {}.type)
                    val tripDataWithUserId = tripData.toMutableMap()
                    tripDataWithUserId["userId"] = userId

                    val originLatLng = getLatLngFromAddress(context, origin)
                    val destinationLatLng = getLatLngFromAddress(context, destination)

                    if (originLatLng != null) {
                        tripDataWithUserId["originMap"] = mapOf(
                            "lat" to originLatLng.first,
                            "lng" to originLatLng.second
                        )
                    }

                    if (destinationLatLng != null) {
                        tripDataWithUserId["destinationMap"] = mapOf(
                            "lat" to destinationLatLng.first,
                            "lng" to destinationLatLng.second
                        )
                    }
                    tripResponse.paymentUrl?.let {
                        tripDataWithUserId["paymentUrl"] = it
                    }

                    db.collection("trips").document(tripId).set(tripDataWithUserId)
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


private fun getLatLngFromAddress(context: Context, address: String): Pair<Double, Double>? {
    return try {
        val geocoder = Geocoder(context, Locale.getDefault())
        val results = geocoder.getFromLocationName(address, 1)
        if (!results.isNullOrEmpty()) {
            val location = results[0]
            Pair(location.latitude, location.longitude)
        } else null
    } catch (e: Exception) {
        Log.e("TripViewModel", "❌ Error getting lat/lng from address: ${e.message}")
        null
    }
}
