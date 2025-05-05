package com.example.capital_taxi.Presentation.ui.Driver.Screens.profile

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.capital_taxi.Navigation.Destination
import com.example.capital_taxi.Presentation.ui.Passengar.Screens.profile.Components.ProfileTextField
import com.example.capital_taxi.R
import com.example.capital_taxi.domain.DriverViewModel
import com.example.capital_taxi.domain.DriverViewModelFactory
import com.example.capital_taxi.domain.RetrofitClient
import com.google.firebase.firestore.FirebaseFirestore
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

// Helper function to save image to internal storage
private fun saveImageToInternalStorage(context: Context, uri: Uri): String? {
    return try {
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
        // Create a file in the app's internal files directory
        val file = File(context.filesDir, "profile_image.jpg") // Use a consistent filename
        val outputStream = FileOutputStream(file)
        inputStream?.copyTo(outputStream)
        inputStream?.close()
        outputStream.close()
        file.absolutePath // Return the absolute path of the saved file
    } catch (e: Exception) {
        e.printStackTrace()
        null // Return null if saving failed
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfile(navController: NavController, ) {

    val apiService = RetrofitClient.apiService
    val viewModel: DriverViewModel = viewModel(factory = DriverViewModelFactory(apiService))

    val context = LocalContext.current
    val sharedPreferences = remember { context.getSharedPreferences("your_prefs", Context.MODE_PRIVATE) }
    val driverId = sharedPreferences.getString("USER_ID", null)

    LaunchedEffect(Unit) {
        driverId?.let { viewModel.fetchUserProfileById(it) }
    }


    val userProfile by viewModel.userProfile.observeAsState()



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

    // --- Image Persistence Logic (Internal Storage) ---
    // 1. Load the saved file path from SharedPreferences
    val initialImagePath = remember { sharedPreferences.getString("PROFILE_IMAGE_PATH", null) }
    // 2. Create a File object if the path exists
    val initialImageFile = remember(initialImagePath) {
        initialImagePath?.let { File(it) }
    }
    // 3. Initialize the imageFile state with the loaded File or null if it doesn't exist
    var imageFile by remember { mutableStateOf<File?>(if (initialImageFile?.exists() == true) initialImageFile else null) }

    // 4. Define the ActivityResultLauncher
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            val filePath = saveImageToInternalStorage(context, it)
            filePath?.let {
                val file = File(it)
                imageFile = file

                // Save locally
                val editor = sharedPreferences.edit()
                editor.putString("PROFILE_IMAGE_PATH", it)
                editor.apply()

                // 🔁 رفع الصورة لـ Supabase وتحديث Firestore
                driverId?.let { id ->
                    CoroutineScope(Dispatchers.IO).launch {
                        val imageUrl = uploadProfileImage(id, file)
                        imageUrl?.let { url ->
                            updateUserImageUrlInFirestore(id, url)
                        }
                    }
                }
            }
        }
    }

    // --- End of Image Persistence Logic ---

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
                    // 6. Image composable now loads from the internal File object
                    Image(
                        painter = imageFile?.let { rememberAsyncImagePainter(it) } ?: painterResource(R.drawable.person),
                        contentDescription = "Profile Picture",
                        contentScale = ContentScale.Crop, // يملأ الدائرة بالصورة
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape) // هذا هو المهم لقص الصورة بشكل دائري
                            .background(Color.Gray, CircleShape)
                            .clickable { launcher.launch("image/*") }
                    )

                    IconButton(
                        onClick = { launcher.launch("image/*") }, // Launch image picker on click
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
val supabase = createSupabaseClient(
    supabaseUrl = "https://mwncdoelxuwhtlrvtnap.supabase.co",
    supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im13bmNkb2VseHV3aHRscnZ0bmFwIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDUwMjU4NjUsImV4cCI6MjA2MDYwMTg2NX0.f5Zlz_WSLypyCUn67g2PEA5ZjHa8VsqjJDbxIgtBBTk"

) {
    install(Storage)
    install(Postgrest)
}

suspend fun uploadProfileImage(userId: String, file: File): String? {
    return try {
        val bucket = supabase.storage.from("users-profile")

        // اسم فريد لكل صورة
        val fileName = "$userId-${System.currentTimeMillis()}.jpg"

        // رفع الصورة
        bucket.upload(fileName, file.readBytes())

        // جلب رابط الصورة الجديد (اللي اترفع فعليًا)
        val publicUrl = bucket.publicUrl(fileName)

        publicUrl
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

suspend fun updateUserImageUrlInFirestore(userId: String, imageUrl: String) {
    val cacheBustedUrl = "$imageUrl?t=${System.currentTimeMillis()}"
    val db = FirebaseFirestore.getInstance()
    db.collection("users")
        .whereEqualTo("id", userId)
        .get()
        .addOnSuccessListener { documents ->
            for (document in documents) {
                document.reference.update("imageUrl", cacheBustedUrl)
            }
        }
}

