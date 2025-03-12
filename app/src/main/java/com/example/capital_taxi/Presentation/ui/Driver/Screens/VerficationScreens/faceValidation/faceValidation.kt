package com.example.capital_taxi.Presentation.ui.Driver.Screens.VerficationScreens.faceValidation

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.airbnb.lottie.compose.*
import com.example.app.ui.theme.CustomFontFamily
import com.example.app.ui.theme.responsiveTextSize
import com.example.capital_taxi.Navigation.Destination
import com.example.capital_taxi.R
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FaceValidation(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(R.string.face_validation_title),
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Box(
                            modifier = Modifier

                                .size(36.dp)
                                .background(Color.Transparent)
                                .border(4.dp, color = Color.Black, RoundedCornerShape(30.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                modifier = Modifier.size(26.dp),
                                painter = painterResource(id = R.drawable.baseline_arrow_back_ios_new_24),
                                contentDescription = "Back",
                                tint = Color.Black
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.navigationBars)

                    .background(Color.White),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                DriverDocumentUploader(navController = navController)
            }
        }
    }
}

@Composable
fun DriverDocumentUploader(navController: NavController) {
    var isUploaded by remember { mutableStateOf(false) }
    var capturedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    val savedImagePath = remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    // Camera launcher
    val captureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            val path = saveBitmapToFile(context, bitmap, "captured_photo.jpg")
            if (path != null) {
                savedImagePath.value = path
                capturedBitmap = bitmap
                isUploaded = true
            } else {
                Toast.makeText(context, "Failed to save photo.", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "Failed to capture photo.", Toast.LENGTH_SHORT).show()
        }
    }

    // Load saved bitmap on start
    LaunchedEffect(Unit) {
        val path = context.filesDir.resolve("captured_photo.jpg").absolutePath
        val loadedBitmap = loadBitmapFromFile(path)
        if (loadedBitmap != null) {
            capturedBitmap = loadedBitmap
            isUploaded = true
            savedImagePath.value = path
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        DocumentButton(
            navController,
            documentName = "ID Picture",
            isUploaded = isUploaded,
            capturedBitmap = capturedBitmap,
            onCapture = { captureLauncher.launch(null) }
        )
    }
}

fun saveBitmapToFile(context: Context, bitmap: Bitmap, fileName: String): String? {
    val file = File(context.filesDir, fileName) // Save in app's private directory
    return try {
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out) // Save as JPEG
        }
        file.absolutePath // Return the file path
    } catch (e: IOException) {
        e.printStackTrace()
        null
    }
}

fun loadBitmapFromFile(filePath: String): Bitmap? {
    return BitmapFactory.decodeFile(filePath)
}

@Composable
fun DocumentButton(
    navController: NavController,
    documentName: String,
    isUploaded: Boolean,
    capturedBitmap: Bitmap?,
    onCapture: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(90.dp))

        Text(
            text = stringResource(R.string.photo),
            color = Color.Black,
            fontSize = 25.sp,
            fontWeight = FontWeight.W900
        )
        Spacer(modifier = Modifier.height(50.dp))

        Box(
            modifier = Modifier
                .height(200.dp)
                .width(200.dp)
                .clip(CircleShape)
                .background(Color.LightGray),
            contentAlignment = Alignment.Center
        ) {
            if (capturedBitmap != null) {
                // Show captured image
                Image(
                    bitmap = capturedBitmap.asImageBitmap(),
                    contentDescription = stringResource(R.string.photo),
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                // Show Lottie animation
                val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.facescan))
                val progress by animateLottieCompositionAsState(
                    composition = composition,
                    iterations = LottieConstants.IterateForever
                )
                LottieAnimation(composition = composition, progress = { progress })
            }
        }

        Spacer(modifier = Modifier.height(50.dp))

        // Conditional button based on `isUploaded` state
        if (!isUploaded) {
            Card(
                elevation = CardDefaults.elevatedCardElevation(10.dp),
                modifier = Modifier.background(Color.Transparent)
            ) {
                Button(
                    onClick = onCapture,
                    colors = ButtonColors(
                        containerColor = colorResource(R.color.primary_color),
                        contentColor = colorResource(R.color.primary_color),
                        disabledContainerColor = colorResource(R.color.primary_color),
                        disabledContentColor = colorResource(R.color.primary_color),
                    ),
                    modifier = Modifier
                        .width(200.dp)
                        .height(50.dp)
                        .border(1.dp, Color.DarkGray, RoundedCornerShape(10.dp)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = stringResource(R.string.add_a_photo),
                        color = Color(0XFF111111),
                        fontSize = responsiveTextSize(
                            fraction = 0.06f,
                            minSize = 14.sp,
                            maxSize = 18.sp
                        ),


                        fontFamily = CustomFontFamily,
                    )
                }
            }
        } else {
            Button(
                onClick = { /* Handle upload confirmation */ },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                modifier = Modifier
                    .width(200.dp)
                    .height(50.dp),
                enabled = false // Disabled once photo is uploaded
            ) {
                Text(
                    text = stringResource(R.string.photo_is_uploaded),
                    color = Color.White,
                    fontSize = responsiveTextSize(
                        fraction = 0.06f,
                        minSize = 14.sp,
                        maxSize = 18.sp
                    ),


                    fontFamily = CustomFontFamily,
                )
            }
        }

        Spacer(modifier = Modifier.height(50.dp))

        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.clear_face),
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                fontSize = responsiveTextSize(fraction = 0.06f, minSize = 14.sp, maxSize = 18.sp),


                fontFamily = CustomFontFamily,
            )
            Text(
                text = stringResource(R.string.no_sunglasses),
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                fontSize = responsiveTextSize(fraction = 0.06f, minSize = 14.sp, maxSize = 18.sp),


                fontFamily = CustomFontFamily,
            )
            Text(
                text = stringResource(R.string.good_lighting),
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                fontSize = responsiveTextSize(fraction = 0.06f, minSize = 14.sp, maxSize = 18.sp),


                fontFamily = CustomFontFamily,
            )
        }

    }
}
