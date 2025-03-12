package com.example.capital_taxi.Presentation.ui.shared.Confirm_information

import android.net.Uri
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.example.capital_taxi.Helper.User_Image.getImageFromSharedPreferences
import com.example.capital_taxi.Helper.User_Image.saveImageToInternalStorage
import com.example.capital_taxi.Navigation.Destination
import com.example.capital_taxi.Presentation.ui.shared.Confirm_information.Components.BackButton
import com.example.capital_taxi.Presentation.ui.shared.Confirm_information.Components.EmailField
import com.example.capital_taxi.Presentation.ui.shared.Confirm_information.Components.NameField
import com.example.capital_taxi.Presentation.ui.shared.Confirm_information.Components.NextButton
import com.example.capital_taxi.Presentation.ui.shared.Confirm_information.Components.PhoneNumberField
import com.example.capital_taxi.Presentation.ui.shared.Confirm_information.Components.ProfileImage
import com.example.capital_taxi.Presentation.ui.shared.Confirm_information.Components.TitleText
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmInformation(
    navController: NavController,
    Name: String,
    Email: String,
    PhotoUrl: String?
) {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("UserProfilePrefs", Context.MODE_PRIVATE)

    // Load saved URI from SharedPreferences
    var profilePictureUri by remember {
        mutableStateOf(
            sharedPreferences.getString("profile_picture_uri", null)?.let { Uri.parse(it) }
        )
    }

    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                // Save the image to internal storage
                val savedUri = saveImageToInternalStorage(context, uri)
                if (savedUri != null) {
                    profilePictureUri = savedUri // Update the state immediately
                    println("New URI: $savedUri") // Log the new URI
                    // Save the URI to SharedPreferences
                    sharedPreferences.edit()
                        .putString("profile_picture_uri", savedUri.toString())
                        .apply()
                } else {
                    Toast.makeText(context, "Failed to save image", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "No image selected", Toast.LENGTH_SHORT).show()
            }
        }

    var name by remember { mutableStateOf(Name) }
    var email by remember { mutableStateOf(Email) }
    var phoneNumber by remember { mutableStateOf("") }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { null },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        BackButton()
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                TitleText()
                Spacer(modifier = Modifier.height(20.dp))

                // Use a key to force recomposition when the URI changes
                ProfileImage(
                    key = profilePictureUri.toString(), // Force recomposition when URI changes
                    photo = profilePictureUri ?: Uri.parse(PhotoUrl ?: ""),
                    getContent = { launcher.launch("image/*") },
                    painter = rememberAsyncImagePainter(
                        ImageRequest.Builder(LocalContext.current)
                            .data(profilePictureUri ?: PhotoUrl)
                            .size(Size.ORIGINAL)
                            .build()
                    )
                )
                Spacer(modifier = Modifier.height(20.dp))

                NameField(name = name, onNameChange = { name = it })
                Spacer(modifier = Modifier.height(20.dp))

                EmailField(email = email)
                Spacer(modifier = Modifier.height(20.dp))

                PhoneNumberField(
                    phoneNumber = phoneNumber,
                    onPhoneNumberChange = { phoneNumber = it }
                )
                Spacer(modifier = Modifier.height(20.dp))

                NextButton {
                    navController.navigate(Destination.OtpScreen.route)
                }
            }
        }
    }
}