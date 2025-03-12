package com.example.capital_taxi.ui.screens.Driver.VerficationScreens

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.app.ui.theme.CustomFontFamily
import com.example.app.ui.theme.responsiveTextSize
import com.example.capital_taxi.Navigation.Destination
import com.example.capital_taxi.R
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NationalIDValidation(navController: NavController) {


            Column(
                modifier = Modifier


                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(60.dp))
                Text(
                    text = stringResource(R.string.national_id_card),
                    color = Color.Black,
                    fontSize = responsiveTextSize(
                        fraction = 0.06f,
                        minSize = 24.sp,
                        maxSize = 30.sp
                    ),


                    fontFamily = CustomFontFamily,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Front of National ID Card Section
                DriverNationalIDCaptureSection(
                    title = stringResource(R.string.front_of_national_id),
                    buttonText = stringResource(R.string.add_front_photo),
                    backgroundColor = Color(0xFFE0F7FA) // Light Blue
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Back of National ID Card Section
                DriverNationalIDCaptureSection(
                    title = stringResource(R.string.back_of_national_id),
                    buttonText = stringResource(R.string.add_back_photo),
                    backgroundColor = Color(0xFFFCE4EC) // Light Pink
                )

            }
        }



@Composable
fun DriverNationalIDCaptureSection(
    title: String,
    buttonText: String,
    backgroundColor: Color
) {
    var capturedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    val context = LocalContext.current

    // Define file path for storage
    val fileName = if (title.contains("Front")) "national_id_front.jpg" else "national_id_back.jpg"
    val file = File(context.filesDir, fileName)

    // Load the saved image if available
    LaunchedEffect(Unit) {
        if (file.exists()) {
            val bitmap = BitmapFactory.decodeFile(file.absolutePath)
            capturedBitmap = bitmap
        }
    }

    // Camera launcher
    val captureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            capturedBitmap = bitmap
            saveBitmapToFile(bitmap, file)
        } else {
            Toast.makeText(context, "Failed to capture photo.", Toast.LENGTH_SHORT).show()
        }
    }

    // Box with background color
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = title,
                color = Color.Black,
                fontSize = responsiveTextSize(fraction = 0.06f, minSize = 18.sp, maxSize = 22.sp),


                fontFamily = CustomFontFamily,
                fontWeight = FontWeight.Bold
            )

            Box(
                modifier = Modifier
                    .size(200.dp)
                    .background(Color.LightGray),
                contentAlignment = Alignment.Center
            ) {
                if (capturedBitmap != null) {
                    Card(
                        elevation = CardDefaults.elevatedCardElevation(10.dp),
                        modifier = Modifier
                            .background(Color.Transparent)
                            .border(2.dp, Color.Transparent, RoundedCornerShape(16.dp))
                    ) {
                        Image(
                            bitmap = capturedBitmap!!.asImageBitmap(),
                            contentDescription = "Captured Photo",
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                } else {
                    Card(
                        elevation = CardDefaults.elevatedCardElevation(10.dp),
                        modifier = Modifier
                            .background(Color.Transparent)
                            .border(2.dp, Color.Transparent, RoundedCornerShape(16.dp))
                    ) {
                        Image(
                            painter = painterResource(R.drawable.id), // Placeholder image
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }


            Card(
                elevation = CardDefaults.elevatedCardElevation(10.dp),
                modifier = Modifier.background(Color.Transparent)

            ) {
                Button(
                    onClick = { captureLauncher.launch(null) },
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
                        text = buttonText,
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
        }
    }
}

fun saveBitmapToFile(bitmap: Bitmap, file: File) {
    try {
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }
}


