package com.example.capital_taxi.Presentation.ui.shared.search_for_location

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.capital_taxi.Navigation.Destination
import com.example.capital_taxi.Presentation.ui.shared.search_for_location.Components.LocationPrompt
import com.example.capital_taxi.Presentation.ui.shared.search_for_location.Components.RequestLocationPermission
import com.example.capital_taxi.Presentation.ui.shared.search_for_location.Components.TopBar
import com.example.capital_taxi.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchForLocation(navController: NavController) {
    val context = LocalContext.current

    // ActivityResultLauncher for requesting location permission
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                // If permission is granted, navigate to the home screen
                navController.navigate(Destination.UserHomeScreen.route)
            } else {
                // Handle the case where permission is denied
                // You can show a message or prompt for retry
            }
        }
    )

    // Check for location permission
    val locationPermission = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    Scaffold(
        topBar = { TopBar(navController) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color.White),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                ) {
                    Image(
                        painter = painterResource(R.drawable.search_location),
                        contentDescription = null
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Location prompt
                LocationPrompt()

                Spacer(modifier = Modifier.weight(1f))

                // Handle location permission request
                RequestLocationPermission(
                    permissionLauncher = { permissionLauncher.launch(it) },
                    locationPermissionStatus = locationPermission,
                    onPermissionGranted = { navController.navigate(Destination.UserHomeScreen.route) }
                )

                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}
