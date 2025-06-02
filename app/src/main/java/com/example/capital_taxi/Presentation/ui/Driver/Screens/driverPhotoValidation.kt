package com.example.capital_taxi.Presentation.ui.Driver.Screens

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.example.capital_taxi.R
import com.google.firebase.firestore.FirebaseFirestore
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DriverPhotoValidationScreen(
    navController: NavController,
    onValidationComplete: () -> Unit
) {

    val context = LocalContext.current
    var profileImageUrl by remember { mutableStateOf<String?>(null) }
    val sharedPreferences = context.getSharedPreferences("your_prefs", Context.MODE_PRIVATE)
    val driverId = sharedPreferences.getString("driver_id", null)  // القيمة الافتراضية لو مش موجودة هنا null

    LaunchedEffect(driverId) {
        val db = FirebaseFirestore.getInstance()
        db.collection("drivers")
            .whereEqualTo("id", driverId)
            .get()
            .addOnSuccessListener { documents ->
                for (doc in documents) {
                    profileImageUrl = doc.getString("imageUrl")
                    break // نكتفي بأول نتيجة
                }
            }
            .addOnFailureListener { e ->
                Log.e("DriverPhotoValidation", "Error fetching image URL", e)
            }
    }



    var showError by remember { mutableStateOf(false) }
    var imageFile by remember { mutableStateOf<File?>(null) }
    var capturedImageUri by remember { mutableStateOf<Uri?>(null) }
    var pendingImageUri by remember { mutableStateOf<Uri?>(null) }
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header with icon
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
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
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
                        .border(
                            2.dp,
                            MaterialTheme.colorScheme.primary,
                            CircleShape
                        )
                        .background(MaterialTheme.colorScheme.surface)
                        .align(Alignment.CenterHorizontally)
                ) {
                    if (profileImageUrl != null) {
                        AsyncImage(
                            model = profileImageUrl,
                            contentDescription = "Original driver photo",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        // صورة افتراضية أو تحميل
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
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
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
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
                    if (capturedImageUri == null) {
                        if (showError) MaterialTheme.colorScheme.error
                        else MaterialTheme.colorScheme.outline
                    } else {
                        MaterialTheme.colorScheme.primary
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
                createImageFile(context)?.let { file ->
                    imageFile = file
                    createImageUri(context, file)?.let { uri ->
                        pendingImageUri = uri
                        cameraLauncher.launch(uri)
                    } ?: run {
                        showError = true
                        Log.e("PhotoDebug", "Failed to create URI")
                    }
                } ?: run {
                    showError = true
                    Log.e("PhotoDebug", "Failed to create file")
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

        Spacer(modifier = Modifier.height(32.dp))

        // Confirm Button
        Button(
            onClick = {
                imageFile?.let { file ->
                    Log.d("DriverPhotoValidation", "File exists: ${file.exists()}, size: ${file.length()}")

                    if (file.exists() && file.length() > 0) {
                        onValidationComplete()
                    } else {
                        // ممكن تظهر رسالة خطأ أو تعامل الحالة دي
                        Log.e("DriverPhotoValidation", "Image file does not exist or is empty!")
                    }
                } ?: run {
                    Log.e("DriverPhotoValidation", "Image file is null!")
                }
            },
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
            enabled = capturedImageUri != null,
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 4.dp,
                pressedElevation = 8.dp
            )
        ) {
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
private fun createImageFile(context: Context): File? {
    return try {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "JPEG_${timeStamp}_"
        val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.apply {
            mkdirs() // تأكد من وجود المجلد
        }
        File.createTempFile(
            imageFileName,
            ".jpg",
            storageDir
        ).apply {
            createNewFile() // تأكد من إنشاء الملف فعلياً
        }.also {
            Log.d("PhotoDebug", "File created at: ${it.absolutePath}")
        }
    } catch (e: Exception) {
        Log.e("PhotoDebug", "Error creating file", e)
        null
    }
}

private fun createImageUri(context: Context, file: File): Uri? {
    return try {
        FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider", // يجب أن يتطابق مع الـ Manifest
            file
        ).also {
            Log.d("PhotoDebug", "URI created: $it")
        }
    } catch (e: Exception) {
        Log.e("PhotoDebug", "Error creating URI", e)
        null
    }
}