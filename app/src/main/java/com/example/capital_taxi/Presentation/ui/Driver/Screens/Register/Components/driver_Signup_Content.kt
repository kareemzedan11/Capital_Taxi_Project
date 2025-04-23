import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.example.capital_taxi.Presentation.Common.All_Register_textFields
import com.example.capital_taxi.Presentation.Common.AlreadyHaveAccount
import com.example.capital_taxi.Presentation.Common.RegisterHeader
import com.example.capital_taxi.Presentation.Common.TermsAndConditionsCheckbox
import com.example.capital_taxi.Presentation.Common.userMediaLoginOption
import com.example.capital_taxi.Presentation.ui.Passengar.Screens.Register.Components.registerDriver
import com.example.capital_taxi.Presentation.ui.Passengar.Screens.Register.Components.sendDriverData
import com.example.capital_taxi.R
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun driverSignupContent(navController: NavController) {
    val context = LocalContext.current

    val name = remember { mutableStateOf("") }
    val username = remember { mutableStateOf("") }
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val confirmPassword = remember { mutableStateOf("") }

    val phone = remember { mutableStateOf("") }
    val carType = remember { mutableStateOf("") }
    val carNumber = remember { mutableStateOf("") }
    val carModel = remember { mutableStateOf("") }
    val carColor = remember { mutableStateOf("") }
    var isChecked by remember { mutableStateOf(false) }

    val profileImage = remember { mutableStateOf<Bitmap?>(null) }
    val nationalIdFront = remember { mutableStateOf<Bitmap?>(null) }
    val nationalIdBack = remember { mutableStateOf<Bitmap?>(null) }
    val licenseFront = remember { mutableStateOf<Bitmap?>(null) }
    val licenseBack = remember { mutableStateOf<Bitmap?>(null) }
    val carLicenseFront = remember { mutableStateOf<Bitmap?>(null) }
    val carLicenseBack = remember { mutableStateOf<Bitmap?>(null) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        RegisterHeader()
        Spacer(modifier = Modifier.height(16.dp))

        All_Register_textFields(
            name = name,
            username = username,
            email = email,
            password = password,
            confirmPassword = confirmPassword,
            phone = phone,
            carType = carType,
            carNumber = carNumber,
            carColor = carColor,
            carModel = carModel
        )
        Spacer(modifier = Modifier.height(16.dp))

        DriverDocumentCaptureSection("Profile Image", profileImage)
        DriverDocumentCaptureSection("National ID (Front)", nationalIdFront)
        DriverDocumentCaptureSection("National ID (Back)", nationalIdBack)
        DriverDocumentCaptureSection("Driver License (Front)", licenseFront)
        DriverDocumentCaptureSection("Driver License (Back)", licenseBack)
        DriverDocumentCaptureSection("Car License (Front)", carLicenseFront)
        DriverDocumentCaptureSection("Car License (Back)", carLicenseBack)

        Spacer(modifier = Modifier.height(16.dp))

        TermsAndConditionsCheckbox(isChecked = isChecked, onCheckedChange = { isChecked = it })

        Spacer(modifier = Modifier.height(20.dp))

        driverSignUpButton(
            isEnabled = isChecked,
            onClick = {
                sendDriverData(
                    name.value,
                    username.value,
                    email.value,
                    phone.value,
                    carType.value,
                    carNumber.value,
                    carColor = carColor.value,
                    carModel = carModel.value,
                    profile = profileImage.value?.let { bitmapToFile(context, it, "profile.jpg") },
                    nationalIdFront = nationalIdFront.value?.let { bitmapToFile(context, it, "national_id_front.jpg") },
                    nationalIdBack = nationalIdBack.value?.let { bitmapToFile(context, it, "national_id_back.jpg") },
                    licenseFront = licenseFront.value?.let { bitmapToFile(context, it, "license_front.jpg") },
                    licenseBack = licenseBack.value?.let { bitmapToFile(context, it, "license_back.jpg") },
                    carLicenseFront = carLicenseFront.value?.let { bitmapToFile(context, it, "car_license_front.jpg") },
                    carLicenseBack = carLicenseBack.value?.let { bitmapToFile(context, it, "car_license_back.jpg") }
,
                            context =   context,
                )


                registerDriver(
                    name.value,
                    username.value,
                    email.value,
                    password.value,
                    phone.value,
                    navController,
                    context,

                    nationalIdFront = nationalIdFront.value?.let { bitmapToFile(context, it, "national_id_front.jpg") },
                    nationalIdBack = nationalIdBack.value?.let { bitmapToFile(context, it, "national_id_back.jpg") },
                    licenseFront = licenseFront.value?.let { bitmapToFile(context, it, "license_front.jpg") },
                    licenseBack = licenseBack.value?.let { bitmapToFile(context, it, "license_back.jpg") },
                    carLicenseFront = carLicenseFront.value?.let { bitmapToFile(context, it, "car_license_front.jpg") },
                    carLicenseBack = carLicenseBack.value?.let { bitmapToFile(context, it, "car_license_back.jpg") }


                )
            },
            text = R.string.Continue
        )

        Spacer(modifier = Modifier.padding(30.dp))

        userMediaLoginOption()

        Spacer(modifier = Modifier.padding(22.dp))

        AlreadyHaveAccount(navController)
    }
}

@Composable
fun DriverDocumentCaptureSection(title: String, capturedBitmap: MutableState<Bitmap?>) {
    val context = LocalContext.current
    val fileName = "${title.replace(" ", "_").lowercase()}.jpg"
    val file = File(context.filesDir, fileName)

    LaunchedEffect(Unit) {
        if (file.exists()) {
            capturedBitmap.value = BitmapFactory.decodeFile(file.absolutePath)
        }
    }

    val captureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            capturedBitmap.value = bitmap
            saveBitmapToFile(bitmap, file)
        } else {
            Toast.makeText(context, "Failed to capture photo.", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = title, fontSize = 18.sp, fontWeight = FontWeight.Bold)

        Box(
            modifier = Modifier
                .size(200.dp)
                .background(Color.LightGray),
            contentAlignment = Alignment.Center
        ) {
            if (capturedBitmap.value != null) {
                Image(
                    bitmap = capturedBitmap.value!!.asImageBitmap(),
                    contentDescription = "Captured Photo",
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Image(
                    painter = painterResource(R.drawable.message),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        Button(
            onClick = { captureLauncher.launch(null) },
            modifier = Modifier
                .width(200.dp)
                .height(50.dp),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text(text = "Capture $title", fontSize = 16.sp)
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
fun bitmapToFile(context: Context, bitmap: Bitmap, fileName: String): File {
    val file = File(context.cacheDir, fileName)
    try {
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return file
}

@Composable
fun driverSignUpButton(isEnabled: Boolean, onClick: () -> Unit,text:Int) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
        enabled = isEnabled,
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(colorResource(R.color.primary_color))
    ) {
        Text(
            text = stringResource(id =text),
            fontSize = responsiveTextSize(
                fraction = 0.06f,
                minSize = 14.sp,
                maxSize = 18.sp
            ),
            fontFamily = CustomFontFamily,
            color = Color.Black
        )
    }
}