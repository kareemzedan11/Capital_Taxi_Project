package com.example.capital_taxi.Presentation.ui.Passengar.Screens.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.ui.platform.LocalContext
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.app.ui.theme.CustomFontFamily
import com.example.app.ui.theme.responsiveTextSize
import com.example.capital_taxi.Navigation.Destination
import com.example.capital_taxi.Presentation.ui.Passengar.Screens.profile.Components.ProfileTextField
import com.example.capital_taxi.R
import com.example.capital_taxi.domain.DriverViewModel
import com.example.capital_taxi.domain.DriverViewModelFactory
import com.example.capital_taxi.domain.RetrofitClient

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Profile(navController: NavController, ) {

    val apiService = RetrofitClient.apiService // الحصول على API Service
    val viewModel: DriverViewModel = viewModel(factory = DriverViewModelFactory(apiService))


    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("your_prefs", Context.MODE_PRIVATE)
    val token = sharedPreferences.getString("USER_TOKEN", null)

    LaunchedEffect(Unit) {
        token?.let { viewModel.fetchUserProfile(it) }
    }

    val userProfile by viewModel.driverProfile.observeAsState()

    var userName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    LaunchedEffect(userProfile) {
        userProfile?.let {
            userName = it.name
            email = it.email
            phone = it.phone
        }
    }

    var imageUri by remember { mutableStateOf<Uri?>(null) }

    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) {
        imageUri = it
    }

    LaunchedEffect(Unit) {
        token?.let { viewModel.fetchUserProfile(it) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_arrow_back_24),
                            contentDescription = "Back",
                            tint = Color.Black
                        )
                    }
                },
                title = { Text("Account Settings", color = Color.Black, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = Color.Transparent)
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color.White),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier.padding(top = 16.dp).size(120.dp)
                ) {
                    Image(
                        painter = imageUri?.let { rememberAsyncImagePainter(it) } ?: painterResource(R.drawable.person),
                        contentDescription = "Profile Picture",
                        modifier = Modifier
                            .size(120.dp)
                            .background(Color.Gray, CircleShape)
                            .clickable { launcher.launch("image/*") }
                    )
                    IconButton(
                        onClick = { launcher.launch("image/*") },
                        modifier = Modifier.align(Alignment.BottomEnd).size(26.dp).background(Color.White, CircleShape)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_add_circle_outline_24),
                            contentDescription = "Upload",
                            tint = colorResource(R.color.primary_color)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                ProfileTextField(
                    label = "User Name",
                    value = userName,
                    onValueChange = { userName = it },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_person_outline_24),
                            contentDescription = null,
                            tint = colorResource(R.color.primary_color)
                        )
                    }
                )
                ProfileTextField(
                    label = "Email",
                    value = email,
                    onValueChange = { email = it },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_email_24),
                            contentDescription = null,
                            tint = colorResource(R.color.primary_color)
                        )
                    }
                )

                ProfileTextField(
                    label = "Phone",
                    value = phone,
                    onValueChange = { phone = it },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_phone_24),
                            contentDescription = null,
                            tint = colorResource(R.color.primary_color)
                        )
                    }
                )


                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { navController.navigate(Destination.UserHomeScreen.route) },
                    modifier = Modifier.fillMaxWidth(0.9f).height(60.dp),
                    colors = ButtonDefaults.buttonColors(colorResource(R.color.primary_color)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(text = "Save", fontSize = 18.sp, color = Color.Black)
                }
            }
        }
    )
}
