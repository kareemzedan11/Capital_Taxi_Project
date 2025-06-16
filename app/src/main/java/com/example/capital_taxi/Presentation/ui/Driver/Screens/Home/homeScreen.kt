package com.example.capital_taxi.Presentation.ui.Driver.Screens.Home

import TopBar
import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.app.ui.theme.CustomFontFamily
import com.example.app.ui.theme.responsiveTextSize
import com.example.capital_taxi.Navigation.Destination
import com.example.capital_taxi.Presentation.ui.Driver.Components.DriverControls
import com.example.capital_taxi.Presentation.ui.Driver.Components.InProgressMap
import com.example.capital_taxi.Presentation.ui.Driver.Components.MapStateViewModel
import com.example.capital_taxi.Presentation.ui.Driver.Screens.Home.Components.DriverNavigationDrawer
import com.example.capital_taxi.Presentation.ui.Driver.Screens.Home.Components.Home_Components.TripViewModel4
import com.example.capital_taxi.Presentation.ui.Driver.Screens.Home.Components.StartTrip
import com.example.capital_taxi.Presentation.ui.Driver.Screens.Home.Components.TripArrivedCard2
import com.example.capital_taxi.Presentation.ui.Driver.Screens.Home.Components.TripDetailsCard
import com.example.capital_taxi.Presentation.ui.Driver.Screens.Home.Components.captainToPassenger
import com.example.capital_taxi.Presentation.ui.Driver.Screens.Home.Components.dataTripViewModel
import com.example.capital_taxi.Presentation.ui.Driver.Screens.Home.Components.driverHomeScreenContent
import com.example.capital_taxi.Presentation.ui.Driver.Screens.Home.Components.driverlocation
import com.example.capital_taxi.Presentation.ui.Driver.viewmodel.DriversViewModel
import com.example.capital_taxi.Presentation.ui.Passengar.Components.StateTripViewModel
import com.example.capital_taxi.Presentation.ui.Passengar.Screens.Home.UserHome.Components.DirectionsApi
import com.example.capital_taxi.Presentation.ui.Passengar.Screens.Home.UserHome.Components.LocationDataStore
import com.example.capital_taxi.Presentation.ui.Passengar.Screens.Home.UserHome.Components.LocationViewModel5
import com.example.capital_taxi.Presentation.ui.Passengar.Screens.Home.UserHome.Components.ResultWrapper
import com.example.capital_taxi.Presentation.ui.Passengar.Screens.Home.UserHome.Components.TrackDriverScreen
import com.example.capital_taxi.domain.DirectionsViewModel
import com.example.capital_taxi.domain.Location
import com.example.capital_taxi.domain.Trip
import com.example.capital_taxi.domain.driver.model.acceptTripViewModel
import com.example.capital_taxi.domain.fetchTripDirections
import com.example.capital_taxi.domain.shared.TripViewModel
import com.example.capital_taxi.domain.shared.saveDriverLocationToRealtimeDatabase
import com.example.capital_taxi.domain.storedPoints
import com.example.capital_taxi.utils.DirectionsUpdater
import com.example.myapplication.DriverMapView
import com.example.myapplication.interpolateLocation
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Firebase
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.firestore
import com.google.maps.android.PolyUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.osmdroid.util.GeoPoint
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

@SuppressLint("UnrememberedMutableState")
@Composable
fun driverHomeScreen(navController: NavController) {
    val stateTripViewModel: StateTripViewModel = viewModel()
    val tripState by stateTripViewModel.uiState

    val scope = rememberCoroutineScope()
    val tripViewModel = remember { TripViewModel() }
    var availableTrips by remember { mutableStateOf<List<Trip>>(emptyList()) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    var startPoint = remember { mutableStateOf<GeoPoint?>(null) }
    var endPoint = remember { mutableStateOf<GeoPoint?>(null) }
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("your_prefs", Context.MODE_PRIVATE)
    val authToken = sharedPreferences.getString("driver_token", "") ?: ""
    val driver_id = sharedPreferences.getString("driver_id", "") ?: ""
    val directionsViewModel: DirectionsViewModel = viewModel()
    val DriverViewModel: DriverViewModel = viewModel()



    val viewmodel: driverlocation = viewModel()
    var tripListener by remember { mutableStateOf<ListenerRegistration?>(null) }

    var passengerID by remember { mutableStateOf<String?>(null) }
    var passengerName by remember { mutableStateOf<String?>(null) }
    var destination by remember { mutableStateOf<String?>(null) }
    var fare by remember { mutableStateOf<Double?>(null) }
    var distance by remember { mutableStateOf<Double?>(null) }
    var tripId by remember { mutableStateOf<String?>(null) }

    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val firestore = FirebaseFirestore.getInstance()
    val driverId = driver_id
    var driverLocation = viewmodel.driverLocation.value
    var driverLocationState2 by remember { mutableStateOf<GeoPoint?>(null) }
    var driverLocationState by remember { mutableStateOf<GeoPoint?>(null) }
    var previousLocation by remember { mutableStateOf<GeoPoint?>(null) }
    var carBearing by remember { mutableStateOf(0f) }
    val locationDataStore = LocationDataStore(context)
    val accepttrip: acceptTripViewModel = viewModel()
    val accepttripViewModel by accepttrip.isTripAccepted
    val startTrip by accepttrip.isTripStarted
    val EndTrip by accepttrip.isTripCompleted
    var showCancellationDialog by remember { mutableStateOf(false) }
    var rawLocation by remember { mutableStateOf<GeoPoint?>(null) }
    var smoothedLocation by remember { mutableStateOf<GeoPoint?>(null) }
    var currentLocation by remember { mutableStateOf<GeoPoint?>(null) }
    var passengerLocation2 by remember { mutableStateOf(GeoPoint(30.0444, 31.2357)) }
    var rating by remember { mutableStateOf<Double?>(null) }
    var showNavigationButton by remember { mutableStateOf(false) }
    var originPoint by remember { mutableStateOf<GeoPoint?>(null) }
    var destinationPoint by remember { mutableStateOf<GeoPoint?>(null) }
    val sharedPref = context.getSharedPreferences("trip_prefs", Context.MODE_PRIVATE)
    var tripStarted by remember { mutableStateOf(sharedPref.getBoolean("trip_started", false)) }
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






    // Handle trip state changes
    LaunchedEffect(tripState) {
        when {
            tripState.isCancelled -> {
                showCancellationDialog = true
            }
            tripState.isEnd -> {

            }
        }
    }
    LaunchedEffect(Unit) {
        Log.d("TripCheck", "🚀 LaunchedEffect started")

        val sharedPreferences = context.getSharedPreferences("your_prefs", Context.MODE_PRIVATE)
        val activeTripId = sharedPreferences.getString("active_trip_id", null)
           passengerID = sharedPreferences.getString("passenger_id", null)

        Log.d("TripCheck", "🧠 activeTripId = $activeTripId")

        if (activeTripId == null) {
            Log.d("TripCheck", "⚠️ No active trip found in SharedPreferences")
            return@LaunchedEffect
        }
        val sharedPref = context.getSharedPreferences("trip_prefs", Context.MODE_PRIVATE)
        val fareStr = sharedPref.getString("fare", "0.0") ?: "0.0"
          fare = fareStr.toDoubleOrNull() ?: 0.0

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

                if (status in listOf("accepted", "InProgress", "Started")) {
                    Log.d("TripCheck", "✅ Active trip detected, updating state")

                    // تحويل الحالة حسب التدرج
                    val updatedStatus = when (status) {
                        "accepted" -> {
                            stateTripViewModel.setStart(true)
                            "Started"

                        }
                        "InProgress" -> {
                            stateTripViewModel.setStart(true)
                            "InProgress"
                        }
                        "Started" -> {
                            stateTripViewModel.setStart(true)
                            "InProgress"
                        }

                        else -> status // لو هي Started تفضل زي ما هي
                    }
                    Log.d("TripCheck", "📌 Updated status: $updatedStatus")
                    stateTripViewModel.updateTripStatus(updatedStatus)
                }

                else {
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

    // Update map and get directions periodically
    LaunchedEffect(tripState.isAccepted, startPoint.value, endPoint.value) {
        while (tripState.isAccepted && startPoint.value != null && endPoint.value != null) {
            delay(2000)
            val origin = Location(startPoint.value!!.latitude, startPoint.value!!.longitude)
            val destination = Location(endPoint.value!!.latitude, endPoint.value!!.longitude)

            val token = sharedPreferences.getString("driver-token", null)
            if (token != null) {
                fetchTripDirections(
                    token = token,
                    origin = origin,
                    destination = destination,
                    directionsViewModel = directionsViewModel,
                    onSuccess = { directionsResponse ->
                        Log.d("TripDirections", "Successfully fetched directions: $directionsResponse")
                    },
                    onError = { errorMessage ->
                        Log.e("TripDirections", "Error fetching directions: $errorMessage")
                    }
                )
            }
        }
    }
    val directionsUpdater = remember(tripId) {
        tripId?.let {
            DirectionsUpdater(
                tripId = it,
                graphHopperApiKey = "c69abe50-60d2-43bc-82b1-81cbdcebeddc"
            )
        }
    }
    var originStr by remember { mutableStateOf<String?>(null) }
    var destinationStr by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(tripState.isTripBegin, tripState.inProgress, directionsUpdater, driverLocationState, passengerLocation2, originStr, destinationStr) {
        Log.d("TripLog", "LaunchedEffect triggered")
        Log.d("TripLog", "isTripBegin: ${tripState.isTripBegin}, inProgress: ${tripState.inProgress}")

        val shouldUseLiveTracking = tripState.inProgress
        val driverLocation = driverLocationState
        val passengerLocation = passengerLocation2

        if (directionsUpdater != null) {
            if (shouldUseLiveTracking && driverLocation != null && passengerLocation != null) {
                val origin = "${driverLocation.latitude},${driverLocation.longitude}"
                val destination = "${passengerLocation.latitude},${passengerLocation.longitude}"
                Log.d("TripLog", "Live tracking: driver to passenger")
                directionsUpdater.setRoute(originStr = origin, destinationStr = destination)
                directionsUpdater.start()
            } else if (tripState.isTripBegin && originStr != null && destinationStr != null && driverLocation != null) {
                val origin = "${driverLocation.latitude},${driverLocation.longitude}"

                Log.d("TripLog", "Trip begin: using static origin/destination")
                directionsUpdater.setRoute(originStr = origin, destinationStr = destinationStr!!)
                directionsUpdater.start()
            } else {
                Log.d("TripLog", "Stopping directionsUpdater")
                directionsUpdater.stop()
            }
        }
    }


    DisposableEffect(tripId) {
        var documentListener: ListenerRegistration? = null

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
                    Log.d("TripData", "Document found: ${document.id}")

                    val originMap = document.get("originMap") as? Map<String, Any>
                    Log.d("TripData", "originMap: $originMap")

                    val originLat = originMap?.get("lat") as? Double
                    val originLng = originMap?.get("lng") as? Double
                    Log.d("TripData", "originLat: $originLat, originLng: $originLng")

                    val destinationMap = document.get("destinationMap") as? Map<String, Any>
                    Log.d("TripData", "destinationMap: $destinationMap")

                    val destinationLat = destinationMap?.get("lat") as? Double
                    val destinationLng = destinationMap?.get("lng") as? Double
                    Log.d("TripData", "destinationLat: $destinationLat, destinationLng: $destinationLng")

                    passengerID = document.get("userId") as? String
                    Log.d("TripData", "passengerID: $passengerID")

                    if (originLat != null && originLng != null) {
                        passengerLocation2 = GeoPoint(originLat, originLng)
                        originPoint = GeoPoint(originLat, originLng)
                        Log.d("TripData", "passengerLocation2 set: $passengerLocation2")
                    }

                    if (destinationLat != null && destinationLng != null) {
                        destinationPoint = GeoPoint(destinationLat, destinationLng)
                    }
                    if (originLat != null && originLng != null) {
                        originStr = "$originLat,$originLng"
                    }
                    if (destinationLat != null && destinationLng != null) {
                        destinationStr = "$destinationLat,$destinationLng"
                    }

                    passengerID?.let { id ->
                        firestore.collection("users")
                            .whereEqualTo("id", id)
                            .limit(1)
                            .get()
                            .addOnSuccessListener { query ->
                                val userDoc = query.documents.firstOrNull()
                                Log.d("UserData", "User document: $userDoc")

                                val ratingMap = userDoc?.get("rating") as? Map<*, *>
                                Log.d("UserData", "Rating map: $ratingMap")

                                val count = (ratingMap?.get("count") as? Number)?.toInt() ?: 0
                                val total = (ratingMap?.get("total") as? Number)?.toInt() ?: 0
                                val averageRating = if (count > 0) total.toDouble() / count else null
                                rating = averageRating
                                Log.d("UserData", "count: $count, total: $total, averageRating: $averageRating")

                                passengerName = userDoc?.getString("name") ?: "مستخدم غير معروف"
                                Log.d("UserData", "passengerName: $passengerName")
                            }
                            .addOnFailureListener { e ->
                                Log.e("Firestore", "Error fetching user name", e)
                                passengerName = "مستخدم غير معروف"
                            }
                    }

                    documentListener = document.reference.addSnapshotListener { snapshot, error2 ->
                        if (error2 != null) {
                            Log.e("TripStatus", "Error listening to trip status", error2)
                            return@addSnapshotListener
                        }

                        snapshot?.let { doc ->
                            val status = doc.getString("status") ?: "pending"
                            Log.d("TripStatus", "Trip status: $status")

                            if (status == "Cancelled" && !tripState.isCancelled) {
                                stateTripViewModel.setCancelled()
                                Toast.makeText(context, "Trip cancelled by passenger", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                } else {
                    Log.w("TripData", "No trip document found for tripId: $tripId")
                }
            }

            tripListener = registration
        }

        onDispose {
            tripListener?.remove()
            documentListener?.remove()
        }
    }

    fun updateLocationAndStatus(driverId: String, location: GeoPoint) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: android.location.Location? ->
                if (location != null) {
                    val database = FirebaseDatabase.getInstance().getReference("drivers").child(driverId)
                    driverLocationState = GeoPoint(location.latitude, location.longitude)
                    val locationMap = mapOf(
                        "latitude" to location.latitude,
                        "longitude" to location.longitude
                    )

                    database.child("location").setValue(locationMap)
                        .addOnSuccessListener {
                            Log.d("RealtimeDB", "Location stored successfully")
                        }
                        .addOnFailureListener { e ->
                            Log.e("RealtimeDB", "Failed to store location", e)
                        }
                }
            }
        } else {
            Log.e("Permission", "Location permission not granted")
        }
    }

    fun requestLocationUpdates(
        fusedLocationClient: FusedLocationProviderClient,
        firestore: FirebaseFirestore,
        driverId: String,
        context: Context
    ) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val locationRequest = LocationRequest.create().apply {
            interval = 2000
            fastestInterval = 1000
            priority = Priority.PRIORITY_HIGH_ACCURACY
        }

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.locations.lastOrNull()?.let { location ->
                    val newLocation = GeoPoint(location.latitude, location.longitude)
                    previousLocation = newLocation
                    driverLocationState = newLocation
                    val geoPoint = GeoPoint(location.latitude, location.longitude)
                    updateLocationAndStatus(driverId, geoPoint)
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
    }

    var previousLocation2 by remember { mutableStateOf<GeoPoint?>(null) }
    var currentLocation2 by remember { mutableStateOf<GeoPoint?>(null) }

    val fusedLocationClient2 = remember { LocationServices.getFusedLocationProviderClient(context) }

    // Request location updates
    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val locationRequest = LocationRequest.create().apply {
                interval = 2000
                fastestInterval = 1000
                priority = Priority.PRIORITY_HIGH_ACCURACY
            }

            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    locationResult.locations.lastOrNull()?.let { location ->
                        val newPoint = GeoPoint(location.latitude, location.longitude)
                        previousLocation2 = currentLocation2
                        currentLocation2 = newPoint
                    }
                }
            }

            fusedLocationClient2.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        } else {
            println("Location permission not granted")
        }
    }


    fun parseGeoPoint(destination: String): GeoPoint {
        val parts = destination.split(",")
        return if (parts.size == 2) {
            try {
                val latitude = parts[0].toDouble()
                val longitude = parts[1].toDouble()
                GeoPoint(latitude, longitude)
            } catch (e: NumberFormatException) {
                Log.e("parseGeoPoint", "Invalid coordinates format: $destination")
                GeoPoint(30.0444, 31.2357)
            }
        } else {
            Log.e("parseGeoPoint", "Invalid destination format: $destination")
            GeoPoint(30.0444, 31.2357)
        }
    }

    // Function to open Google Maps with navigation
    fun openGoogleMapsNavigation(origin: GeoPoint, destination: GeoPoint) {
        try {
            val uri = Uri.parse("https://www.google.com/maps/dir/?api=1" +
                    "&origin=${origin.latitude},${origin.longitude}" +
                    "&destination=${destination.latitude},${destination.longitude}" +
                    "&travelmode=driving")

            val intent = Intent(Intent.ACTION_VIEW, uri)
            intent.setPackage("com.google.android.apps.maps")
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Google Maps app not installed", Toast.LENGTH_SHORT).show()
            val uri = Uri.parse("https://www.google.com/maps/dir/?api=1" +
                    "&origin=${origin.latitude},${origin.longitude}" +
                    "&destination=${destination.latitude},${destination.longitude}" +
                    "&travelmode=driving")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            context.startActivity(intent)
        }
    }

    // Show navigation button when trip is accepted
    LaunchedEffect(tripState.isAccepted) {
        showNavigationButton = tripState.isAccepted && originPoint != null && destinationPoint != null
    }

    Box(modifier = Modifier.fillMaxSize()) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet {
                    DriverNavigationDrawer(navController)
                }
            },
            gesturesEnabled = drawerState.isOpen,
            modifier = Modifier.fillMaxSize()
        ) {
            driverHomeScreenContent(navController)
            val passengerLocation: LocationViewModel5 = viewModel()
            val tripViewModel4: TripViewModel4 = viewModel()
            val tripLocation by tripViewModel4.tripLocation

            LaunchedEffect(Unit) {
                CoroutineScope(Dispatchers.IO).launch {
                    val savedLocation = locationDataStore.getLocation()
                    savedLocation?.let { (lat, lng) ->
                        val latLng = LatLng(lat, lng)
                        passengerLocation.updatePassengerLocation(latLng)
                    }
                }
            }

            val mapStateViewModel: MapStateViewModel = viewModel()
            val shouldShowTracking by mapStateViewModel.shouldShowTracking
            val db = FirebaseFirestore.getInstance()
            val directions = remember { mutableStateListOf<GeoPoint>() }
            var isDataLoading by remember { mutableStateOf(true) }
            var driverLocation2 by remember { mutableStateOf(GeoPoint(30.0444, 31.2357)) }

            LaunchedEffect(mapStateViewModel.isTripInProgress.value) {
                if (mapStateViewModel.isTripInProgress.value) {
                    isDataLoading = true
                    try {
                        val querySnapshot = db.collection("trips")
                            .whereEqualTo("_id", tripId)
                            .get()
                            .await()

                        if (!querySnapshot.isEmpty) {
                            val document = querySnapshot.documents.first()
                            destination = document.get("destination") as? String
                              passengerID = document.get("userId") as? String

                            if (passengerID != null) {
                                val sharedPreferences = context.getSharedPreferences("your_prefs", Context.MODE_PRIVATE)
                                sharedPreferences.edit()
                                    .putString("passenger_id", passengerID)
                                    .apply() // أو .commit() لو عايز تنتظر الحفظ
                                Log.d("TripCheck", "💾 passengerID saved in SharedPreferences: $passengerID")
                            } else {
                                Log.d("TripCheck", "⚠️ passengerID is null, not saved.")
                            }

                            passengerID?.let { id ->
                                try {
                                    val query = FirebaseFirestore.getInstance()
                                        .collection("users")
                                        .whereEqualTo("id", id)
                                        .limit(1)
                                        .get()
                                        .await()

                                    passengerName = query.documents.firstOrNull()
                                        ?.getString("name") ?: "مستخدم غير معروف"
                                } catch (e: Exception) {
                                    Log.e("Firestore", "Error fetching user name", e)
                                    passengerName = "مستخدم غير معروف"
                                }
                            }

                            fare = document.get("fare") as? Double ?: 0.0
                            val sharedPref = context.getSharedPreferences("trip_prefs", Context.MODE_PRIVATE)
                            sharedPref.edit().putString("fare", fare.toString()).apply()

                            distance = document.get("distanceInKm") as? Double ?: 0.0

                            val originMap = document.get("originMap") as? Map<String, Any>
                            val originLat = originMap?.get("lat") as? Double
                            val originLng = originMap?.get("lng") as? Double

                            val destinationMap = document.get("destinationMap") as? Map<String, Any>
                            val destinationLat = destinationMap?.get("lat") as? Double
                            val destinationLng = destinationMap?.get("lng") as? Double

                            if (originLat != null && originLng != null && destinationLat != null && destinationLng != null) {
                                passengerLocation2 = GeoPoint(originLat, originLng)
                                driverLocation2 = GeoPoint(destinationLat, destinationLng)

                                val result = DirectionsApi.getDirections(
                                    start = passengerLocation2,
                                    end = driverLocation2,
                                    apiKey = "89c93449-775a-43a8-8c0f-c635dd067da6",
                                    context = context,
                                    tripId = tripId!!
                                )

                                when (result) {
                                    is ResultWrapper.Success -> {
                                        val response = result.value
                                        val encodedPolyline = response.paths.firstOrNull()?.points

                                        if (encodedPolyline != null) {
                                            val decodedPoints = PolyUtil.decode(encodedPolyline).map {
                                                GeoPoint(it.latitude, it.longitude)
                                            }
                                            directions.clear()
                                            directions.addAll(decodedPoints)
                                            Log.d("Polyline", "Encoded polyline: $encodedPolyline")
                                            Log.d("time", "Encoded polyline: ${response.paths.firstOrNull()?.time}")
                                            Log.d("instructions", "Encoded polyline: ${response.paths.firstOrNull()?.instructions}")

                                            val updates = mapOf(
                                                "points" to encodedPolyline,
                                                "instructions" to (response.paths.firstOrNull()?.instructions ?: listOf()),
                                                "time" to (response.paths.firstOrNull()?.time ?: 0)
                                            )
                                            document.reference.update(updates)
                                                .addOnSuccessListener {
                                                    Log.d("Firestore33", "تم تحديث بيانات الرحلة بنجاح")
                                                }
                                                .addOnFailureListener {
                                                    Log.e("Firestore33", "فشل في تحديث البيانات: ${it.message}")
                                                }
                                        }
                                    }
                                    is ResultWrapper.Failure -> {
                                        Log.e("Directions", "فشل في جلب الاتجاهات: ${result.exception.message}")
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
            }




            Box(modifier = Modifier.fillMaxSize()) {
                if (mapStateViewModel.isTripInProgress.value) {
//                    InProgressMap(
//                        directions = directions,
//                        currentLocation = current,
//                        previousLocation = previous,
//                    )
                    InProgressMap(
                        directions = directions,
                        currentLocation = currentLocation2,
                        previousLocation = previousLocation2,
                    )
                    if (isDataLoading) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .fillMaxSize()
                                .alpha(0.7f)
                        ) {
                            CircularProgressIndicator(
                                color = Color.Blue,
                                strokeWidth = 5.dp
                            )
                        }
                    }
                } else if (shouldShowTracking) {
                    tripId?.let {
                        TrackDriverScreen(
                            tripId = it,
                            passengerLocation = passengerLocation2
                        )
                    }
                } else {
                    com.example.capital_taxi.utils.DriverMapView(
                        currentLocation = currentLocation2,
                        previousLocation = previousLocation2,
                    )

                }


                fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
                    val earthRadius = 6371.0 // كيلومتر

                    val dLat = Math.toRadians(lat2 - lat1)
                    val dLon = Math.toRadians(lon2 - lon1)

                    val a = sin(dLat / 2) * sin(dLat / 2) +
                            cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                            sin(dLon / 2) * sin(dLon / 2)

                    val c = 2 * atan2(sqrt(a), sqrt(1 - a))

                    return earthRadius * c
                }

                DisposableEffect(tripState.isStart) {
                    var listenerRegistration: ListenerRegistration? = null

                    if (tripState.isStart && !tripState.isAccepted && !tripState.isCancelled) {
                        listenerRegistration = tripViewModel.observePendingTrips(
                            driverId = driver_id,
                            onUpdate = { trips ->
                                val driverLocation = currentLocation2 ?: return@observePendingTrips

                                availableTrips = trips.filter { trip ->
                                    if (trip._id == tripId) return@filter false

                                    try {
                                        val originMap = trip.originMap ?: return@filter false
                                        val passengerLat = (originMap["lat"] as? Number)?.toDouble() ?: return@filter false
                                        val passengerLng = (originMap["lng"] as? Number)?.toDouble() ?: return@filter false

                                        val distance = calculateDistance(
                                            driverLocation.latitude,
                                            driverLocation.longitude,
                                            passengerLat,
                                            passengerLng
                                        )

                                        val maxDistance = 5000000.0
                                        distance <= maxDistance
                                    } catch (e: Exception) {
                                        Log.e("DistanceFilter", "Error calculating distance", e)
                                        false
                                    }
                                }
                            },
                            onError = { Log.e("driverHomeScreen", "❌ $it") }
                        )
                    }

                    onDispose {
                        listenerRegistration?.remove()
                    }
                }


                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    if (tripState.isStart && !tripState.isAccepted) {
                        tripLocation?.let {
                            updateLocationAndStatus(
                                driverId = driverId,
                                location = it
                            )
                        }
                        requestLocationUpdates(
                            fusedLocationClient = fusedLocationClient,
                            firestore = firestore,
                            driverId = driverId,
                            context = context
                        )
                    }
                    val tripDetailsViewModel: TripDetailsViewModel = viewModel()
                    val tripViewModel2: dataTripViewModel = viewModel()
                    val passengerData by tripDetailsViewModel.passengerData.collectAsState()
                    val showCard = remember { mutableStateOf(true) }

                    availableTrips.firstOrNull()?.let { trip ->
                        if (tripState.isStart && !tripState.isAccepted && showCard.value) {

                            LaunchedEffect(trip._id) {
                                delay(10000) // 15 ثانية
                                showCard.value = false
                            }

                            TripListener(tripId = trip._id)

                            // باقي الكود
                            TripDetailsCard(
                                light = false,
                                trip = trip,
                                availableTrips = availableTrips,
                                tripViewModel = tripViewModel,
                                onTripAccepted = {
                                    mapStateViewModel.enableTracking()
                                    accepttrip.acceptTrip()
                                    sharedPreferences.edit().putString("active_trip_id", trip._id).apply()

                                    stateTripViewModel.setAccepted()
                                    tripDetailsViewModel.setTripId(trip._id)
                                    tripId = trip._id
                                    val destinationPoint = parseGeoPoint(trip.origin)
                                    val start = parseGeoPoint(trip.destination)
                                    tripViewModel2.setTripDetails(trip.origin, trip.destination)
                                    Log.d("TripDetails", "${trip.origin} ${trip.destination}")
                                    stateTripViewModel.setStart(false)

                                    availableTrips = availableTrips.filter { it._id != trip._id }

                                    CoroutineScope(Dispatchers.IO).launch {
                                        val token = sharedPreferences.getString("driver-token", null)
                                        if (token != null) {
                                            fetchTripDirections(
                                                token = token,
                                                origin = Location(start.latitude, start.longitude),
                                                destination = Location(
                                                    destinationPoint.latitude,
                                                    destinationPoint.longitude
                                                ),
                                                directionsViewModel = directionsViewModel,
                                                onSuccess = { directionsResponse ->
                                                    sharedPreferences.edit().putString("active_trip_id", trip._id).apply()
                                                    Log.d("TripDirections", "Successfully fetched directions: $directionsResponse")
                                                },
                                                onError = { errorMessage ->
                                                    Log.e("TripDirections", "Error fetching directions: $errorMessage")
                                                }
                                            )
                                        }
                                    }

                                    driverLocation?.let { location ->
                                        saveDriverLocationToRealtimeDatabase(trip._id, location)
                                    } ?: run {
                                        Log.e("DriverLocation", "Driver location is null")
                                    }
                                },
                                onTripCancelled = {
                                    availableTrips = availableTrips.filter { it._id != trip._id }
                                },
                                userId2 = passengerData?.id ?: "",
                                rating = passengerData?.rating?.toString() ?: "0.0"
                            )

                }
                    }
                }

                val balance = DriverViewModel.balance.value

                LaunchedEffect(driverId) {
                    DriverViewModel.observeDriverBalance(driverId)
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .align(Alignment.TopStart)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
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
                        Spacer(modifier = Modifier.weight(1f))

                        Button(
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                            onClick = {},
                            modifier = Modifier
                                .wrapContentWidth()
                                .height(60.dp)
                                .padding(end = 80.dp)
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (balance != null) String.format("%.2f EGP", balance) else "Loading...",
                                    fontSize = responsiveTextSize(
                                        fraction = 0.06f,
                                        minSize = 14.sp,
                                        maxSize = 18.sp
                                    ),
                                    fontFamily = CustomFontFamily,
                                )
                            }
                        }
                        Spacer(modifier = Modifier.weight(1f))
                    }
                    if (tripState.inProgress ) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(top = 500.dp, end = 16.dp)
                        ) {
                            Button(
                                onClick = {
                                    openGoogleMapsNavigation(currentLocation2!!, originPoint!!)
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.Black,
                                    contentColor = Color.White
                                ),
                                modifier = Modifier
                                    .wrapContentWidth()
                                    .height(40.dp)
                            ) {
                                Text(
                                    text = "Navigate to Passenger",
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }

                    if (tripState.isTripBegin ) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(top = 500.dp, end = 16.dp)
                        ) {
                            Button(
                                onClick = {
                                    openGoogleMapsNavigation(currentLocation2!!, destinationPoint!!)
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.Black,
                                    contentColor = Color.White
                                ),
                                modifier = Modifier
                                    .wrapContentWidth()
                                    .height(40.dp)
                            ) {
                                Text(
                                    text = "Navigate to Destination",
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }

            LaunchedEffect(accepttripViewModel) {
                if (accepttripViewModel) {
                    delay(2000)
                    stateTripViewModel.setInProgress()
                }
            }

            LaunchedEffect(startTrip) {
                if (startTrip) {
                    stateTripViewModel.beginTrip()
                }
            }

            LaunchedEffect(EndTrip) {
                if (EndTrip) {
                    stateTripViewModel.TripEnd()
                }
            }

            if (!tripState.isAccepted && !tripState.inProgress) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .height(130.dp)
                        .align(Alignment.BottomCenter)
                        .padding(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 20.dp)
                ) {
                    DriverControls(
                        onClick = {stateTripViewModel.setStart(true) },
                        onClick2 = {stateTripViewModel.setStart(false) },
                        driverId = driverId,
                        tripLocation = tripLocation,
                        modifier = Modifier.wrapContentWidth()
                    )
                }
            }

            if (tripState.inProgress) {
                captainToPassenger(
                    context = context,
                    navController = navController,
                    tripId = tripId!!,
                    mapchangetoInPrograss = { mapStateViewModel.startTrip() },
                    onTripStarted = { accepttrip.startTrip() },
                    passengerName = passengerName ?: "Loading",
                    rating = rating.toString()?:"",
                    userId2 = passengerID
                )
            }

            if (tripState.isTripBegin) {
                StartTrip(
                    tripId!!, TripEnd = { accepttrip.EndTrip() }, driverId,
                    totalFare = fare!!
                )
            }

            if (tripState.isEnd) {
                TripArrivedCard2(
                    destination = destination ?: "",
                    fare = fare.toString(),
                    distance = distance.toString(),
                    tripId = tripId!!,
                    userId = passengerName,
                    driverId = driver_id ?: "1234",
                    onProblemSubmitted = {
                        Toast.makeText(context, "Problem reported successfully", Toast.LENGTH_SHORT)
                            .show()
                    },
                    onclick = {
                        sharedPreferences.edit().remove("active_trip_id").apply()

                        storedPoints = null
                        stateTripViewModel.resetAll()
                        navController.navigate(Destination.DriverHomeScreen.route) {
                            popUpTo(Destination.DriverHomeScreen.route) {
                                inclusive = true
                            }
                        }
                    },
                    userIdToRate = passengerID!!
                )
            }
            if (showCancellationDialog) {
                AlertDialog(
                    onDismissRequest = {
                        sharedPreferences.edit().remove("active_trip_id").apply()

                        storedPoints=null

                        showCancellationDialog = false
                        stateTripViewModel.resetAll()
                        navController.navigate(Destination.DriverHomeScreen.route) {
                            popUpTo(Destination.DriverHomeScreen.route) {
                                inclusive = true
                            }
                        }
                    },
                    title = {
                        Text(text = "Trip Cancellation")
                    },
                    text = {
                        Text(text = "The trip has been cancelled by the passenger")
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                sharedPreferences.edit().remove("active_trip_id").apply()

                                storedPoints=null

                                showCancellationDialog = false
                                stateTripViewModel.resetAll()
                                navController.navigate(Destination.DriverHomeScreen.route) {
                                    popUpTo(Destination.DriverHomeScreen.route) {
                                        inclusive = true
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                        ) {
                            Text("OK", color = Color.White)
                        }
                    }
                )
            }
        }
    }
}

class TripDetailsViewModel : ViewModel() {
    private val _tripId = MutableStateFlow<String?>(null)
    val tripId: StateFlow<String?> = _tripId.asStateFlow()

    private val _passengerData = MutableStateFlow<PassengerData?>(null)
    val passengerData: StateFlow<PassengerData?> = _passengerData.asStateFlow()

    private var firestoreListener: ListenerRegistration? = null

    fun setTripId(newTripId: String) {
        _tripId.value = newTripId
        setupFirestoreListeners(newTripId)
    }

    private fun setupFirestoreListeners(tripId: String) {
        firestoreListener?.remove()

        val query = FirebaseFirestore.getInstance()
            .collection("trips")
            .whereEqualTo("_id", tripId)

        firestoreListener = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e("TripDetails", "Error listening to trip", error)
                return@addSnapshotListener
            }

            snapshot?.documents?.firstOrNull()?.let { document ->
                // معالجة بيانات الرحلة
                val originMap = document.get("originMap") as? Map<String, Any>
                val destinationMap = document.get("destinationMap") as? Map<String, Any>
                val userId = document.get("userId") as? String

                // تحديث بيانات الراكب
                userId?.let { fetchPassengerData(it) }
            }
        }
    }

    private fun fetchPassengerData(userId: String) {
        FirebaseFirestore.getInstance()
            .collection("users")
            .whereEqualTo("id", userId)
            .limit(1)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val userDoc = querySnapshot.documents.firstOrNull()
                val ratingMap = userDoc?.get("rating") as? Map<*, *>

                val passengerData = PassengerData(
                    id = userId,
                    name = userDoc?.getString("name") ?: "مستخدم غير معروف",
                    rating = calculateRating(ratingMap)
                )

                _passengerData.value = passengerData
            }
            .addOnFailureListener { e ->
                Log.e("TripDetails", "Error fetching passenger data", e)
            }
    }

    private fun calculateRating(ratingMap: Map<*, *>?): Double {
        val count = (ratingMap?.get("count") as? Number)?.toInt() ?: 0
        val total = (ratingMap?.get("total") as? Number)?.toInt() ?: 0
        return if (count > 0) total.toDouble() / count else 0.0
    }

    override fun onCleared() {
        super.onCleared()
        firestoreListener?.remove()
    }
}

data class PassengerData(
    val id: String,
    val name: String,
    val rating: Double
)@Composable
fun TripListener(
    tripId: String?,
    tripViewModel: TripDetailsViewModel = viewModel()
) {
    DisposableEffect(tripId) {
        if (tripId != null) {
            tripViewModel.setTripId(tripId)
        }

        onDispose {
            // يتم التعامل مع إزالة الـ listeners داخل الـ ViewModel
        }
    }
}class DriverViewModel : ViewModel() {

    private val _balance = mutableStateOf<Double?>(null)
    val balance: State<Double?> = _balance

    private var listenerRegistration: ListenerRegistration? = null

    fun observeDriverBalance(driverId: String) {
        val db = Firebase.firestore

        // ألغِ أي Listener سابق
        listenerRegistration?.remove()

        listenerRegistration = db.collection("drivers")
            .whereEqualTo("id", driverId)
            .limit(1)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    _balance.value = 0.0
                    return@addSnapshotListener
                }

                if (snapshot != null && !snapshot.isEmpty) {
                    val doc = snapshot.documents[0]
                    val balanceValue = doc.getDouble("balance") ?: 0.0
                    _balance.value = balanceValue
                } else {
                    _balance.value = 0.0
                }
            }
    }

    override fun onCleared() {
        super.onCleared()
        // ألغِ الاستماع عند تدمير ViewModel
        listenerRegistration?.remove()
    }
}
