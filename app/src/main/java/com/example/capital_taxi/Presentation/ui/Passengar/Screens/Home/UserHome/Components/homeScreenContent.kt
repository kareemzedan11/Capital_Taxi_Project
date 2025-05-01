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
import com.example.capital_taxi.Navigation.Destination
import com.example.capital_taxi.Presentation.ui.Driver.Components.InProgressMap
import com.example.capital_taxi.Presentation.ui.Driver.Screens.Home.Components.Home_Components.TripViewModel2
import com.example.capital_taxi.Presentation.ui.Driver.Screens.Home.Components.Home_Components.getAddressFromLatLng
import com.example.capital_taxi.Presentation.ui.Driver.Screens.Home.Components.updateTripStatus
import com.example.capital_taxi.Presentation.ui.Passengar.Components.StateTripViewModel
import com.example.capital_taxi.Presentation.ui.Passengar.Components.fetchDriverInfoWithRetry
import com.example.capital_taxi.Presentation.ui.Passengar.Components.waitForDriverIdFromTrip
import com.example.capital_taxi.Presentation.ui.Passengar.Screens.Home.UserHome.Components.During_the_trip.DriverArrivalCard
import com.example.capital_taxi.Presentation.ui.Passengar.Screens.Home.UserHome.Components.During_the_trip.RideInProgressScreen
import com.example.capital_taxi.Presentation.ui.Passengar.Screens.Home.UserHome.Components.Trip_Rating.TripCompletedScreen
import com.example.capital_taxi.data.repository.graphhopper_response.Details
import com.example.capital_taxi.data.repository.graphhopper_response.Hints
import com.example.capital_taxi.data.repository.graphhopper_response.Info
import com.example.capital_taxi.data.repository.graphhopper_response.Instruction
import com.example.capital_taxi.data.repository.graphhopper_response.Path
import com.example.capital_taxi.data.repository.graphhopper_response.graphhopper_response
import com.example.capital_taxi.domain.shared.TripInfoViewModel
import com.example.capital_taxi.domain.storedPoints
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

    val locationViewModel: LocationViewModel = viewModel()
    val pickupLatLng = locationViewModel.pickupLocation
    val dropoffLatLng = locationViewModel.dropoffLocation

    val fareViewModel: FareViewModel = viewModel()
    val fare = fareViewModel.fare  // لا حاجة لـ observeAsState

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
                                // Update the toast message to English
                                Toast.makeText(context, "Trip cancelled by driver", Toast.LENGTH_LONG).show()
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

                if (originLat != null && originLng != null && destinationLat != null && destinationLng != null) {
                    passengerLocation2 = GeoPoint(originLat, originLng)
                    driverLocation2 = GeoPoint(destinationLat, destinationLng)

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



    val directions2 = remember { mutableStateListOf<GeoPoint>() }

    // متغير لتخزين الموقع
    // قيمة مبدئية
    var isDataLoading2 by remember { mutableStateOf(true) }  // حالة التحميل
    var driverLocation3 by remember {
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
            val querySnapshot = db.collection("trips")
                .whereEqualTo("_id", tripId)
                .get()
                .await()

            if (!querySnapshot.isEmpty) {
                val document = querySnapshot.documents.first()

                // جلب بيانات originMap
                val originMap = document.get("originMap") as? Map<String, Any>
                val originLat = originMap?.get("lat") as? Double
                val originLng = originMap?.get("lng") as? Double

                // جلب بيانات destinationMap
                val destinationMap =
                    document.get("destinationMap") as? Map<String, Any>
                val destinationLat = destinationMap?.get("lat") as? Double
                val destinationLng = destinationMap?.get("lng") as? Double

                if (originLat != null && originLng != null && destinationLat != null && destinationLng != null) {
                    passengerLocation2 = GeoPoint(originLat, originLng)
                    driverLocation3 = GeoPoint(destinationLat, destinationLng)

                    val result = DirectionsApi.getDirections(
                        start = passengerLocation2,
                        end = driverLocation3,
                        apiKey = "c69abe50-60d2-43bc-82b1-81cbdcebeddc",
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

                                directions2.clear()
                                directions2.addAll(decodedPoints)
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
// إعادة تحميل الخريطة باستخدام `key`

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
                // Open rating dialog or bottom sheet
                navController.navigate("RateDriver/${tripId}")
            },
            onReturnHomeClick = {
                stateTripViewModel.resetAll()
                navController.navigate(Destination.UserHomeScreen.route) {
                    popUpTo(Destination.UserHomeScreen.route) { inclusive = true }
                }
            },
            onReceiptClick = {
                // Show detailed receipt
                navController.navigate("TripReceipt/${tripId}")
            },
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
        )

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
                            }

                        }


                        if (state.inProgress||state.isStart) {
                            isTripBegin=true
                            isstart = true
                            InProgressMap(
                                directions = directions2,
                                driverLocation = passengerLocation2,
                                Destination = driverLocation3
                            )
                        }
                        if (state.isCancelled) {
                            storedPoints = null
                            MapViewComposable(
                                startPoint = startPoint.value,
                                endPoint = endPoint.value
                            )
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


                                DraggableIcon(navController = navController)

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
                                            fetchDriverInfoWithRetry(
                                                driverId,
                                                onSuccess = { name, car ->
                                                    driverName = name
                                                    carType = car
                                                    Log.d("DriverData", "🚗 الاسم: $driverName - النوع: $carType")
                                                },
                                                maxRetries = 5 // عدد المحاولات لجلب driverId (تقدر تزوده لو حابب)
                                            )
                                        },
                                        maxRetries = 5 // عدد المحاولات لجلب driverId
                                    )
                                }


                                RideDetailsBottomSheetContent(
                                    onclick = {  stateTripViewModel.setCancelled()},
                                    navController, tripId!!)
                                LaunchedEffect(tripStatus) {
                                    if (tripStatus == "InProgress") {

                                        stateTripViewModel.updateTripStatus("InProgress")
                                    }
                                }

                            }
                            state.isConfirmed -> {
                                isSearch=true
                                confirmPickup(onclick = {
                                    isConfirmed=false
                                    stateTripViewModel.searchDriver()  })

                            }
                            state.isSearch -> {
                                searchAboutADriver()
                                LaunchedEffect(tripStatus) {
                                    if (tripStatus == "accepted") {
                                        stateTripViewModel.updateTripStatus("accepted")
                                    }
                                }
                            }

                            state.isCancelled ->{

                                    AlertDialog(
                                        onDismissRequest = {
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
            if (currentIsLocationEnabled.value && currentIsLocationGranted.value && !isConfirmed && !isSearch&&!isstart&&!isTripBegin&&state.isInitialPickup) {
                val Savedtoken =
                    token // Fetch or pass the token
                FindDriverCard(onclick = {



                    if (startPoint.value == null || endPoint.value == null) {
                        Toast.makeText(
                            context,
                            "Please select both pickup and drop-off locations",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@FindDriverCard
                    }

                    Log.d("TripScreen", "FindDriverCard clicked")
                    stateTripViewModel.confirmPickup()
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
                    val fare = fare
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

                    isConfirmed = true
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
}

@Composable
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

    // Listen for trip updates
    LaunchedEffect(tripId) {
        tripsRef.whereEqualTo("_id", tripId)
            .addSnapshotListener { documents, error ->
                if (error != null) {
                    Log.e("Firestore", "❌ Error listening for updates: ${error.message}")
                    return@addSnapshotListener
                }

                documents?.let {
                    for (document in it) {
                        val driverLat = document.getDouble("driverLocation.latitude")
                        val driverLng = document.getDouble("driverLocation.longitude")

                        if (driverLat != null && driverLng != null) {
                            driverLocation = GeoPoint(driverLat, driverLng)
                            Log.d("Firestore", "✅ Driver Location Updated: $driverLocation")
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
            driverLocation = driverLocation,
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

// تجهيز التعليمات بصيغة Map
            val instructionList = instructions.map { inst ->
                mapOf(
                    "text" to inst.text,
                    "distance" to inst.distance,
                    "time" to inst.time,
                    "sign" to inst.sign,
                    "street_name" to inst.street_name,
                    "street_destination" to inst.street_destination,
                    "exit_number" to inst.exit_number,
                    "exited" to inst.exited,
                    "interval" to inst.interval,
                    "last_heading" to inst.last_heading,
                    "turn_angle" to inst.turn_angle
                )
            }

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
                                    "instructions" to instructionList // ✅ تم إضافة التعليمات هنا
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