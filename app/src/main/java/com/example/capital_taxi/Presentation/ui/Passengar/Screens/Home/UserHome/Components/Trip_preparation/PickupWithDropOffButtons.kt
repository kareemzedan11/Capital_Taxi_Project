package com.example.capital_taxi.Presentation.ui.Passengar.Screens.Home.UserHome.Components.Trip_preparation

import IntercityCard

import android.content.Context
import android.location.Geocoder
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.example.capital_taxi.Presentation.ui.Passengar.Screens.Home.UserHome.Components.PickupDropOffRow
import com.example.capital_taxi.Presentation.ui.Passengar.Screens.Home.UserHome.Components.fetchGraphHopperSuggestions

import com.example.capital_taxi.R
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint
import java.util.Locale
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.capital_taxi.Navigation.Destination
import com.example.capital_taxi.domain.DirectionsViewModel
import com.example.capital_taxi.domain.FareViewModel
import com.example.capital_taxi.domain.Location
import com.example.capital_taxi.domain.calculateFare
import com.example.capital_taxi.domain.fetchTripDirections
import com.example.capital_taxi.utils.Constants.ApiConstants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PickupWithDropOffButtons(
    navController: NavController,
    locationName: String? = "Select Pickup Location",
    viewModel: LocationViewModel = viewModel() // الحصول على ViewModel

) {
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val context = LocalContext.current
    if (showBottomSheet) {
        ModalBottomSheet(
            modifier = Modifier
                .fillMaxHeight()
                .padding(top = 40.dp),
            sheetState = sheetState,
            onDismissRequest = { showBottomSheet = false }
        ) {
            LocationModalBottomSheetContent(navController = navController)
        }
    }

    val locationViewModel: LocationViewModel = viewModel()
    val pickupLatLng = locationViewModel.pickupLocation
    val dropoffLatLng = locationViewModel.dropoffLocation
    // نقاط البداية والنهاية
    var startPoint = remember { mutableStateOf<GeoPoint?>(null) }
    val endPoint = remember { mutableStateOf<GeoPoint?>(null) }
    val sharedPreferences = context.getSharedPreferences("your_prefs", Context.MODE_PRIVATE)
    val token = sharedPreferences.getString("USER_TOKEN", null)
    val directionsViewModel: DirectionsViewModel = viewModel()
    var pickupLocation by remember { mutableStateOf("") }
    var pickupSuggestions by remember { mutableStateOf(emptyList<String>()) }
    var dropOffLocation by remember { mutableStateOf("") }
    var dropOffSuggestions by remember { mutableStateOf(emptyList<String>()) }
    val coroutineScope = rememberCoroutineScope()
    var selectedVehicleIndex by remember { mutableStateOf(-1) }
    var selectedVehicleName by remember { mutableStateOf("") } // Track the selected vehicle name

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(1.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = stringResource(R.string.Where_are_you_going_today),
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = Color.Black
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        PickupDropOffRow(
            iconRes = R.drawable.circle,
            hintText = pickupLocation.ifEmpty { stringResource(R.string.Select_Pickup_Location) },
            onValueChange = { text ->
                pickupLocation = text
                if (text.isNotEmpty()) {
                    coroutineScope.launch {
                        fetchGraphHopperSuggestions(
                            text,
                            "e315c5d2-3d52-42f1-a338-f5e57fc3e82f"
                        ) { result ->
                            pickupSuggestions = result
                        }
                    }
                } else {
                    pickupSuggestions = emptyList()
                }
            },
            value = pickupLocation // تأكد من ربط القيمة هنا
        )

        repeat(9) {
            VerticalDivider(
                thickness = 2.dp,
                modifier = Modifier
                    .height(5.dp)
                    .padding(start = 12.dp)
            )
        }

        PickupDropOffRow(
            iconRes = R.drawable.travel,
            hintText = dropOffLocation.ifEmpty { stringResource(R.string.Select_Drop_Off_Location) },
            onValueChange = { text ->
                dropOffLocation = text
                if (text.isNotEmpty()) {
                    coroutineScope.launch {
                        fetchGraphHopperSuggestions(
                            text,
                            "71ab0bb4-9572-4423-ab8f-332deb2827a7"
                        ) { result ->
                            dropOffSuggestions = result
                        }
                    }
                } else {
                    dropOffSuggestions = emptyList()
                }
            },
            value = dropOffLocation // تمرير القيمة
        )
        if (pickupSuggestions.isNotEmpty()) {
            Column(modifier = Modifier.fillMaxWidth()) {
                pickupSuggestions.forEach { suggestion ->
                    Text(
                        text = suggestion,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                pickupLocation = suggestion
                                pickupSuggestions = emptyList()

                                // تحديث startPoint عند اختيار اقتراح
                                getLatLngFromAddress(context, suggestion) { latLng ->
                                    if (latLng != null) {
                                        startPoint.value =
                                            GeoPoint(latLng.latitude, latLng.longitude)
                                        viewModel.setPickupLocation(latLng)
                                        Log.d(
                                            "PickupLocation",
                                            "Selected Pickup Location: $pickupLocation, Lat: ${latLng.latitude}, Lng: ${latLng.longitude}"
                                        )
                                    } else {
                                        Log.d(
                                            "PickupLocation",
                                            "Location not found for: $pickupLocation"
                                        )
                                    }
                                }
                            }
                            .padding(10.dp),
                        color = Color.Black,
                        fontSize = 16.sp
                    )
                }
            }
        }

        if (dropOffSuggestions.isNotEmpty()) {
            Column(modifier = Modifier.fillMaxWidth()) {
                dropOffSuggestions.forEach { suggestion ->
                    Text(
                        text = suggestion,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                dropOffLocation = suggestion
                                dropOffSuggestions = emptyList()

                                // تحديث endPoint عند اختيار اقتراح
                                getLatLngFromAddress(context, suggestion) { latLng ->
                                    if (latLng != null) {
                                        endPoint.value = GeoPoint(latLng.latitude, latLng.longitude)

                                        viewModel.setDropoffLocation(latLng)
                                        Log.d(
                                            "DropOffLocation",
                                            "Selected Drop-Off Location: $dropOffLocation, Lat: ${latLng.latitude}, Lng: ${latLng.longitude}"
                                        )

                                        if (startPoint.value != null && endPoint.value != null) {
                                            val origin = Location(
                                                startPoint.value!!.latitude,
                                                startPoint.value!!.longitude
                                            )
                                            val destination = Location(
                                                endPoint.value!!.latitude,
                                                endPoint.value!!.longitude
                                            )




                                            CoroutineScope(Dispatchers.IO).launch {
                                                if (token != null) {
                                                    fetchTripDirections(
                                                        token = token,
                                                        origin = origin,
                                                        destination = destination,
                                                        directionsViewModel = directionsViewModel, // ✅ أضف هذا السطر
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
                                    } else {
                                        Log.d(
                                            "DropOffLocation",
                                            "Location not found for: $dropOffLocation"
                                        )
                                    }
                                }
                            }
                            .padding(10.dp),
                        color = Color.Black,
                        fontSize = 16.sp
                    )
                }
            }
        }

        val fareViewModel: FareViewModel = viewModel()
          val url = ApiConstants.base_URL
        val vehicleOptionsState = remember { mutableStateOf(listOf<VehicleOption>()) }
        var selectedVehicleIndex by remember { mutableStateOf(-1) }
        var selectedVehicleName by remember { mutableStateOf("") }

        if (startPoint.value != null && endPoint.value != null) {

            val origin = Location(startPoint.value!!.latitude, startPoint.value!!.longitude)
            val destination = Location(endPoint.value!!.latitude, endPoint.value!!.longitude)

            // Log the origin and destination
            Log.d("LocationData", "Origin: (${origin.lat}, ${origin.lng}), Destination: (${destination.lat}, ${destination.lng})")

            // Build the full URL
            val fullUrl = "${url}trips/calculate-fare?origin=${origin.lat},${origin.lng}&destination=${destination.lat},${destination.lng}&paymentMethod=cash"
            Log.d("API_URL", "Request URL: $fullUrl")

            // Call calculateFare
            if (token != null) {
                calculateFare(
                    origin = origin,
                    destination = destination,
                    paymentMethod = "cash",
                    token = token,
                    coroutineScope = coroutineScope,
                    fareViewModel = fareViewModel, // ✅ أضف هذا السطر
                    onSuccess = { updatedOptions ->
                        vehicleOptionsState.value = updatedOptions
                    },
                    onError = { errorMessage ->
                        Log.e("FareCalculation", errorMessage)
                    }
                )
            }

            if (vehicleOptionsState.value.isNotEmpty()) {
                val vehicle = vehicleOptionsState.value.first() // أول عربية في القائمة

                Box(
                    modifier = Modifier
                        .width(170.dp)
                        .height(150.dp)
                        .padding(top = 10.dp)
                        .shadow(
                            elevation = 8.dp,
                            shape = RoundedCornerShape(20.dp),
                            clip = false
                        )
                        .background(color = colorResource(R.color.primary_color) , shape = RoundedCornerShape(20.dp))
                ) {
                    Column {
                        Box(
                            modifier = Modifier
                                .height(70.dp)

                                .background(
                                    color = colorResource(R.color.primary_color),
                                    shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(vehicle.imageRes),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        Box(
                            modifier = Modifier

                                .fillMaxWidth()
                                .wrapContentHeight()
                                .background(
                                    color = colorResource(R.color.primary_color),
                                    shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp)
                                )
                        ) {
                            Column(
                                modifier = Modifier.padding(start = 10.dp, top = 10.dp),
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = vehicle.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp,
                                    color = Color.White
                                )
                                Text(
                                    text = "£${vehicle.price}",
                                    fontSize = 18.sp,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }}
    Spacer(modifier = Modifier.padding(top = 15.dp))

    if (selectedVehicleIndex != -1) {
        IntercityCard(text = selectedVehicleName) // Pass selected vehicle name
    }
}

fun getLatLngFromAddress(
    context: Context,
    address: String,
    onLocationRetrieved: (LatLng?) -> Unit
) {
    val geocoder = Geocoder(context, Locale.getDefault())
    try {
        val addressList = geocoder.getFromLocationName(address, 1)
        if (addressList != null && addressList.isNotEmpty()) {
            val location = addressList[0]
            val latLng = LatLng(location.latitude, location.longitude)
            onLocationRetrieved(latLng) // Return the LatLng
        } else {
            onLocationRetrieved(null) // If no location found
        }
    } catch (e: Exception) {
        Log.e("LocationError", "Error fetching location: ${e.message}")
        onLocationRetrieved(null) // Return null if an error occurs
    }
}


class LocationViewModel : ViewModel() {
    private val _pickupLocation = mutableStateOf<LatLng?>(null)
    val pickupLocation: LatLng? get() = _pickupLocation.value

    private val _dropoffLocation = mutableStateOf<LatLng?>(null)
    val dropoffLocation: LatLng? get() = _dropoffLocation.value

    fun setPickupLocation(latLng: LatLng) {
        _pickupLocation.value = latLng
    }

    fun setDropoffLocation(latLng: LatLng) {
        _dropoffLocation.value = latLng
    }
}

@Preview(showBackground = true)
@Composable
fun PickupWithDropOffButtonsPreview(){
    PickupWithDropOffButtons(navController = NavController(LocalContext.current))
}