package com.example.capital_taxi.Presentation.ui.Driver.Screens.Home.Components

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.app.ui.theme.CustomFontFamily
import com.example.app.ui.theme.responsiveTextSize
import com.example.capital_taxi.Navigation.Destination
import com.example.capital_taxi.Presentation.ui.Passengar.Screens.Home.UserHome.Components.navigationDrawerItem
import com.example.capital_taxi.R
import com.example.capital_taxi.domain.DriverViewModel
import com.example.capital_taxi.domain.DriverViewModelFactory
import com.example.capital_taxi.domain.RetrofitClient
import androidx.lifecycle.viewModelScope
import coil.compose.AsyncImage
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun DriverNavigationDrawer(navController: NavController) {


    val apiService = RetrofitClient.apiService
    val viewModel: DriverViewModel = viewModel(factory = DriverViewModelFactory(apiService))

    val context = LocalContext.current
    val sharedPreferences = remember { context.getSharedPreferences("your_prefs", Context.MODE_PRIVATE) }
    val driverId = sharedPreferences.getString("driver_id", null)

    LaunchedEffect(Unit) {
        driverId?.let { viewModel.fetchDriverProfileById(it) }
    }


    val userProfile by viewModel.driverProfile.observeAsState()



    var userName by remember { mutableStateOf("") }
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.2f)
                .background(colorResource(R.color.primary_color)),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .clickable { navController.navigate(Destination.driverProfile.route) },
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = userProfile?.imageUrl,
                    contentDescription = "Profile Image",
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape),
                    placeholder = painterResource(R.drawable.person),
                    error = painterResource(R.drawable.person)
                )

                Spacer(Modifier.width(16.dp))

                Column {
                    Text(
                        userProfile?.name?:"",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black // Changed to white for better visibility
                    )

                    Spacer(Modifier.height(4.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_star_rate_24),
                            contentDescription = null,
                            tint = Color.Yellow // Added tint for visibility
                        )

                        Spacer(Modifier.width(4.dp))
                        Text(
                            String.format("%.1f", userProfile?.averageRating ?: 0.0),
                            fontSize = 16.sp,
                            color = Color.White,
                            fontFamily = CustomFontFamily
                        )

                    }
                }

                Spacer(Modifier.weight(1f))

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        HorizontalDivider()
        Box(modifier = Modifier.fillMaxWidth()) {

            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                navigationDrawerItem(
                    onClick = { navController.navigate(Destination.InboxPage.route) },

                    text = stringResource(R.string.Inbox)
                )

                Spacer(Modifier.height(10.dp))


                navigationDrawerItem(
                    onClick = { navController.navigate(Destination.InviteFriendsPage.route) },
                    text = stringResource(R.string.Invite_Friends)
                )

                Spacer(Modifier.height(10.dp))
                navigationDrawerItem(
                    onClick = { navController.navigate(Destination.notification.route) },
                    text = stringResource(R.string.Notifications)

                )
                Spacer(Modifier.height(10.dp))

                navigationDrawerItem(
                    onClick = { navController.navigate(Destination.IncomePage.route) },
                    text = stringResource(R.string.Income)
                )
                Spacer(Modifier.height(10.dp))

                navigationDrawerItem(
                    onClick = { navController.navigate(Destination.voucherScreen.route) },
                    text = stringResource(R.string.Wallet)
                )
                Spacer(Modifier.height(10.dp))
                navigationDrawerItem(
                    onClick = { navController.navigate(Destination.DriverHelpScreen.route) },
                    text = stringResource(R.string.Help)
                )
                Spacer(Modifier.height(10.dp))
                navigationDrawerItem(
                    onClick = { navController.navigate(Destination.driversettings.route) },
                    text = stringResource(R.string.Settings)
                )

            }
        }

    }
}


data class DriverProfile(
    val name: String,
    val averageRating: Double
)