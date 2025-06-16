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
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import com.google.maps.android.PolyUtil
import findNearestIndex
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.util.Locale
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin

private val Context.dataStore by preferencesDataStore(name = "location_prefs")

@OptIn(ExperimentalMaterialApi::class)
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
    val realStreetPath = listOf(
        GeoPoint(29.983390, 31.282690),
        GeoPoint(29.984770, 31.282180),
        GeoPoint(29.984310, 31.280370),
        GeoPoint(29.984040, 31.279310),
        GeoPoint(29.983680, 31.277840),
        GeoPoint(29.983320, 31.276420),
        GeoPoint(29.983210, 31.275990),
        GeoPoint(29.982750, 31.274040),
        GeoPoint(29.983540, 31.274050),
        GeoPoint(29.983950, 31.274070),
        GeoPoint(29.984060, 31.274070),
        GeoPoint(29.984280, 31.274020),
        GeoPoint(29.984390, 31.273990),
        GeoPoint(29.984900, 31.273760),
        GeoPoint(29.985220, 31.273670),
        GeoPoint(29.986440, 31.273100),
        GeoPoint(29.986710, 31.272960),
        GeoPoint(29.987210, 31.272690),
        GeoPoint(29.987390, 31.272560),
        GeoPoint(29.987410, 31.272520),
        GeoPoint(29.987760, 31.272290),
        GeoPoint(29.988240, 31.272000),
        GeoPoint(29.988290, 31.271920),
        GeoPoint(29.988320, 31.271890),
        GeoPoint(29.988390, 31.271840),
        GeoPoint(29.988470, 31.271810),
        GeoPoint(29.989750, 31.271680),
        GeoPoint(29.989820, 31.271690),
        GeoPoint(29.989890, 31.271720),
        GeoPoint(29.989950, 31.271750),
        GeoPoint(29.990010, 31.271800),
        GeoPoint(29.990060, 31.271850),
        GeoPoint(29.990100, 31.271920),
        GeoPoint(29.990130, 31.271990),
        GeoPoint(29.990210, 31.272230),
        GeoPoint(29.990200, 31.272580),
        GeoPoint(29.990210, 31.273350),
        GeoPoint(29.990240, 31.274120),
        GeoPoint(29.990290, 31.274880),
        GeoPoint(29.990450, 31.276260),
        GeoPoint(29.990670, 31.277820),
        GeoPoint(29.990850, 31.279180),
        GeoPoint(29.991100, 31.281120),
        GeoPoint(29.991240, 31.282250),
        GeoPoint(29.991290, 31.282680),
        GeoPoint(29.991350, 31.283010),
        GeoPoint(29.991420, 31.283330),
        GeoPoint(29.991500, 31.283640),
        GeoPoint(29.991840, 31.284840),
        GeoPoint(29.992350, 31.286680),
        GeoPoint(29.992430, 31.287080),
        GeoPoint(29.992470, 31.287290),
        GeoPoint(29.992520, 31.287720),
        GeoPoint(29.992560, 31.288140),
        GeoPoint(29.992570, 31.288770),
        GeoPoint(29.992560, 31.289160),
        GeoPoint(29.992530, 31.289540),
        GeoPoint(29.992480, 31.289920),
        GeoPoint(29.992420, 31.290290),
        GeoPoint(29.992350, 31.290660),
        GeoPoint(29.992250, 31.291020),
        GeoPoint(29.992140, 31.291380),
        GeoPoint(29.991640, 31.292890),
        GeoPoint(29.991140, 31.294160),
        GeoPoint(29.990800, 31.294970),
        GeoPoint(29.989480, 31.298280),
        GeoPoint(29.989130, 31.299370),
        GeoPoint(29.988810, 31.300350),
        GeoPoint(29.988280, 31.302000),
        GeoPoint(29.988020, 31.302700),
        GeoPoint(29.987880, 31.303190),
        GeoPoint(29.987840, 31.303420),
        GeoPoint(29.987800, 31.303760),
        GeoPoint(29.987700, 31.304290),
        GeoPoint(29.987570, 31.304800),
        GeoPoint(29.987420, 31.305310),
        GeoPoint(29.987320, 31.305780),
        GeoPoint(29.987230, 31.306250),
        GeoPoint(29.987150, 31.306720),
        GeoPoint(29.987120, 31.307030),
        GeoPoint(29.987070, 31.307330),
        GeoPoint(29.987000, 31.307630),
        GeoPoint(29.986800, 31.308330),
        GeoPoint(29.986730, 31.308490),
        GeoPoint(29.986460, 31.309230),
        GeoPoint(29.985910, 31.310800),
        GeoPoint(29.985860, 31.310970),
        GeoPoint(29.985790, 31.311310),
        GeoPoint(29.985270, 31.312750),
        GeoPoint(29.984540, 31.314760),
        GeoPoint(29.984430, 31.315090),
        GeoPoint(29.984310, 31.315520),
        GeoPoint(29.984180, 31.316070),
        GeoPoint(29.984110, 31.316490),
        GeoPoint(29.984060, 31.316980),
        GeoPoint(29.984020, 31.317500),
        GeoPoint(29.984000, 31.318200),
        GeoPoint(29.984010, 31.318380),
        GeoPoint(29.984040, 31.318780),
        GeoPoint(29.984190, 31.320100),
        GeoPoint(29.984230, 31.321060),
        GeoPoint(29.984190, 31.321750),
        GeoPoint(29.984070, 31.322640),
        GeoPoint(29.983980, 31.323190),
        GeoPoint(29.983710, 31.324640),
        GeoPoint(29.983490, 31.325990),
        GeoPoint(29.983310, 31.327250),
        GeoPoint(29.983190, 31.328450),
        GeoPoint(29.982920, 31.330530),
        GeoPoint(29.982630, 31.332830),
        GeoPoint(29.982320, 31.335450),
        GeoPoint(29.982130, 31.336900),
        GeoPoint(29.981890, 31.338930),
        GeoPoint(29.981630, 31.341070),
        GeoPoint(29.981540, 31.341280),
        GeoPoint(29.981470, 31.341490),
        GeoPoint(29.981260, 31.342200),
        GeoPoint(29.981140, 31.342580),
        GeoPoint(29.981050, 31.343280),
        GeoPoint(29.980960, 31.343950),
        GeoPoint(29.980840, 31.344590),
        GeoPoint(29.980740, 31.345240),
        GeoPoint(29.980650, 31.346010),
        GeoPoint(29.980630, 31.346460),
        GeoPoint(29.980630, 31.346860),
        GeoPoint(29.980640, 31.347300),
        GeoPoint(29.980620, 31.347740),
        GeoPoint(29.980520, 31.348370),
        GeoPoint(29.980390, 31.348970),
        GeoPoint(29.980290, 31.349380),
        GeoPoint(29.979910, 31.350620),
        GeoPoint(29.979730, 31.351150),
        GeoPoint(29.979090, 31.353120),
        GeoPoint(29.978970, 31.353270),
        GeoPoint(29.978850, 31.353410),
        GeoPoint(29.978720, 31.353530),
        GeoPoint(29.978410, 31.353780),
        GeoPoint(29.978250, 31.353930),
        GeoPoint(29.978140, 31.354090),
        GeoPoint(29.978090, 31.354210),
        GeoPoint(29.978020, 31.354470),
        GeoPoint(29.978000, 31.354620),
        GeoPoint(29.977990, 31.355150),
        GeoPoint(29.977950, 31.355610),
        GeoPoint(29.977890, 31.356320),
        GeoPoint(29.977830, 31.356910),
        GeoPoint(29.977710, 31.357800),
        GeoPoint(29.977440, 31.359100),
        GeoPoint(29.977310, 31.359630),
        GeoPoint(29.977110, 31.360090),
        GeoPoint(29.977040, 31.360240),
        GeoPoint(29.976870, 31.360520),
        GeoPoint(29.976780, 31.360650),
        GeoPoint(29.976500, 31.360940),
        GeoPoint(29.975330, 31.361940),
        GeoPoint(29.974330, 31.362860),
        GeoPoint(29.973930, 31.363260),
        GeoPoint(29.973470, 31.363750),
        GeoPoint(29.972730, 31.364670),
        GeoPoint(29.971060, 31.367120),
        GeoPoint(29.969250, 31.369560),
        GeoPoint(29.969060, 31.369830),
        GeoPoint(29.968880, 31.370110),
        GeoPoint(29.968700, 31.370390),
        GeoPoint(29.968540, 31.370690),
        GeoPoint(29.968380, 31.370990),
        GeoPoint(29.968240, 31.371300),
        GeoPoint(29.968100, 31.371610),
        GeoPoint(29.967850, 31.372220),
        GeoPoint(29.967740, 31.372520),
        GeoPoint(29.967520, 31.373150),
        GeoPoint(29.967420, 31.373490),
        GeoPoint(29.967330, 31.373840),
        GeoPoint(29.967160, 31.374540),
        GeoPoint(29.967040, 31.375220),
        GeoPoint(29.966970, 31.375910),
        GeoPoint(29.966920, 31.376590),
        GeoPoint(29.966910, 31.377270),
        GeoPoint(29.966930, 31.384360),
        GeoPoint(29.966910, 31.386670),
        GeoPoint(29.966800, 31.387640),
        GeoPoint(29.966680, 31.388590),
        GeoPoint(29.966330, 31.390450),
        GeoPoint(29.965870, 31.392610),
        GeoPoint(29.965770, 31.393280),
        GeoPoint(29.965700, 31.393960),
        GeoPoint(29.965590, 31.398980),
        GeoPoint(29.965530, 31.400300),
        GeoPoint(29.965430, 31.401490),
        GeoPoint(29.965240, 31.403320),
        GeoPoint(29.965110, 31.404060),
        GeoPoint(29.964950, 31.404920),
        GeoPoint(29.964770, 31.405740),
        GeoPoint(29.964490, 31.406850),
        GeoPoint(29.964110, 31.408230),
        GeoPoint(29.963920, 31.408930),
        GeoPoint(29.963280, 31.411080),
        GeoPoint(29.962670, 31.413320),
        GeoPoint(29.961830, 31.416250),
        GeoPoint(29.961080, 31.418890),
        GeoPoint(29.960780, 31.419860),
        GeoPoint(29.960480, 31.420860),
        GeoPoint(29.960030, 31.422160),
        GeoPoint(29.959590, 31.423460),
        GeoPoint(29.958970, 31.425200),
        GeoPoint(29.958330, 31.427010),
        GeoPoint(29.957500, 31.429300),
        GeoPoint(29.956820, 31.431280),
        GeoPoint(29.956430, 31.432540),
        GeoPoint(29.956240, 31.433220),
        GeoPoint(29.956010, 31.434220),
        GeoPoint(29.955700, 31.435760),
        GeoPoint(29.955640, 31.436170),
        GeoPoint(29.955530, 31.436850),
        GeoPoint(29.955370, 31.437990),
        GeoPoint(29.955160, 31.439250),
        GeoPoint(29.955070, 31.439950),
        GeoPoint(29.955010, 31.440460),
        GeoPoint(29.954980, 31.440900),
        GeoPoint(29.954970, 31.441760),
        GeoPoint(29.954980, 31.442290),
        GeoPoint(29.955010, 31.442800),
        GeoPoint(29.955100, 31.443610),
        GeoPoint(29.955220, 31.444370),
        GeoPoint(29.955390, 31.445120),
        GeoPoint(29.955600, 31.445860),
        GeoPoint(29.955850, 31.446580),
        GeoPoint(29.956130, 31.447270),
        GeoPoint(29.956300, 31.447620),
        GeoPoint(29.956460, 31.447970),
        GeoPoint(29.957030, 31.448930),
        GeoPoint(29.958020, 31.450400),
        GeoPoint(29.959360, 31.452450),
        GeoPoint(29.960230, 31.453720),
        GeoPoint(29.961570, 31.455780),
        GeoPoint(29.963560, 31.458780),
        GeoPoint(29.963960, 31.459410),
        GeoPoint(29.964290, 31.459930),
        GeoPoint(29.964970, 31.461070),
        GeoPoint(29.965510, 31.462140),
        GeoPoint(29.966020, 31.463470),
        GeoPoint(29.966260, 31.464240),
        GeoPoint(29.966420, 31.464830),
        GeoPoint(29.966580, 31.465590),
        GeoPoint(29.966680, 31.466180),
        GeoPoint(29.966760, 31.466770),
        GeoPoint(29.966820, 31.467360),
        GeoPoint(29.966850, 31.467960),
        GeoPoint(29.966860, 31.468550),
        GeoPoint(29.966860, 31.469150),
        GeoPoint(29.966810, 31.470070),
        GeoPoint(29.966710, 31.470980),
        GeoPoint(29.966640, 31.471460),
        GeoPoint(29.966550, 31.471930),
        GeoPoint(29.966440, 31.472390),
        GeoPoint(29.966300, 31.472960),
        GeoPoint(29.966130, 31.473510),
        GeoPoint(29.965940, 31.474060),
        GeoPoint(29.965670, 31.474810),
        GeoPoint(29.965440, 31.475350),
        GeoPoint(29.965190, 31.475880),
        GeoPoint(29.964930, 31.476400),
        GeoPoint(29.964590, 31.477010),
        GeoPoint(29.964240, 31.477530),
        GeoPoint(29.964020, 31.477820),
        GeoPoint(29.963850, 31.478060),
        GeoPoint(29.963400, 31.478590),
        GeoPoint(29.962450, 31.479770),
        GeoPoint(29.961570, 31.480960),
        GeoPoint(29.961060, 31.481680),
        GeoPoint(29.960500, 31.482410),
        GeoPoint(29.958850, 31.484470),
        GeoPoint(29.955920, 31.488250),
        GeoPoint(29.953920, 31.490850),
        GeoPoint(29.952540, 31.492780),
        GeoPoint(29.947860, 31.499920),
        GeoPoint(29.945530, 31.503410),
        GeoPoint(29.941800, 31.509120),
        GeoPoint(29.940310, 31.511430),
        GeoPoint(29.938180, 31.514750),
        GeoPoint(29.936440, 31.517430),
        GeoPoint(29.935270, 31.519270),
        GeoPoint(29.934940, 31.519550),
        GeoPoint(29.934760, 31.519710),
        GeoPoint(29.934560, 31.519920),
        GeoPoint(29.934340, 31.520200),
        GeoPoint(29.934040, 31.520640),
        GeoPoint(29.933730, 31.521160),
        GeoPoint(29.933500, 31.521600),
        GeoPoint(29.933230, 31.522180),
        GeoPoint(29.933040, 31.522670),
        GeoPoint(29.932890, 31.523150),
        GeoPoint(29.932760, 31.523560),
        GeoPoint(29.931450, 31.528520),
        GeoPoint(29.931230, 31.528820),
        GeoPoint(29.931060, 31.528980),
        GeoPoint(29.930930, 31.529060),
        GeoPoint(29.930730, 31.529130),
        GeoPoint(29.930520, 31.529140),
        GeoPoint(29.930290, 31.529090),
        GeoPoint(29.930140, 31.529020),
        GeoPoint(29.929970, 31.528870),
        GeoPoint(29.929830, 31.528690),
        GeoPoint(29.929720, 31.528540),
        GeoPoint(29.929640, 31.528360),
        GeoPoint(29.929590, 31.528150),
        GeoPoint(29.929580, 31.527870),
        GeoPoint(29.929610, 31.527640),
        GeoPoint(29.929700, 31.527420),
        GeoPoint(29.929870, 31.527200),
        GeoPoint(29.930000, 31.527090),
        GeoPoint(29.930140, 31.527020),
        GeoPoint(29.930300, 31.526960),
        GeoPoint(29.930410, 31.526940),
        GeoPoint(29.930780, 31.526960),
        GeoPoint(29.931350, 31.527170),
        GeoPoint(29.934030, 31.528090),
        GeoPoint(29.936190, 31.528790),
        GeoPoint(29.937360, 31.529220),
        GeoPoint(29.937560, 31.529310),
        GeoPoint(29.937740, 31.529410),
        GeoPoint(29.938080, 31.529610),
        GeoPoint(29.938230, 31.529700),
        GeoPoint(29.942380, 31.532690),
        GeoPoint(29.949980, 31.538230),
        GeoPoint(29.950990, 31.538970),
        GeoPoint(29.951770, 31.539530),
        GeoPoint(29.952740, 31.539870),
        GeoPoint(29.954530, 31.541160),
        GeoPoint(29.955640, 31.541980),
        GeoPoint(29.960020, 31.545200),
        GeoPoint(29.961770, 31.546470),
        GeoPoint(29.962680, 31.547120),
        GeoPoint(29.963720, 31.547830),
        GeoPoint(29.964570, 31.548290),
        GeoPoint(29.964920, 31.548490),
        GeoPoint(29.965260, 31.548710),
        GeoPoint(29.965580, 31.548970),
        GeoPoint(29.965890, 31.549250),
        GeoPoint(29.966180, 31.549540),
        GeoPoint(29.966450, 31.549800),
        GeoPoint(29.967010, 31.550280),
        GeoPoint(29.967850, 31.550890),
        GeoPoint(29.968770, 31.551500),
        GeoPoint(29.970070, 31.552460),
        GeoPoint(29.982080, 31.561180),
        GeoPoint(29.985870, 31.564010),
        GeoPoint(29.988130, 31.565560),
        GeoPoint(29.988750, 31.566030),
        GeoPoint(29.989810, 31.566790),
        GeoPoint(29.990970, 31.567620),
        GeoPoint(29.991740, 31.568200),
        GeoPoint(29.993300, 31.569310),
        GeoPoint(29.997850, 31.572630),
        GeoPoint(29.999340, 31.573700),
        GeoPoint(30.004370, 31.577370),
        GeoPoint(30.005630, 31.578300),
        GeoPoint(30.007670, 31.579790),
        GeoPoint(30.008190, 31.580150),
        GeoPoint(30.008670, 31.580530),
        GeoPoint(30.009470, 31.581120),
        GeoPoint(30.009900, 31.581480),
        GeoPoint(30.010840, 31.582420),
        GeoPoint(30.012140, 31.583890),
        GeoPoint(30.012880, 31.585010),
        GeoPoint(30.013860, 31.586350),
        GeoPoint(30.015490, 31.588620),
        GeoPoint(30.018040, 31.592180),
        GeoPoint(30.018580, 31.592890),
        GeoPoint(30.019190, 31.593650),
        GeoPoint(30.019850, 31.594400),
        GeoPoint(30.020600, 31.595150),
        GeoPoint(30.021000, 31.595530),
        GeoPoint(30.021490, 31.595950),
        GeoPoint(30.022140, 31.596470),
        GeoPoint(30.022430, 31.596690),
        GeoPoint(30.023030, 31.597110),
        GeoPoint(30.023560, 31.597450),
        GeoPoint(30.024260, 31.597870),
        GeoPoint(30.025070, 31.598300),
        GeoPoint(30.025730, 31.598610),
        GeoPoint(30.026100, 31.598770),
        GeoPoint(30.026470, 31.598920),
        GeoPoint(30.027240, 31.599190),
        GeoPoint(30.027940, 31.599400),
        GeoPoint(30.028250, 31.599490),
        GeoPoint(30.028870, 31.599630),
        GeoPoint(30.029830, 31.599830),
        GeoPoint(30.031110, 31.600040),
        GeoPoint(30.033650, 31.600480),
        GeoPoint(30.034230, 31.600600),
        GeoPoint(30.034780, 31.600750),
        GeoPoint(30.035230, 31.600910),
        GeoPoint(30.036180, 31.601530),
        GeoPoint(30.036400, 31.601630),
        GeoPoint(30.037980, 31.602460),
        GeoPoint(30.038460, 31.602750),
        GeoPoint(30.040320, 31.604010),
        GeoPoint(30.041310, 31.604630),
        GeoPoint(30.041460, 31.604840),
        GeoPoint(30.041590, 31.605090),
        GeoPoint(30.041690, 31.605350),
        GeoPoint(30.041770, 31.605580),
        GeoPoint(30.042150, 31.607560),
        GeoPoint(30.042190, 31.607670),
        GeoPoint(30.042220, 31.607710),
        GeoPoint(30.042250, 31.607750),
        GeoPoint(30.042390, 31.607890),
        GeoPoint(30.042420, 31.607960),
        GeoPoint(30.042500, 31.608090),
        GeoPoint(30.042660, 31.608560),
        GeoPoint(30.042770, 31.608850),
        GeoPoint(30.043140, 31.609620),
        GeoPoint(30.043570, 31.610620),
        GeoPoint(30.043990, 31.611760),
        GeoPoint(30.044180, 31.612350),
        GeoPoint(30.044340, 31.612950),
        GeoPoint(30.044510, 31.613710),
        GeoPoint(30.044690, 31.614720),
        GeoPoint(30.044760, 31.615210),
        GeoPoint(30.044800, 31.615710),
        GeoPoint(30.044820, 31.616200),
        GeoPoint(30.044800, 31.619110),
        GeoPoint(30.044790, 31.623770),
        GeoPoint(30.044790, 31.626710),
        GeoPoint(30.044760, 31.629490),
        GeoPoint(30.044760, 31.629930),
        GeoPoint(30.044790, 31.631000),
        GeoPoint(30.044790, 31.633520),
        GeoPoint(30.044720, 31.638350),
        GeoPoint(30.044740, 31.641650),
        GeoPoint(30.044720, 31.645700),
        GeoPoint(30.044680, 31.652470),
        GeoPoint(30.044620, 31.661110),
        GeoPoint(30.044580, 31.665950),
        GeoPoint(30.044520, 31.676190),
        GeoPoint(30.044510, 31.679840),
        GeoPoint(30.044490, 31.680930),
        GeoPoint(30.044410, 31.682670),
        GeoPoint(30.044340, 31.683180),
        GeoPoint(30.044220, 31.684130),
        GeoPoint(30.044170, 31.684770),
        GeoPoint(30.044120, 31.685480),
        GeoPoint(30.044120, 31.686970),
        GeoPoint(30.043820, 31.686980),
        GeoPoint(30.043810, 31.688890),
        GeoPoint(30.043800, 31.690300),
        GeoPoint(30.043830, 31.691130),
        GeoPoint(30.044020, 31.692380),
        GeoPoint(30.044080, 31.692900),
        GeoPoint(30.044090, 31.693110),
        GeoPoint(30.044100, 31.694080),
        GeoPoint(30.044070, 31.697510),
        GeoPoint(30.043990, 31.697510),
    )

    var index by remember { mutableStateOf(0) }
    var current by remember { mutableStateOf<GeoPoint?>(null) }
    var previous by remember { mutableStateOf<GeoPoint?>(null) }

    LaunchedEffect(Unit) {
        while (true) {
            if (index < realStreetPath.size - 1) {
                previous = realStreetPath[index]
                current = realStreetPath[index + 1]
                index++
            } else {
                index = 0
            }
            delay(2000L) // كل خطوة 1.5 ثانية
        }
    }

    var showBottomSheet by remember { mutableStateOf(false) }

    PartialBottomSheet(
        showBottomSheet = showBottomSheet,
        onDismissRequest = { showBottomSheet = false }) {
        PaymentMethodContent()
    }
    var driverLocationState by remember { mutableStateOf<LatLng?>(null) }
    // Main Container


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
                                RideInProgressScreen(
                                    startLocation = originString?:"",
                                    endLocation = destinationString?:"مطار القاهرة الدولي",
                                    estimatedTime = formattedTime?: "30 دقيقة",

                                    onEmergencyClick = {
                                        // هنا ترسل alert للطوارئ أو Firebase
                                        Log.d("EMERGENCY", "🚨 تم الضغط على زر الطوارئ!")
                                        // تقدر تبعت location أو تعمل أي logic إضافي
                                    }
                                )

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
                FindDriverCard(onclick = {



                    if (

                        storedPoints == null  ||startPoint.value == null || endPoint.value == null||tripStatus!="pending") {
                        Toast.makeText(
                            context,
                            "Please select both pickup and drop-off locations",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@FindDriverCard
                    }
                    if (tripStatus=="pending") {

                    Log.d("TripScreen", "FindDriverCard clicked")
                    isSearch=true
                    stateTripViewModel.searchDriver()
                    val sharedPreferences =
                        context.getSharedPreferences("your_prefs", Context.MODE_PRIVATE)
                    val userId = sharedPreferences.getString("USER_ID", null)
                    val userBalance = sharedPreferences.getFloat("USER_BALANCE", 0f)

                    if (userId == null) {
                        Toast.makeText(context, "User not logged in", Toast.LENGTH_SHORT).show()

                    }
                    // Use the current location as the origin
                    val origin = getAddressFromLatLng(
                        context,

                        latitude = startPoint.value?.latitude ?: 0.0,
                        longitude = startPoint.value?.longitude ?: 0.0
                    )

                    // Use the selected destination as the destination
                    val destination =
                        getAddressFromLatLng(context,
                            latitude = endPoint.value?.latitude ?: 0.0,
                            longitude = endPoint.value?.longitude ?: 0.0

                        )
                    val fare = fare2
                    val distanceInKm = distance
                    val paymentMethod = "cash"
                    val apiKey =
                        "c69abe50-60d2-43bc-82b1-81cbdcebeddc" // Replace with your actual API key

                    Log.d(
                        "TripScreen",
                        "Requesting trip with origin: $origin, destination: $destination, fare: $fare, distance: $distanceInKm"
                    )


                    if (Savedtoken != null) {
                        if (distanceInKm != null) {
                            tripViewModel.createTrip(
                                context = context,
                                userId!!,
                                origin,
                                destination,
                                paymentMethod,
                                fare!!,
                                distanceInKm,
                                Savedtoken,
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
                    }


                    // Set the endpoint when the button is clicked
                    endPoint.value = GeoPoint(destinationLat, destinationLng)

}
                })
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