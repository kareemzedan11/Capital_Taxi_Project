package com.example.capital_taxi.Presentation.ui.Driver.Screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.example.capital_taxi.Navigation.Destination
import com.example.capital_taxi.R
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.annotations.SerializedName
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


@Composable
fun DriverPhotoValidationScreen(
    navController: NavController,
    onValidationComplete: () -> Unit,
    viewModel: DriverPhotoValidationViewModel = viewModel()
) {
    val context = LocalContext.current
    var profileImageUrl by remember { mutableStateOf<String?>(null) }
    var isProfileLoading by remember { mutableStateOf(true) }
    val sharedPreferences = context.getSharedPreferences("your_prefs", Context.MODE_PRIVATE)
    val driverId = sharedPreferences.getString("driver_id", null)

    var showError by remember { mutableStateOf(false) }
    var imageFile by remember { mutableStateOf<File?>(null) }
    var capturedImageUri by remember { mutableStateOf<Uri?>(null) }
    var pendingImageUri by remember { mutableStateOf<Uri?>(null) }
    var isVerifying by remember { mutableStateOf(false) }
    var verificationResult by remember { mutableStateOf<Boolean?>(null) }
    var showConfirmDialog by remember { mutableStateOf(false) }

    // تعريف cameraLauncher هنا بحيث يكون في نفس النطاق
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            capturedImageUri = pendingImageUri
            showError = false
        } else {
            capturedImageUri = null
            showError = true
        }
    }

    // Permission handling
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            createImageFile(context)?.let { file ->
                imageFile = file
                createImageUri(context, file)?.let { uri ->
                    pendingImageUri = uri
                    cameraLauncher.launch(uri) // استخدام cameraLauncher هنا
                } ?: run {
                    showError = true
                }
            } ?: run {
                showError = true
            }
        } else {
            showError = true
        }
    }


    // Load driver profile
    LaunchedEffect(driverId) {
        if (driverId != null) {
            val db = FirebaseFirestore.getInstance()
            db.collection("drivers")
                .whereEqualTo("id", driverId)
                .get()
                .addOnSuccessListener { documents ->
                    isProfileLoading = false
                    for (doc in documents) {
                        profileImageUrl = doc.getString("imageUrl")
                        break
                    }
                }
                .addOnFailureListener { e ->
                    isProfileLoading = false
                    Log.e("DriverPhotoValidation", "Error fetching image URL", e)
                }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                painter = painterResource(id = R.drawable.uber),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Driver Identity Verification",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Original Photo Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Your Current Profile Photo",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Box(
                    modifier = Modifier
                        .size(150.dp)
                        .clip(CircleShape)
                        .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                        .background(MaterialTheme.colorScheme.surface)
                        .align(Alignment.CenterHorizontally)
                ) {
                    when {
                        isProfileLoading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                        profileImageUrl != null -> AsyncImage(
                            model = profileImageUrl,
                            contentDescription = "Original driver photo",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        else -> Icon(
                            painter = painterResource(id = R.drawable.baseline_camera_alt_24),
                            contentDescription = "No photo",
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }

        // Instructions Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Photo Requirements",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = "Take a new photo that matches your current profile picture:",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                    ),
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Column {
                    InstructionPoint("Same facial expression and appearance")
                    InstructionPoint("Similar lighting conditions")
                    InstructionPoint("No hats, sunglasses or face coverings")
                    InstructionPoint("Face clearly visible and centered")
                    InstructionPoint("High quality, not blurry")
                }
            }
        }

        // Photo Capture Section
        Text(
            text = "Take New Photo",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )

        Box(
            modifier = Modifier
                .size(240.dp)
                .shadow(8.dp, RoundedCornerShape(16.dp))
                .clip(RoundedCornerShape(16.dp))
                .border(
                    2.dp,
                    when {
                        capturedImageUri != null -> MaterialTheme.colorScheme.primary
                        showError -> MaterialTheme.colorScheme.error
                        else -> MaterialTheme.colorScheme.outline
                    },
                    RoundedCornerShape(16.dp)
                )
                .background(MaterialTheme.colorScheme.surface),
            contentAlignment = Alignment.Center
        ) {
            if (capturedImageUri != null) {
                val imageLoader = ImageLoader.Builder(context)
                    .diskCachePolicy(CachePolicy.DISABLED)
                    .memoryCachePolicy(CachePolicy.DISABLED)
                    .build()

                Image(
                    painter = rememberAsyncImagePainter(
                        ImageRequest.Builder(context)
                            .data(capturedImageUri)
                            .diskCachePolicy(CachePolicy.DISABLED)
                            .memoryCachePolicy(CachePolicy.DISABLED)
                            .build(),
                        imageLoader = imageLoader
                    ),
                    contentDescription = "Captured image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Camera icon",
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No photo yet",
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                when {
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.CAMERA
                    ) == PackageManager.PERMISSION_GRANTED -> {
                        createImageFile(context)?.let { file ->
                            imageFile = file
                            createImageUri(context, file)?.let { uri ->
                                pendingImageUri = uri
                                cameraLauncher.launch(uri)
                            } ?: run {
                                showError = true
                            }
                        } ?: run {
                            showError = true
                        }
                    }
                    else -> {
                        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text("Open Camera")
        }

        if (showError) {
            Text(
                text = "Failed to capture image. Please try again.",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        // Verification result message
        verificationResult?.let { isMatch ->
            if (isMatch) {
                LaunchedEffect(Unit) {
                    updateDriverPhotoVerification(driverId!!)
                    navController.navigate(Destination.DriverHomeScreen.route) {
                        popUpTo(Destination.SplashScreen.route) { inclusive = true }
                    }
                }

                Text(
                    text = "✅ Identity verified successfully!",
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            else {
                Text(
                    text = "❌ Photos don't match. Please try again.",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }


        Spacer(modifier = Modifier.height(32.dp))

        // Confirm Button
        Button(
            onClick = { showConfirmDialog = true },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (capturedImageUri != null)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.surfaceVariant,
                contentColor = if (capturedImageUri != null)
                    MaterialTheme.colorScheme.onPrimary
                else
                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            ),
            enabled = capturedImageUri != null && !isVerifying,
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 4.dp,
                pressedElevation = 8.dp
            )
        ) {
            if (isVerifying) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Text(
                    text = "Submit Verification",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                )
            }
        }
    }
// Confirmation Dialog
    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("Confirm Submission") },
            text = { Text("Are you sure you want to submit this photo for verification?") },
            confirmButton = {
                Button(onClick = {
                    showConfirmDialog = false
                    isVerifying = true
                    viewModel.verifyImages(
                        originalUrl = profileImageUrl,
                        newImageUri = capturedImageUri,
                        onResult = { isMatch ->
                            isVerifying = false
                            verificationResult = isMatch
                            if (isMatch == true) {
                                onValidationComplete()
                            }
                        },
                        context = context
                    )
                }) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }}

@Composable
private fun InstructionPoint(text: String) {
    Row(
        modifier = Modifier.padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
    }
}

class DriverPhotoValidationViewModel : ViewModel() {

    private val api = retrofit.create(MatchApi::class.java)
    private val supabase = createSupabaseClient(
        supabaseUrl = "https://mwncdoelxuwhtlrvtnap.supabase.co",
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im13bmNkb2VseHV3aHRscnZ0bmFwIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDUwMjU4NjUsImV4cCI6MjA2MDYwMTg2NX0.f5Zlz_WSLypyCUn67g2PEA5ZjHa8VsqjJDbxIgtBBTk"

    ) {
        install(Postgrest)
        install(Storage)
    }

    suspend fun uploadImageToSupabase(context: Context, imageUri: Uri): String? {
        return try {
            val fileName = "verify-driver/${UUID.randomUUID()}.jpg"
            val inputStream = context.contentResolver.openInputStream(imageUri) ?: return null
            val byteArray = inputStream.readBytes()

            Log.d("SupabaseUpload", "Uploading to Supabase... FileName: $fileName")

            val result = supabase.storage.from("verify-driver")
                .upload(fileName, byteArray, upsert = true)

            Log.d("SupabaseUpload", "Upload complete: $result")

            "https://mwncdoelxuwhtlrvtnap.supabase.co/storage/v1/object/public/verify-driver/$fileName"
        } catch (e: Exception) {
            Log.e("SupabaseUpload", "Upload failed: ${e.message}", e)
            null
        }
    }


    fun verifyImages(
        context: Context,
        originalUrl: String?,
        newImageUri: Uri?,
        onResult: (Boolean?) -> Unit
    )
 {
        viewModelScope.launch {
            try {
                if (originalUrl == null || newImageUri == null) {
                    onResult(null)
                    return@launch
                }

                // رفع الصورة الجديدة إلى Supabase
                val uploadedUrl = uploadImageToSupabase(context, newImageUri)

                if (uploadedUrl == null) {
                    onResult(null)
                    return@launch
                }

                Log.d("PhotoValidation", "Original URL: $originalUrl")
                Log.d("PhotoValidation", "Uploaded URL: $uploadedUrl")

                val response = api.compareImages(
                    MatchRequest(
                        originalImageUrl = originalUrl,
                        newImageUrl = uploadedUrl
                    )
                )
                onResult(response.match)
            } catch (e: Exception) {
                Log.e("PhotoValidation", "Error: ${e.message}")
                onResult(null)
            }
        }
    }
}

private fun createImageFile(context: Context): File? {
    return try {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            ?: context.filesDir

        File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        ).apply {
            createNewFile()
        }
    } catch (e: Exception) {
        null
    }
}

private fun createImageUri(context: Context, file: File): Uri? {
    return try {
        FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
    } catch (e: Exception) {
        null
    }
}

private val retrofit = Retrofit.Builder()
    .baseUrl("https://3abslam-driver-face-verification.hf.space/verify-driver/")
    .addConverterFactory(GsonConverterFactory.create())
    .build()

interface MatchApi {
    @POST("verify-driver/")


    suspend fun compareImages(@Body request: MatchRequest): MatchResponse
}

data class MatchRequest(
    @SerializedName("original_image_url") val originalImageUrl: String,
    @SerializedName("new_image_url") val newImageUrl: String
)

data class MatchResponse(
    val match: Boolean
)

fun updateDriverPhotoVerification(driverId: String) {
    val db = FirebaseFirestore.getInstance()
    db.collection("drivers")
        .whereEqualTo("id", driverId)
        .limit(1)
        .get()
        .addOnSuccessListener { documents ->
            if (!documents.isEmpty) {
                val docId = documents.documents[0].id
                db.collection("drivers").document(docId).update(
                    mapOf(
                        "lastPhotoCheck" to FieldValue.serverTimestamp(),
                        "isPhotoVerified" to true
                    )
                )
            } else {
                Log.e("DriverUpdate", "Driver not found with _id = $driverId")
            }
        }
        .addOnFailureListener { e ->
            Log.e("DriverUpdate", "Error fetching driver: ${e.message}", e)
        }
}

