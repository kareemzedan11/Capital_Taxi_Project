package com.example.capital_taxi.Presentation.ui.Passengar.Screens.Home.UserHome.Components


import AcceptanceMap
import TopBar
import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.location.LocationManager
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.BottomSheetValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.rememberBottomSheetState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import drawerContent
import kotlinx.coroutines.launch
import androidx.compose.material.rememberBottomSheetScaffoldState

import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import kotlin.math.*  // يحتوي على sin, cos, sqrt, atan2, p*

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.capital_taxi.Helper.PartialBottomSheet
import com.example.capital_taxi.Helper.PermissionViewModel
import com.example.capital_taxi.Helper.checkLocationPermission
import com.example.capital_taxi.Presentation.ui.Passengar.Screens.Home.UserHome.Components.Trip_preparation.FindDriverCard
import com.example.capital_taxi.Presentation.ui.Passengar.Screens.Home.UserHome.Components.Trip_preparation.PickupWithDropOffButtons
import com.example.capital_taxi.Presentation.ui.Passengar.Screens.Home.UserHome.Components.Trip_request.searchAboutADriver
import com.example.capital_taxi.domain.Location
import com.example.capital_taxi.Presentation.ui.Driver.Screens.Home.Components.StatusTripViewModel
import com.example.capital_taxi.Presentation.ui.Driver.Screens.Home.Components.driverlocation
import com.example.capital_taxi.Presentation.ui.Passengar.Screens.Home.UserHome.Components.Trip_preparation.LocationViewModel
import com.example.capital_taxi.Presentation.ui.Passengar.Screens.Home.UserHome.Components.Waiting_for_the_driver.RideDetailsBottomSheetContent
import com.example.capital_taxi.domain.DirectionsViewModel
import com.example.capital_taxi.domain.FareViewModel
import com.example.capital_taxi.domain.TripResponse


import com.example.capital_taxi.domain.shared.TripViewModel


import com.example.myapplication.MapViewComposable

import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import org.osmdroid.util.GeoPoint
import java.io.IOException

import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.capital_taxi.Helper.rating.RateDriverBottomSheet
import com.example.capital_taxi.Helper.rating.submitRatingToFirebase
import com.example.capital_taxi.Navigation.Destination
import com.example.capital_taxi.Presentation.ui.Driver.Components.InProgressMap
import com.example.capital_taxi.Presentation.ui.Driver.Screens.Home.Components.Home_Components.TripViewModel2
import com.example.capital_taxi.Presentation.ui.Driver.Screens.Home.Components.Home_Components.getAddressFromLatLng
import com.example.capital_taxi.Presentation.ui.Driver.Screens.Home.Components.updateTripStatus
import com.example.capital_taxi.Presentation.ui.Driver.viewmodel.DriversViewModel
import com.example.capital_taxi.Presentation.ui.Passengar.Components.StateTripViewModel
import com.example.capital_taxi.Presentation.ui.Passengar.Components.fetchDriverInfo
import com.example.capital_taxi.Presentation.ui.Passengar.Components.waitForDriverIdFromTrip
import com.example.capital_taxi.Presentation.ui.Passengar.Screens.Home.UserHome.Components.During_the_trip.DriverArrivalCard
import com.example.capital_taxi.Presentation.ui.Passengar.Screens.Home.UserHome.Components.During_the_trip.RideInProgressScreen
import com.example.capital_taxi.Presentation.ui.Passengar.Screens.Home.UserHome.Components.Trip_Rating.TripCompletedScreen
import com.example.capital_taxi.R
import com.example.capital_taxi.data.repository.graphhopper_response.Details
import com.example.capital_taxi.data.repository.graphhopper_response.Hints
import com.example.capital_taxi.data.repository.graphhopper_response.Info
import com.example.capital_taxi.data.repository.graphhopper_response.Instruction
import com.example.capital_taxi.data.repository.graphhopper_response.Path
import com.example.capital_taxi.data.repository.graphhopper_response.graphhopper_response
import com.example.capital_taxi.domain.shared.TripInfoViewModel
import com.example.capital_taxi.domain.storedPoints
import com.example.capital_taxi.utils.SearchMapView
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.maps.android.PolyUtil
import findNearestIndex
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin

private val Context.dataStore by preferencesDataStore(name = "location_prefs")

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun homeScreenContent(navController: NavController) {
    var isConfirmed by remember { mutableStateOf(false) }
    var isTripBegin by remember { mutableStateOf(false) }


    var isSearch by remember { mutableStateOf(false) }
    var menuIconShow by remember { mutableStateOf(true) }
    var isstart by remember { mutableStateOf(false) }
    var passengerLocation2 by remember {
        mutableStateOf(
            GeoPoint(
                30.0444,
                31.2357
            )
        )
    }
    val tripViewModel2: TripViewModel2 = viewModel()
    val selectedTripId by tripViewModel2.selectedTripId.observeAsState()
    val driverId2State = remember { mutableStateOf<String?>(null) }
    val locationViewModel: LocationViewModel = viewModel()
    val pickupLatLng = locationViewModel.pickupLocation
    val dropoffLatLng = locationViewModel.dropoffLocation

    val fareViewModel: FareViewModel = viewModel()
    var fare2 = fareViewModel.fare  // لا حاجة لـ observeAsState
    var fare by remember { mutableStateOf<Double?>(null) }

    val permissionViewModel: PermissionViewModel = viewModel()
    val context = LocalContext.current

    val tripInfoViewmodel: TripInfoViewModel = viewModel()

    LaunchedEffect(context) {
        checkLocationPermission(context, permissionViewModel)
    }

    val isLocationGranted by permissionViewModel.isLocationGranted.collectAsState()
    val StatusTripViewModel: StatusTripViewModel = viewModel()

    val scope = rememberCoroutineScope()

    var tripStatus by remember { mutableStateOf("pending") } // الحالة الابتدائية
    var driverId by remember { mutableStateOf("") } // الحالة الابتدائية

    // BottomSheetScaffoldState
    val bottomSheetState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberBottomSheetState(initialValue = BottomSheetValue.Collapsed)
    )


    // DrawerState
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val gesturesEnabled = drawerState.isOpen

    // Check if location service is enabled
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    val isLocationEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    var tripDetails by remember { mutableStateOf<TripResponse?>(null) }

    // Remember updated state of location enabled and granted
    val currentIsLocationEnabled = rememberUpdatedState(isLocationEnabled)
    val currentIsLocationGranted = rememberUpdatedState(isLocationGranted)
    val sharedPreferences = context.getSharedPreferences("your_prefs", Context.MODE_PRIVATE)
    val token = sharedPreferences.getString("USER_TOKEN", null)

    // FusedLocationProviderClient to get current location
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    var locationName by remember { mutableStateOf("Fetching location...") }
    var destinationLat by remember { mutableStateOf(0.0) }
    var destinationLng by remember { mutableStateOf(0.0) }
    val tripViewModel: TripViewModel = viewModel()

    val stateTripViewModel: StateTripViewModel = viewModel()
    val state = stateTripViewModel.uiState.value

    var tripId by remember { mutableStateOf<String?>(null) }

    var startPoint = remember { mutableStateOf<GeoPoint?>(null) }
    val endPoint = remember { mutableStateOf<GeoPoint?>(null) }
    val reloadMap = remember { mutableStateOf(false) }

    val directionsViewModel: DirectionsViewModel = viewModel()
    val distance by directionsViewModel.distance.collectAsState()
    val duration by directionsViewModel.duration.collectAsState()
    var originString by remember { mutableStateOf<String?>(null) }
    var destinationString by remember { mutableStateOf<String?>(null) }
    var Time by remember { mutableStateOf<Long?>(null) }
    var formattedTime by remember { mutableStateOf<String?>(null) }
    var driverName by remember { mutableStateOf<String?>(null) }
    var carType by remember { mutableStateOf<String?>(null) }
    val locationDataStore = LocationDataStore(context)
    var showCancellationDialog by remember { mutableStateOf(false) }
    var tripListener by remember { mutableStateOf<ListenerRegistration?>(null) }
    val firestore = FirebaseFirestore.getInstance()
    var previousDriverLocation2 by remember { mutableStateOf<GeoPoint?>(null) }

    val DriversViewModel: DriversViewModel = viewModel()
    val coroutineScope = rememberCoroutineScope()


    val driverLocations = DriversViewModel.driverLocations
// Modify the state handling in LaunchedEffect
    LaunchedEffect(stateTripViewModel) {
        when {
            state.isCancelled -> {
                showCancellationDialog = true
            }
            state.isEnd -> {
                // Trip completed logic
            }
        }
    }




// ✅ إنشاء ViewModel مرة واحدة داخل Composable
    val locationViewModel2: LocationViewModel5 = viewModel()
    LaunchedEffect(Unit) {
        Log.d("TripCheck", "🚀 LaunchedEffect started")

        val sharedPreferences = context.getSharedPreferences("your_prefs", Context.MODE_PRIVATE)
        val activeTripId = sharedPreferences.getString("active_trip_id", null)

        Log.d("TripCheck", "🧠 activeTripId = $activeTripId")
        val sharedPref = context.getSharedPreferences("trip_prefs", Context.MODE_PRIVATE)
        val fareStr = sharedPref.getString("fare", "0.0") ?: "0.0"
        fare = fareStr.toDoubleOrNull() ?: 0.0
        if (activeTripId == null) {
            Log.d("TripCheck", "⚠️ No active trip found in SharedPreferences")
            return@LaunchedEffect
        }

        // خزن القيمة في متغير tripId اللي بتستخدمه في Compose
        tripId = activeTripId

        try {
            val querySnapshot = withContext(Dispatchers.IO) {
                Log.d("TripCheck", "🔎 Fetching trip from Firestore")
                FirebaseFirestore.getInstance().collection("trips")
                    .whereEqualTo("_id", activeTripId)
                    .get()
                    .await()
            }

            val document = querySnapshot.documents.firstOrNull()
            Log.d("TripCheck", "📄 Document fetched: ${document?.data}")

            if (document != null) {
                val status = document.getString("status") ?: ""
                Log.d("TripCheck", "📌 Trip status = $status")

                if (status in listOf("accepted", "Started", "InProgress")) {
                    Log.d("TripCheck", "✅ Active trip detected, updating state")
                    stateTripViewModel.updateTripStatus(status)
                } else {
                    Log.d("TripCheck", "🛑 Trip ended or cancelled, clearing active trip ID")
                    sharedPreferences.edit().remove("active_trip_id").apply()
                    stateTripViewModel.resetAll()
                    tripId = null // برضه نزّل المتغير لو الرحلة خلصت
                }
            } else {
                Log.d("TripCheck", "❌ No document found with this trip ID")
                sharedPreferences.edit().remove("active_trip_id").apply()
                stateTripViewModel.resetAll()
                tripId = null
            }
        } catch (e: Exception) {
            Log.e("TripCheck", "🔥 Exception while fetching trip: ${e.message}", e)
            sharedPreferences.edit().remove("active_trip_id").apply()
            stateTripViewModel.resetAll()
            tripId = null
        }
    }

    LaunchedEffect(Unit) {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            location?.let {
                val latLng = LatLng(it.latitude, it.longitude) // ✅ تحويل الموقع إلى LatLng

                locationViewModel2.updatePassengerLocation(latLng) // ✅ تخزين الموقع مؤقتًا
                Log.d(
                    "Location", "✅pass location: ${
                        locationViewModel2.passengerLocation?.let { latLng ->
                            GeoPoint(
                                latLng.latitude,
                                latLng.longitude
                            ) // ✅ تحويل LatLng إلى GeoPoint
                        }
                    }"
                )
                CoroutineScope(Dispatchers.IO).launch {
                    locationDataStore.saveLocation(it.latitude, it.longitude)
                }
                val geocoder = Geocoder(context, Locale.getDefault())
                val addressList = geocoder.getFromLocation(it.latitude, it.longitude, 1)
                locationName = if (addressList != null && addressList.isNotEmpty()) {
                    addressList[0].getAddressLine(0) // ✅ الحصول على العنوان
                } else {
                    "Unable to fetch location"
                }
            } ?: run {
                locationName = "Unable to fetch location"
            }
        }
    }
    DisposableEffect(tripId) {
        var documentListener: ListenerRegistration? = null

        onDispose {
            documentListener?.remove()
            tripListener?.remove()
        }

        if (tripId != null) {
            val query = firestore.collection("trips")
                .whereEqualTo("_id", tripId)

            val registration = query.addSnapshotListener { querySnapshot, error ->
                if (error != null) {
                    Log.e("TripStatus", "Error listening to trip", error)
                    return@addSnapshotListener
                }

                if (querySnapshot != null && !querySnapshot.isEmpty) {
                    val document = querySnapshot.documents.first()

                    documentListener = document.reference.addSnapshotListener { snapshot, error2 ->
                        if (error2 != null) {
                            Log.e("TripStatus", "Error listening to trip status", error2)
                            return@addSnapshotListener
                        }

                        snapshot?.let { doc ->
                            val status = doc.getString("status") ?: "pending"
                            if (status == "Cancelled" && !state.isCancelled) {
                                stateTripViewModel.setCancelled()
                            }
                        }
                    }
                }
            }
            tripListener = registration
        }

        onDispose {
            tripListener?.remove()
            documentListener?.remove()
        }
    }
    val viewmodel3: driverlocation = viewModel()

    val db = FirebaseFirestore.getInstance()
    val directions = remember { mutableStateListOf<GeoPoint>() }

    // متغير لتخزين الموقع
    // قيمة مبدئية
    var isDataLoading by remember { mutableStateOf(true) }  // حالة التحميل
    var driverLocation2 by remember {
        mutableStateOf(
            GeoPoint(
                30.0444,
                31.2357
            )
        )
    } // قيمة مبدئية

    LaunchedEffect(tripId) {
        isDataLoading = true

        try {
            val querySnapshot = db.collection("trips")
                .whereEqualTo("_id", tripId)
                .get()
                .await()

            if (!querySnapshot.isEmpty) {
                val document = querySnapshot.documents.first()
                fun formatDuration(durationInMillis: Long): String {
                    val minutes = (durationInMillis / 1000) / 60
                    val hours = minutes / 60
                    val remainingMinutes = minutes % 60

                    return if (hours > 0)
                        "$hours ${if (hours == 1L) "hour" else "hours"} and $remainingMinutes ${if (remainingMinutes == 1L) "minute" else "minutes"}"
                    else
                        "$remainingMinutes ${if (remainingMinutes == 1L) "minute" else "minutes"}"
                }


                // جلب بيانات originMap
                val originMap = document.get("originMap") as? Map<String, Any>
                val originLat = originMap?.get("lat") as? Double
                val originLng = originMap?.get("lng") as? Double
                originString =  document.get("origin") as? String
                  destinationString =  document.get("destination") as? String

// تحديد أقصى عدد للمحاولات
                val maxRetries = 5
                var retryCount = 0
                var time: Long? = null

// محاولة جلب القيمة عدة مرات
                while (time == null && retryCount < maxRetries) {
                    try {
                        val querySnapshot = db.collection("trips")
                            .whereEqualTo("_id", tripId)
                            .get()
                            .await()

                        if (!querySnapshot.isEmpty) {
                            val document = querySnapshot.documents.first()
                            time = document.get("time") as? Long

                            // في حالة كانت القيمة موجودة، نوقف المحاولات
                            if (time != null) {
                                  formattedTime = formatDuration(time)
                                Log.d("CheckType", "Formatted Time: $formattedTime")
                                break
                            }
                        } else {
                            Log.d("CheckType", "No document found with the specified tripId")
                        }

                        // زيادة عدد المحاولات
                        retryCount++

                        // انتظار فترة قصيرة قبل المحاولة التالية (تأخير 2 ثانية)
                        delay(2000) // تأخير لمدة 2 ثانية (يمكنك تعديله حسب الحاجة)

                    } catch (e: Exception) {
                        Log.e("CheckType", "Error fetching data: ${e.localizedMessage}")
                        retryCount++
                        delay(2000) // تأخير لمدة 2 ثانية بين المحاولات
                    }
                }

                if (time == null) {
                    Log.d("CheckType", "Failed to fetch time after $maxRetries retries.")
                    // تعامل مع الحالة عندما تكون القيمة غير موجودة بعد محاولات متعددة
                      formattedTime = "غير متوفر"
                }



                // جلب بيانات destinationMap
                val destinationMap =
                    document.get("destinationMap") as? Map<String, Any>
                val destinationLat = destinationMap?.get("lat") as? Double
                val destinationLng = destinationMap?.get("lng") as? Double

                fare = document.get("fare") as? Double ?: 0.0
                val sharedPref = context.getSharedPreferences("trip_prefs", Context.MODE_PRIVATE)
                sharedPref.edit().putString("fare", fare.toString()).apply()


                val  driverLocation =
                    document.get("driverLocation") as? Map<String, Any>
                val driverLocationLat = driverLocation?.get("lat") as? Double
                val driverLocationLng = driverLocation?.get("lng") as? Double

                if (originLat != null && originLng != null && destinationLat != null && destinationLng != null) {
                    passengerLocation2 = GeoPoint(originLat, originLng)
                    driverLocation2 = GeoPoint(driverLocationLat!!,driverLocationLng!!)
                    val result = DirectionsApi.getDirections(
                        start = passengerLocation2,
                        end = driverLocation2,
                        apiKey = "71ab0bb4-9572-4423-ab8f-332deb2827a7",
                        context = context,
                        tripId = tripId!!
                    )

                    when (result) {
                        is ResultWrapper.Success -> {
                            val response = result.value
                            val encodedPolyline =
                                response.paths.firstOrNull()?.points
                            if (encodedPolyline != null) {
                                // Decode the polyline string into a list of GeoPoints
                                val decodedPoints =
                                    PolyUtil.decode(encodedPolyline)
                                        .map { latLng ->
                                            GeoPoint(
                                                latLng.latitude,
                                                latLng.longitude
                                            )
                                        }

                                directions.clear()
                                directions.addAll(decodedPoints)
                            }
                        }

                        is ResultWrapper.Failure -> {
                            Log.e(
                                "Directions",
                                "فشل في جلب الاتجاهات: ${result.exception.message}"
                            )
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("Firebase", "Error fetching trip data: ${e.message}")
        } finally {
            isDataLoading = false
        }
    }
// الحصول على الموقع المخزن من ViewModel
    val driverLocation = viewmodel3.driverLocation.value
    LaunchedEffect(pickupLatLng, dropoffLatLng) {
        startPoint.value = pickupLatLng?.let { GeoPoint(it.latitude, it.longitude) }

        startPoint.value?.let { geoPoint ->
            locationViewModel2.updatePassengerLocation(
                LatLng(
                    geoPoint.latitude,
                    geoPoint.longitude
                )
            ) // ✅ التحويل الصحيح
        }

        endPoint.value = dropoffLatLng?.let { GeoPoint(it.latitude, it.longitude) }
        reloadMap.value = !reloadMap.value // تغيير الحالة لإجبار إعادة تركيب الخريطة
    }


    fun calculateDistanceInMeters(start: GeoPoint, end: GeoPoint): Double {
        val R = 6371000.0 // نصف قطر الأرض بالمتر
        val lat1 = Math.toRadians(start.latitude)
        val lon1 = Math.toRadians(start.longitude)
        val lat2 = Math.toRadians(end.latitude)
        val lon2 = Math.toRadians(end.longitude)

        val dLat = lat2 - lat1
        val dLon = lon2 - lon1

        val a = sin(dLat / 2).pow(2.0) + cos(lat1) * cos(lat2) * sin(dLon / 2).pow(2.0)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return R * c
    }

    val directions2 = remember { mutableStateListOf<GeoPoint>() }
    var showRatingSheet by remember { mutableStateOf(false) }
    // متغير لتخزين الموقع
    // قيمة مبدئية
    var isDataLoading2 by remember { mutableStateOf(true) }  // حالة التحميل
    var destination by remember {
        mutableStateOf(
            GeoPoint(
                30.0444,
                31.2357
            )
        )
    } // قيمة مبدئية
    LaunchedEffect(tripId) {
        isDataLoading2 = true

        try {
            val tripDocRef = db.collection("trips")
                .whereEqualTo("_id", tripId)
                .get()
                .await()
                .documents
                .firstOrNull()

            if (tripDocRef != null) {
                val documentRef = db.collection("trips").document(tripDocRef.id)

                // --------- قراءة origin و destination لمرة واحدة ------------
                val originMap = tripDocRef.get("originMap") as? Map<String, Any>
                val destinationMap = tripDocRef.get("destinationMap") as? Map<String, Any>
                val originLat = originMap?.get("lat") as? Double
                val originLng = originMap?.get("lng") as? Double
                val destinationLat = destinationMap?.get("lat") as? Double
                val destinationLng = destinationMap?.get("lng") as? Double

                if (originLat != null && originLng != null && destinationLat != null && destinationLng != null) {
                    passengerLocation2 = GeoPoint(originLat, originLng)
                    destination = GeoPoint(destinationLat, destinationLng)

                    // جلب الاتجاهات
                    val result = DirectionsApi.getDirections(
                        start = passengerLocation2,
                        end = destination,
                        apiKey = "c69abe50-60d2-43bc-82b1-81cbdcebeddc",
                        context = context,
                        tripId = tripId!!
                    )

                    if (result is ResultWrapper.Success) {
                        val points = result.value.paths.firstOrNull()?.points
                        if (points != null) {
                            val decodedPoints = PolyUtil.decode(points).map {
                                GeoPoint(it.latitude, it.longitude)
                            }
                            directions2.clear()
                            directions2.addAll(decodedPoints)
                        }
                    }
                }

                // --------- الاستماع لتحديث موقع السائق (map) ------------
                documentRef.addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.e("Firebase", "Error listening to driver location: ${error.message}")
                        return@addSnapshotListener
                    }

                    val mapData = snapshot?.get("driverLocation") as? Map<String, Any>
                    val lat = mapData?.get("latitude") as? Double
                    val lng = mapData?.get("longitude") as? Double

                    if (lat != null && lng != null) {
                        val updatedLocation = GeoPoint(lat, lng)

                        previousDriverLocation2 = driverLocation2 // احتفظ بالموقع السابق
                        driverLocation2 = updatedLocation         // حدث بالموقع الجديد
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("Firebase", "Error fetching trip data: ${e.message}")
        } finally {
            isDataLoading2 = false
        }
    }

    var showBottomSheet by remember { mutableStateOf(false) }
    var selectedPaymentMethod by remember { mutableStateOf("Cash") }

    PartialBottomSheet(
        showBottomSheet = showBottomSheet,
        onDismissRequest = { showBottomSheet = false }
    ) {
        PaymentMethodContent(
            selectedMethod = selectedPaymentMethod,
            onOptionSelected = {
                selectedPaymentMethod = it
                showBottomSheet = false // تغلق الـ BottomSheet بعد الاختيار (اختياري)
            }
        )
    }

    var driverLocationState by remember { mutableStateOf<LatLng?>(null) }
    // Main Container

    val userId = sharedPreferences.getString("USER_ID", null)
    val userType = sharedPreferences.getString("user_type", null)


    Box(modifier = Modifier.fillMaxSize()) {


when{
    state.isEnd->{
        TripCompletedScreen(
            startLocation = originString ?: "undefined", // "غير محدد"
            endLocation = destinationString ?:"undefined", // "غير محدد"
            fare = fare.toString()?.plus(" EGP") ?: "undefined", // "غير متاح"
            duration = formattedTime ?: "{undefined}", // "غير متاح"
            distance = distance?.let { "$it km" } ?: "undefined", // "غير متاح"
            driverName = driverName?:"undefined", // "سائق غير معروف"
            carModel =carType?: "undefied", // "مركبة غير معروفة"
            onRateClick = {
                sharedPreferences.edit().remove("active_trip_id").apply()

                showRatingSheet = true
                storedPoints=null


            },
            onReturnHomeClick = {
                sharedPreferences.edit().remove("active_trip_id").apply()

                storedPoints=null
                stateTripViewModel.resetAll()
                navController.navigate(Destination.UserHomeScreen.route) {
                    popUpTo(Destination.UserHomeScreen.route) { inclusive = true }
                }
            },

            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
        )
        if (showRatingSheet) {
            RateDriverBottomSheet(
                onSubmit = { ratingValue ->
                    sharedPreferences.edit().remove("active_trip_id").apply()

                    showRatingSheet = false
                    if (driverId2State.value!!.isNotEmpty()) {
                        submitRatingToFirebase(driverId2State.value!!, ratingValue)
                    } else {
                        Log.e("Rating", "❌ driverId غير موجود")
                    }
                    storedPoints = null
                    stateTripViewModel.resetAll()
                    navController.navigate(Destination.UserHomeScreen.route) {
                        popUpTo(Destination.UserHomeScreen.route) { inclusive = true }
                    }
                },
                onDismiss = { showRatingSheet = false }
            )
        }
    }
    else->{
        // Drawer
        ModalNavigationDrawer(
            drawerState = drawerState,
            gesturesEnabled = gesturesEnabled,
            drawerContent = {
                ModalDrawerSheet(
                    modifier = Modifier.fillMaxWidth(0.7f) // Set drawer width to 60% of screen
                ) {
                    drawerContent(navController)
                }
            }
        ) {
            BottomSheetScaffold(
                scaffoldState = bottomSheetState,
                sheetPeekHeight = when {
                    state.isTripBegin  ->0.dp

                    isConfirmed -> 200.dp  // إذا كان مؤكدًا ولكن ليس بدء
                    else -> 500.dp  // الحالة الافتراضية
                },
                content = { padding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                    ) {

                        if (state.isInitialPickup) {

                            MapViewComposable(
                                startPoint = startPoint.value,
                                endPoint = endPoint.value
                            )
                        }
                        if (state.isAccepted) {

                            tripId?.let {
                                Log.d("tripId", it)
                                TrackDriverScreen(
                                    tripId = it,

                                    passengerLocation = passengerLocation2
                                )
//                                TrackDriverScreen(
//                                    tripId = it,
//
//                                    driverLocation = current2,
//                                    previousDriverLocation = previous2,
//                                    passengerLocation = passengerLocation2
//                                )
                            }

                        }


                        if (state.inProgress||state.isStart) {
                            isTripBegin=true
                            isstart = true
                            InProgressMap(
                                currentLocation = driverLocation2,
                                previousLocation = previousDriverLocation2,
                                destination = destination,
                                directions = directions2
                            )

                        }
                        if (state.isCancelled) {
                            storedPoints = null
                            MapViewComposable(
                                startPoint = startPoint.value,
                                endPoint = endPoint.value
                            )
                        }
                        if (state.isSearch) {

                            // نزّل تركيبة Lottie مرّة واحدة وتتكرر إلى ما لا نهاية
                            val composition by rememberLottieComposition(
                                LottieCompositionSpec.RawRes(R.raw.searching)         // أو LottieCompositionSpec.Url(...)
                            )

                            // غلّف الخريطة والأنيميشن في Box بحيث تُرسَم Lottie فوقها
                            Box(modifier = Modifier.fillMaxSize()) {
                                val nearbyDrivers = driverLocations.filter { (_, location) ->
                                    calculateDistanceInMeters(location, startPoint.value!!) <= 1000  // أقل من 1000 متر (1 كم)
                                }

                                // الخريطة
                                SearchMapView(
                                    driverLocations = nearbyDrivers.toMutableStateList(),
                                    pickupLocation = startPoint.value!!,
                                    dropoffLocation = endPoint.value!!
                                )


                                // أنيميشن Lottie يشتغل بلا توقف
                                LottieAnimation(
                                    composition = composition,
                                    iterations  = LottieConstants.IterateForever,
                                    modifier    = Modifier
                                        .align(Alignment.Center)   // مكان العرض (أعلى المنتصف مثالًا)
                                        .size(220.dp)                 // غيّر الحجم كما تريد
                                )
                            }
                        }

                        LaunchedEffect("67b0b246322cf017e42a9d3c") {
                            val database = FirebaseDatabase.getInstance()
                            val driverLocationRef =
                                database.getReference("drivers").child("67b0b246322cf017e42a9d3c")
                                    .child("location")

                            driverLocationRef.addValueEventListener(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    val lat = snapshot.child("lat").getValue(Double::class.java)
                                    val lng = snapshot.child("lng").getValue(Double::class.java)

                                    if (lat != null && lng != null) {
                                        driverLocationState = LatLng(lat, lng)
                                        Log.d(
                                            "FirebaseLocation",
                                            "✅ Driver Location Updated: ($lat, $lng)"
                                        )
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    Log.e(
                                        "FirebaseLocation",
                                        "❌ Error fetching driver location: ${error.message}"
                                    )
                                }
                            })
                        }
                        if (menuIconShow == true) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .align(Alignment.TopStart)
                            ) {
                                if (state.isInitialPickup && !state.isSearch && !state.isStart && !state.isTripBegin && !state.isEnd && !state.inProgress && !state.isAccepted) {

                                    TopBar(
                                        onOpenDrawer = {
                                            scope.launch {
                                                if (drawerState.isClosed) {
                                                    drawerState.open()
                                                } else {
                                                    drawerState.close()
                                                }
                                            }
                                        },
                                        navController = navController
                                    )
                                }
if (state.isInitialPickup && !state.isSearch && !state.isStart && !state.isTripBegin && !state.isEnd && !state.inProgress && !state.isAccepted) {
    DraggableIcon(navController = navController)
}
                            }
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .align(Alignment.BottomCenter)
                        ) {
                            when {state.isTripBegin ->{



                                LaunchedEffect(tripStatus) {
                                    if (tripStatus == "Started") {
                                        stateTripViewModel.updateTripStatus("Started")
                                    }
                                }

                                Log.d("UI", "Starting Trip")
                                DriverArrivalCard(
                                    onTripCancelled = {
                                        CoroutineScope(Dispatchers.IO).launch {


                                            updateTripStatus(tripId!!, "Cancelled")
                                        }
                                        // Handle trip cancellation
                                        println("Trip automatically cancelled")
                                    },

                                    tripId = tripId!!,

                                    modifier =Modifier.fillMaxWidth()
                                )
                            }}
                        }
                    }
                },
                sheetContent = {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .border(
                                width = 2.dp,
                                color = Color.Gray,
                                shape = RoundedCornerShape(16.dp)
                            )
                    ) {

                        when {

                            state.inProgress -> {

                                LaunchedEffect(tripStatus) {
                                    if (tripStatus == "Completed") {
                                        stateTripViewModel.updateTripStatus("Completed")
                                    }
                                }
                                var showChatSheet = remember { mutableStateOf(false) }

                                RideInProgressScreen(
                                    startLocation = originString ?: "",
                                    endLocation = destinationString ?: "مطار القاهرة الدولي",
                                    estimatedTime = formattedTime ?: "30 دقيقة",
                                    onEmergencyClick = {
                                        Log.d("EMERGENCY", "🚨 فتح شات الطوارئ")
                                        showChatSheet.value = true
                                    }
                                )
                                if (showChatSheet.value) {
                                    ModalBottomSheet(
                                        onDismissRequest = { showChatSheet.value = false }
                                    ) {
                                        EmergencyChatSheet(
                                            driverId = driverId,
                                            driverName =driverName!!,
                                            passengerId = userId!!,
                                            passengerName ="test" ,
                                            tripNumber =tripId!!,
                                            tripFrom ="tanta",
                                            tripTo ="cairo",
                                            onClose = { showChatSheet.value=  false }
                                        )

                                    }
                                }

                            }
                            state.isAccepted -> {
                                LaunchedEffect(Unit) {
                                    waitForDriverIdFromTrip(
                                        tripId = tripId!!,
                                        onDriverIdReady = { driverId ->
                                            fetchDriverInfo(
                                                driverId,
                                                onSuccess = { name, car ->
                                                    driverName = name ?: "غير معروف"
                                                    carType = car ?: "غير معروف"
                                                    driverId2State.value = driverId // خزنه هنا
                                                    Log.d("DriverData", "🚗 الاسم: $driverName - النوع: $carType")
                                                },
                                            )
                                        },
                                    )
                                }
                                val sharedPref = context.getSharedPreferences("your_prefs", Context.MODE_PRIVATE)
                                val userId = sharedPref.getString("USER_ID", null)


                                if (tripId != null) {
                                    sharedPreferences.edit().putString("active_trip_id", tripId).apply()
                                    Log.d("SharedPreferences", "Saved active_trip_id = $tripId")
                                } else {
                                    Log.e("SharedPreferences", "tripId is null, cannot save active_trip_id")
                                }


                                if (driverId2State.value != null) {
                                    RideDetailsBottomSheetContent(
                                        onclick = { stateTripViewModel.setCancelled() },
                                        navController = navController,
                                        tripid = tripId!!,
                                        UserId = userId!!,
                                        driverid = driverId2State.value!! // استخدم القيمة هنا
                                    ,
                                        fare!!
                                    )
                                }

                                LaunchedEffect(tripStatus) {
                                    if (tripStatus == "InProgress") {
                                        stateTripViewModel.updateTripStatus("InProgress")
                                    }
                                }

                            }

                            state.isSearch -> {
                                searchAboutADriver(
                                    oncancelled = {
                                        coroutineScope.launch {
                                            updateTripStatus(tripId!!, "Cancelled")
                                            storedPoints = null
                                            stateTripViewModel.resetAll()
                                            navController.navigate(Destination.UserHomeScreen.route) {
                                                popUpTo(Destination.UserHomeScreen.route) { inclusive = true }
                                            }
                                        }
                                    }
                                )

                                LaunchedEffect(tripStatus) {
                                    if (tripStatus == "accepted") {
                                        stateTripViewModel.updateTripStatus("accepted")
                                    }
                                }
                            }


                            state.isCancelled ->{

                                    AlertDialog(
                                        onDismissRequest = {
                                            sharedPreferences.edit().remove("active_trip_id").apply()

                                            showCancellationDialog = false
                                            stateTripViewModel.resetAll()
                                            navController.navigate(Destination.UserHomeScreen.route) {
                                                popUpTo(Destination.UserHomeScreen.route) { inclusive = true }
                                            }
                                        },
                                        title = {
                                            Text(text = "Trip Cancelled")
                                        },
                                        text = {
                                            Text(text = "Your trip has been cancelled. You'll be returned to the home screen.")
                                        },
                                        confirmButton = {
                                            Button(
                                                onClick = {
                                                    sharedPreferences.edit().remove("active_trip_id").apply()

                                                    storedPoints = null
                                                    showCancellationDialog = false
                                                    stateTripViewModel.resetAll()
                                                    navController.navigate(Destination.UserHomeScreen.route) {
                                                        popUpTo(Destination.UserHomeScreen.route) { inclusive = true }
                                                    }
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                                            ) {
                                                Text("OK", color = Color.White)
                                            }
                                        }
                                    )

                            }
                            state.isInitialPickup -> {



                                PickupWithDropOffButtons(

                                    navController = navController,
                                    locationName = locationName
                                )
                            }



                        }

                    }
                }
            )
           LaunchedEffect(Unit) {
                while (true) {
                    delay(2000) // تحديث كل 2 ثانية

                    // تحقق من أن selectedTripId ليس null
                    tripId?.let {
                        tripViewModel.getTripStatusById(it, onSuccess = { newStatus ->
                            tripStatus = newStatus // ✅ تحديث الحالة باستمرار
                        }, onError = { errorMessage ->
                            Log.e("TripStatus", "❌ خطأ في جلب الحالة: $errorMessage + $tripId")
                        })
                    } ?: run {
                        Log.e("TripStatus", "❌ selectedTripId is null!")
                    }
                }
            }


            val context = LocalContext.current
            if (currentIsLocationEnabled.value &&
                currentIsLocationGranted.value && !isConfirmed && !isSearch&&!isstart
                &&!isTripBegin&&state.isInitialPickup&&!state.isAccepted
                &&!state.inProgress&&!state.isStart&&!state.isTripBegin ) {
                val Savedtoken =
                    token // Fetch or pass the token
                FindDriverCard { selectedPaymentMethod ->

                    if (storedPoints == null || startPoint.value == null || endPoint.value == null || tripStatus != "pending") {
                        Toast.makeText(context, "Please select both pickup and drop-off locations", Toast.LENGTH_SHORT).show()
                        return@FindDriverCard
                    }

                    if (tripStatus == "pending") {
                        Log.d("TripScreen", "FindDriverCard clicked")
                        isSearch = true
                        stateTripViewModel.searchDriver()

                        val sharedPreferences = context.getSharedPreferences("your_prefs", Context.MODE_PRIVATE)
                        val userId = sharedPreferences.getString("USER_ID", null)
                        val userBalance = sharedPreferences.getFloat("USER_BALANCE", 0f)

                        if (userId == null) {
                            Toast.makeText(context, "User not logged in", Toast.LENGTH_SHORT).show()
                            return@FindDriverCard
                        }

                        val origin = getAddressFromLatLng(
                            context,
                            latitude = startPoint.value?.latitude ?: 0.0,
                            longitude = startPoint.value?.longitude ?: 0.0
                        )

                        val destination = getAddressFromLatLng(
                            context,
                            latitude = endPoint.value?.latitude ?: 0.0,
                            longitude = endPoint.value?.longitude ?: 0.0
                        )

                        val fare = fare2
                        val distanceInKm = distance

                        if (Savedtoken != null && distanceInKm != null) {
                            tripViewModel.createTrip(
                                context = context,
                                userId = userId,
                                origin = origin,
                                destination = destination,
                                paymentMethod = selectedPaymentMethod, // ✅ هنا بنستخدم القيمة المختارة فعلاً
                                fare = fare!!,
                                distance = distanceInKm,
                                token = Savedtoken,
                                coroutineScope = CoroutineScope(Dispatchers.Main),
                                onSuccess = { tripResponse ->
                                    Log.d("tripResponse", "🚗 Trip id: ${tripResponse.trip._id}")
                                    tripId = tripResponse.trip._id
                                },
                                onError = { errorMessage ->
                                    Log.e("TripScreen", "❌ خطأ أثناء إنشاء الرحلة: $errorMessage")
                                }
                            )
                        }

                        endPoint.value = GeoPoint(destinationLat, destinationLng)
                    }
                }

            }
        }
    }
    }

}



}

class LocationViewModel5 : ViewModel() {
    var passengerLocation by mutableStateOf<LatLng?>(null)
        private set

    fun updatePassengerLocation(latLng: LatLng) {
        passengerLocation = latLng
    }
}

fun getLatLngFromAddressNominatim(address: String, onResult: (LatLng?) -> Unit) {
    val url = "https://nominatim.openstreetmap.org/search?format=json&q=${Uri.encode(address)}"

    val request = Request.Builder().url(url).header("User-Agent", "YourAppNam e").build()
    val client = OkHttpClient()

    client.newCall(request).enqueue(object : Callback {
        override fun onResponse(call: Call, response: Response) {
            response.body?.string()?.let { json ->
                val jsonArray = JSONArray(json)
                if (jsonArray.length() > 0) {
                    val firstResult = jsonArray.getJSONObject(0)
                    val lat = firstResult.getDouble("lat")
                    val lon = firstResult.getDouble("lon")
                    onResult(LatLng(lat, lon))
                } else {
                    onResult(null)
                }
            }
        }

        override fun onFailure(call: Call, e: IOException) {
            e.printStackTrace()
            onResult(null)
        }
    })
}@Composable
fun TrackDriverScreen(
    passengerLocation: GeoPoint?,
    tripId: String,
    context: Context = LocalContext.current
) {
    // Firebase references
    val tripsRef = remember { FirebaseFirestore.getInstance().collection("trips") }

    // State variables
    var driverLocation by remember { mutableStateOf<GeoPoint?>(null) }
    var directionsFetched by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var directions by remember { mutableStateOf<List<GeoPoint>>(emptyList()) }

    // دالة جلب الاتجاهات باستخدام OSRM
    fun fetchOSRMDirections(
        start: GeoPoint,
        end: GeoPoint,
        onSuccess: (List<GeoPoint>) -> Unit,
        onError: (String) -> Unit
    ) {
        isLoading = true
        Log.d("tripId 2 ", tripId)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = DirectionsApi.getDirections(
                    start, end,
                    apiKey =  "c69abe50-60d2-43bc-82b1-81cbdcebeddc",
                    tripId = tripId
                )

                withContext(Dispatchers.Main) {
                    when (response) {
                        is ResultWrapper.Success -> {
                            val path = response.value.paths.firstOrNull()
                            if (path != null) {
                                val geoPoints = PolyUtil.decode(path.points).map {
                                    GeoPoint(it.latitude, it.longitude)
                                }
                                directionsFetched = true
                                onSuccess(geoPoints)
                            }
                        }


                        is ResultWrapper.Failure -> {
                            onError(response.exception.message ?: "Unknown error")
                        }
                    }
                    isLoading = false
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    isLoading = false
                    onError("Network error: ${e.message ?: "Unknown error"}")
                }
            }
        }
    }
    var previousDriverLocation by remember { mutableStateOf<GeoPoint?>(null) }

    LaunchedEffect(tripId) {
        tripsRef.whereEqualTo("_id", tripId)
            .addSnapshotListener { documents, error ->
                if (error != null) return@addSnapshotListener

                documents?.let {
                    for (document in it) {
                        val driverLat = document.getDouble("driverLocation.latitude")
                        val driverLng = document.getDouble("driverLocation.longitude")

                        if (driverLat != null && driverLng != null) {
                            val newLocation = GeoPoint(driverLat, driverLng)
                            previousDriverLocation = driverLocation
                            driverLocation = newLocation
                        }
                    }
                }
            }
    }


    // Fetch directions when locations are available
    LaunchedEffect(driverLocation, passengerLocation) {
        if (driverLocation != null && passengerLocation != null && !directionsFetched) {
            fetchOSRMDirections(
                start = driverLocation!!,
                end = passengerLocation,
                onSuccess = { routePoints ->
                    directions = routePoints
                    driverLocation?.let { findNearestIndex(current = it, path =routePoints ) }
                    directionsFetched = true  // ✅ ضروري يتكتب هنا أول ما النجاح يحصل
                    Log.d("OSRM Directions", "✅ Directions fetched: ${routePoints} points")
                },
                onError = { error ->
                    Log.e("OSRM Directions", "❌ Error: $error")
                    Toast.makeText(context, "خطأ في جلب الاتجاهات: $error", Toast.LENGTH_SHORT)
                        .show()
                }
            )
        }
    }

    // UI
    Box(modifier = Modifier.fillMaxSize()) {

        AcceptanceMap(
            currentLocation = driverLocation,
            previousLocation = previousDriverLocation,
            passengerLocation = passengerLocation,
            directions = directions
        )


        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(16.dp)
            )
        }
    }
}



// ملف ResultWrapper.kt
sealed class ResultWrapper<out T> {
    data class Success<out T>(val value: T) : ResultWrapper<T>()
    data class Failure(val exception: Throwable) : ResultWrapper<Nothing>()
}

object DirectionsApi {
    private const val BASE_URL = "https://graphhopper.com/api/1/route?"
    private val client = OkHttpClient()

    suspend fun getDirections(
        start: GeoPoint,
        end: GeoPoint,
        apiKey: String,
        context: Context? = null,
        tripId:String,

    ): ResultWrapper<graphhopper_response> = withContext(Dispatchers.IO) {
        try {
            val url = "${BASE_URL}point=${start.latitude},${start.longitude}" +
                    "&point=${end.latitude},${end.longitude}" +
                    "&instructions=true&points_encoded=true&key=$apiKey"

            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()

            if (!response.isSuccessful) {
                return@withContext ResultWrapper.Failure(Exception("API Error: ${response.code}"))
            }

            val json = JSONObject(
                response.body?.string()
                    ?: return@withContext ResultWrapper.Failure(Exception("Empty response"))
            )
            val result = parseGraphHopperResponse(json,  context,tripId)
            ResultWrapper.Success(result)

        } catch (e: Exception) {
            ResultWrapper.Failure(e)
        }
    }

      @SuppressLint("SuspiciousIndentation")
      fun parseGraphHopperResponse(
        json: JSONObject,
        context: Context? = null,
        tripId:String,
        tripViewModel: TripInfoViewModel ?=null

      ): graphhopper_response {
        val info = json.getJSONObject("info")
        val pathsJson = json.getJSONArray("paths")
        val paths = mutableListOf<Path>()

        fun JSONArray.toListString(): List<String> {
            return List(length()) { getString(it) }
        }

        val infoObj = Info(
            copyrights = info.getJSONArray("copyrights").toListString(),
            road_data_timestamp = info.getString("road_data_timestamp"),
            took = info.getInt("took")
        ).also { println("Info: $it") }

        for (i in 0 until pathsJson.length()) {
            val pathObj = pathsJson.getJSONObject(i)
            val instructions = mutableListOf<Instruction>()

            val instructionsJson = pathObj.getJSONArray("instructions")
            for (j in 0 until instructionsJson.length()) {
                val instObj = instructionsJson.getJSONObject(j)
                instructions.add(
                    Instruction(
                        distance = instObj.getDouble("distance"),
                        exit_number = instObj.optInt("exit_number", 0),
                        exited = instObj.optBoolean("exited", false),
                        interval = instObj.getJSONArray("interval").let { arr ->
                            List(arr.length()) { arr.getInt(it) }
                        },
                        last_heading = instObj.optDouble("last_heading", 0.0),
                        sign = instObj.getInt("sign"),
                        street_destination = instObj.optString("street_destination", ""),
                        street_name = instObj.optString("street_name", ""),
                        text = instObj.getString("text"),
                        time = instObj.getInt("time"),
                        turn_angle = instObj.optDouble("turn_angle", 0.0)
                    )
                )
            }

            val path = Path(
                ascend = pathObj.getDouble("ascend"),
                bbox = pathObj.getJSONArray("bbox").let { arr ->
                    List(arr.length()) { arr.getDouble(it) }
                },
                descend = pathObj.getDouble("descend"),
                details = Details(),
                distance = pathObj.getDouble("distance"),
                instructions = instructions,
                legs = emptyList(),
                points = pathObj.getString("points"),
                points_encoded = pathObj.getBoolean("points_encoded"),
                points_encoded_multiplier = pathObj.getDouble("points_encoded_multiplier"),
                snapped_waypoints = pathObj.getString("snapped_waypoints"),
                time = pathObj.getInt("time"),
                transfers = pathObj.getInt("transfers"),
                weight = pathObj.getDouble("weight")
            )

            Log.d("New", "New path:   time=${pathObj.getInt("time")}")
            paths.add(path)
//
//// تجهيز التعليمات بصيغة Map
//            val instructionList = instructions.map { inst ->
//                mapOf(
//                    "text" to inst.text,
//                    "distance" to inst.distance,
//                    "time" to inst.time,
//                    "sign" to inst.sign,
//                    "street_name" to inst.street_name,
//                    "street_destination" to inst.street_destination,
//                    "exit_number" to inst.exit_number,
//                    "exited" to inst.exited,
//                    "interval" to inst.interval,
//                    "last_heading" to inst.last_heading,
//                    "turn_angle" to inst.turn_angle
//                )
//            }

            val db = FirebaseFirestore.getInstance()

            val tripId = tripId
            val tripRef = db.collection("trips") // افترض أن الـ collection التي تحتوي على البيانات اسمها "trips"

// Coroutine للبحث المتكرر مع تأخير
            GlobalScope.launch {
                var attempts = 0
                val maxAttempts = 5  // عدد المحاولات
                var success = false

                while (attempts < maxAttempts && !success) {
                    try {
                        // استرجاع البيانات من Firebase باستخدام الـ Tripid
                        val querySnapshot = tripRef.whereEqualTo("_id", tripId).get().await()  // await تجعلها متماثلة مع التأخير

                        if (!querySnapshot.isEmpty) {
                            // إذا تم العثور على بيانات تطابق الـ Tripid
                            val document = querySnapshot.documents[0] // البيانات المتطابقة من Firebase
                            val data = document.data

                            // الآن تحقق إذا كان _id في البيانات الموجودة يساوي Tripid
                            if (data != null && data["_id"] == tripId) {
                                // البيانات تطابقت، يمكنك الآن إضافة بيانات جديدة أو تحديثها
                                val newTripInfo = hashMapOf(
                                    "distance" to path.distance,
                                    "points" to path.points,
                                    "time" to pathObj.getInt("time"),
                                  //  "instructions" to instructionList // ✅ تم إضافة التعليمات هنا
                                )

                                Log.d("Firebase", "New path: distance=${path.distance}, time=${path.time}")

                                // إرسال أو تحديث البيانات على Firebase تحت هذا الـ Tripid
                                document.reference.set(newTripInfo, SetOptions.merge()) // استخدام merge للتحديث بدون مسح البيانات السابقة

                                Log.d("Firebase", "Data successfully updated in Firebase!")
                                success = true  // لو تم التحديث بنجاح
                            } else {
                                Log.d("Firebase", "ID mismatch: _id does not match TripId")
                            }
                        } else {
                            Log.d("Firebase", "No matching Trip found with the given Trip ID.")
                        }
                    } catch (e: Exception) {
                        // في حالة حدوث أي خطأ
                        Log.e("Firebase", "Error retrieving trip data: ${e.message}")
                    }

                    if (!success) {
                        attempts++
                        Log.d("Firebase", "Attempt #$attempts failed, retrying in 3 seconds...")
                        delay(3000) // تأخير لمدة 3 ثواني قبل المحاولة مرة أخرى
                    }
                }

                if (!success) {
                    Log.e("Firebase", "Max attempts reached. Data update failed.")
                }

            }


        }

        val hints = Hints(
            visitedNodesSum = json.getJSONObject("hints").getInt("visited_nodes.sum"),
            visitedNodesAverage = json.getJSONObject("hints").getDouble("visited_nodes.average")
        )

          return graphhopper_response(info = infoObj, paths = paths, hints = hints)
    }


private fun decodePolyline(encoded: String): List<GeoPoint> {
        val poly = PolyUtil.decode(encoded)
        return poly.map { GeoPoint(it.latitude, it.longitude) }
    }
}

// LocationDataStore.kt
class LocationDataStore(private val context: Context) {
    private val LATITUDE_KEY = doublePreferencesKey("latitude")
    private val LONGITUDE_KEY = doublePreferencesKey("longitude")

    suspend fun saveLocation(latitude: Double, longitude: Double) {
        context.dataStore.edit { prefs ->
            prefs[LATITUDE_KEY] = latitude
            prefs[LONGITUDE_KEY] = longitude
        }
    }

    suspend fun getLocation(): Pair<Double, Double>? {
        val prefs = context.dataStore.data.first()
        val lat = prefs[LATITUDE_KEY] ?: return null
        val lng = prefs[LONGITUDE_KEY] ?: return null
        return Pair(lat, lng)
    }

    suspend fun clearLocation() {
        context.dataStore.edit { it.clear() }
    }
}

// Extension functions
fun LatLng.toDomainLocation(): Location {
    return Location(latitude, longitude)
}

suspend fun fetchGraphHopperSuggestions(
    query: String,
    apiKey: String,
    onResult: (List<String>) -> Unit
) {
    withContext(Dispatchers.IO) {
        try {
            val encodedQuery = URLEncoder.encode(query, "UTF-8")
            val url =
                "https://graphhopper.com/api/1/geocode?q=$encodedQuery&locale=en&limit=5&key=c69abe50-60d2-43bc-82b1-81cbdcebeddc"

            val connection = URL(url).openConnection() as HttpURLConnection
            connection.requestMethod = "GET"

            val response = connection.inputStream.bufferedReader().use { it.readText() }
            Log.d("GraphHopper", "Response: $response")

            val jsonObject = JSONObject(response)
            val hits = jsonObject.getJSONArray("hits")

            val suggestions = mutableListOf<String>()
            for (i in 0 until hits.length()) {
                val hit = hits.getJSONObject(i)
                suggestions.add(hit.getString("name"))
            }

            withContext(Dispatchers.Main) {
                onResult(suggestions)
            }
        } catch (e: Exception) {
            Log.e("GraphHopper", "Error fetching suggestions", e)
            withContext(Dispatchers.Main) {
                onResult(emptyList())
            }
        }
    }
}

@Composable
fun EmergencyChatSheet(
    driverId: String,
    driverName: String,
    passengerId: String,
    passengerName: String,
    tripNumber: String,
    tripFrom: String,
    tripTo: String,
    passengerImage: String = "",
    onClose: () -> Unit
) {
    val db = FirebaseFirestore.getInstance()
    var newMessage by remember { mutableStateOf("") }
    val messages = remember { mutableStateListOf<ChatMessage>() }
    val context = LocalContext.current

    // Step 1: Get or Create emergency_message document
    var messageId by remember { mutableStateOf<String?>(null) }

    // Create message document if not already created
    LaunchedEffect(Unit) {
        val query = db.collection("emergency_messages")
            .whereEqualTo("tripNumber", tripNumber)
            .whereEqualTo("passengerId", passengerId)
            .whereEqualTo("driverId", driverId)
            .limit(1)
            .get()
            .await()

        if (!query.isEmpty) {
            messageId = query.documents[0].id
        } else {
            val newDoc = hashMapOf(
                "driverId" to driverId,
                "driverName" to driverName,
                "passengerId" to passengerId,
                "passengerName" to passengerName,
                "passengerImage" to passengerImage,
                "tripNumber" to tripNumber,
                "tripFrom" to tripFrom,
                "tripTo" to tripTo,
                "message" to "", // First message will be added in chat
                "status" to "new",
                "isRead" to false,
                "isResolved" to false,
                "timestamp" to FieldValue.serverTimestamp()
            )
            val docRef = db.collection("emergency_messages").add(newDoc).await()
            messageId = docRef.id
        }
    }

    // Step 2: Listen to chat updates
    LaunchedEffect(messageId) {
        if (messageId != null) {
            db.collection("emergency_messages")
                .document(messageId!!)
                .collection("chat")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener { snapshot, _ ->
                    if (snapshot != null) {
                        messages.clear()
                        messages.addAll(snapshot.documents.mapNotNull { it.toObject(ChatMessage::class.java) })
                    }
                }
        }
    }

    Column(
        Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text("شات الطوارئ", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            reverseLayout = false // ده مهم جدًا
        ) {
            items(messages.sortedBy { it.timestamp?.seconds ?: 0 }) { msg ->
                val isMe = msg.senderId == passengerId

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
                ) {
                    Column(
                        modifier = Modifier
                            .background(
                                color = if (isMe) Color(0xFF2196F3) else Color(0xFFE0E0E0),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .padding(12.dp)
                            .widthIn(max = 250.dp)
                    ) {
                        Text(
                            text = msg.message,
                            color = if (isMe) Color.White else Color.Black
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = formatTimestamp(msg.timestamp),
                            fontSize = 10.sp,
                            color = if (isMe) Color.White.copy(alpha = 0.7f) else Color.Gray,
                            modifier = Modifier.align(Alignment.End)
                        )
                    }
                }
            }
        }


        Spacer(modifier = Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = newMessage,
                onValueChange = { newMessage = it },
                label = { Text("اكتب رسالتك") },
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {
                    if (newMessage.isNotBlank() && messageId != null) {
                        val chatMessage = hashMapOf(
                            "message" to newMessage,
                            "senderId" to passengerId,
                            "senderName" to passengerName,
                            "isRead" to false,
                            "timestamp" to FieldValue.serverTimestamp()
                        )
                        db.collection("emergency_messages")
                            .document(messageId!!)
                            .collection("chat")
                            .add(chatMessage)
                        newMessage = ""
                    }
                }
            ) {
                Text("إرسال")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onClose,
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("إغلاق")
        }
    }
}
fun formatTimestamp(timestamp: Timestamp?): String {
    return timestamp?.toDate()?.let {
        SimpleDateFormat("hh:mm a", Locale.getDefault()).format(it)
    } ?: ""
}

data class ChatMessage(
    val message: String = "",
    val senderId: String = "",
    val senderName: String = "",
    val isRead: Boolean = false,
    val timestamp: Timestamp = Timestamp.now()
)




fun sendEmergencyMessage(
    passengerName: String,
    passengerId: String,
    driverName: String,
    driverId: String,
    tripNumber: String,
    tripFrom: String,
    tripTo: String,
    message: String,
    passengerImage: String = "",
    onSuccess: () -> Unit = {},
    onError: (Exception) -> Unit = {}
) {
    val firestore = FirebaseFirestore.getInstance()

    val data = hashMapOf(
        "passengerName" to passengerName,
        "passengerId" to passengerId,
        "driverName" to driverName,
        "driverId" to driverId,
        "tripNumber" to tripNumber,
        "tripFrom" to tripFrom,
        "tripTo" to tripTo,
        "message" to message,
        "timestamp" to FieldValue.serverTimestamp(),
        "status" to "new", // مثلاً new, in_progress, resolved
        "isRead" to false,
        "isResolved" to false,
        "passengerImage" to passengerImage
    )

    firestore.collection("emergency_messages")
        .add(data)
        .addOnSuccessListener {
            Log.d("Firestore", "✅ Emergency message sent")
            onSuccess()
        }
        .addOnFailureListener { e ->
            Log.e("Firestore", "❌ Failed to send emergency message", e)
            onError(e)
        }
}
