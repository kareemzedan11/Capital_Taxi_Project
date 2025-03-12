package com.example.capital_taxi.Presentation.ui.Passengar.Screens.Home.UserHome.Components.Trip_preparation

import android.content.Context
import android.location.Geocoder
import android.location.LocationManager
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.capital_taxi.Helper.PermissionViewModel
import com.example.capital_taxi.Helper.checkLocationPermission
import com.example.capital_taxi.R
import com.google.android.gms.location.LocationServices
import java.util.Locale
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material3.OutlinedTextField
import com.example.capital_taxi.Presentation.ui.Passengar.Screens.Home.UserHome.Components.fetchLocationCoordinates

@Composable
fun LocationModalBottomSheetContent(navController: NavController) {
    val permissionViewModel: PermissionViewModel = viewModel()
    val context = LocalContext.current

    // Check permissions when screen loads
    LaunchedEffect(context) {
        checkLocationPermission(context, permissionViewModel)
    }

    val isLocationGranted by permissionViewModel.isLocationGranted.collectAsState()

    // Check if location service is enabled
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    val isLocationEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    var locationName by remember { mutableStateOf("Fetching location...") }

    // Fetch location name
    LaunchedEffect(Unit) {
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            location?.let {
                val geocoder = Geocoder(context, Locale.getDefault())
                val addressList = geocoder.getFromLocation(it.latitude, it.longitude, 1)
                locationName = if (addressList != null && addressList.isNotEmpty()) {
                    addressList[0].getAddressLine(0)
                } else {
                    "Unable to fetch location"
                }
            } ?: run {
                locationName = "Unable to fetch location"
            }
        }
    }

    // 🔹 حفظ الاقتراحات في State
    var suggestions by remember { mutableStateOf(listOf<String>()) }
    var query by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.Arrange_your_trip),
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        PickupNowForMeUI(navController)
        PickupWithPickoffPoints(navController)

        Spacer(modifier = Modifier.padding(vertical = 10.dp))
        MostVisitedPlaces(navController)
        Spacer(modifier = Modifier.padding(vertical = 10.dp))

        Column(modifier = Modifier.padding(horizontal = 10.dp)) {
            HorizontalDivider(
                modifier = Modifier,
                thickness = 2.dp
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 200.dp)
        ) {
            items(suggestions) { suggestion ->
                Text(
                    text = suggestion,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            Log.d("Suggestion Clicked", "Clicked on suggestion: $suggestion") // إضافة Log هنا
                            query = suggestion // تحديث النص عند النقر على اقتراح
                            suggestions = emptyList() // إخفاء القائمة بعد الاختيار

                            // الآن طلب الإحداثيات عند اختيار الموقع
                            fetchLocationCoordinates(suggestion) { lat, lon ->
                                // طباعة الإحداثيات في الـ log
                                Log.d("Selected Location", "Latitude: $lat, Longitude: $lon")
                            }
                        }
                        .padding(10.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
                Divider()
            }
        }

    }}