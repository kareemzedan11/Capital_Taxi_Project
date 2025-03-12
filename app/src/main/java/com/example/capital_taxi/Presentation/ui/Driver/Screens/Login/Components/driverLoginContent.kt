package com.example.capital_taxi.Presentation.ui.Driver.Screens.Login.Components

import LoginRequest
import android.content.Context
import android.content.SharedPreferences
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.app.ui.theme.CustomFontFamily
import com.example.app.ui.theme.responsiveTextSize
import com.example.capital_taxi.Helper.PermissionViewModel
import com.example.capital_taxi.Helper.checkLocationPermission
import com.example.capital_taxi.Navigation.Destination
import com.example.capital_taxi.Presentation.Common.ForgetPassword
import com.example.capital_taxi.Presentation.Common.userMediaLoginOption
import com.example.capital_taxi.Presentation.Common.LoginForm
import com.example.capital_taxi.R

import com.google.firebase.Firebase
import com.google.firebase.database.database
import com.google.firebase.firestore.FirebaseFirestore

import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore
import com.google.firebase.messaging.FirebaseMessaging
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun driverLoginContent(

    navController: NavController
) {
    val permissionViewModel: PermissionViewModel = viewModel()


    val context = LocalContext.current

    // تأكد من التحقق من الصلاحية عند تحميل الشاشة
    LaunchedEffect(context) {
        checkLocationPermission(context, permissionViewModel)
    }

    val isLocationGranted by permissionViewModel.isLocationGranted.collectAsState()
    var loginError by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    // SharedPreferences for storing token
    val sharedPreferences: SharedPreferences = context.getSharedPreferences("your_prefs", Context.MODE_PRIVATE)
    val editor: SharedPreferences.Editor = sharedPreferences.edit()


    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    LaunchedEffect(isLoading) {
        if (isLoading) {
            val role = "driver"
            val request = LoginRequest(email, password, role)

            try {
                val response = LoginApiClient.loginApiService.loginuser(request)
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    val token = responseBody?.token
                    val userId = responseBody?.account?.userId

                    if (token != null && userId != null) {
                        editor.putString("driver_token", token)
                        editor.putString("driver_id", userId)
                        editor.apply()





                        Log.d("Login", "✅ تسجيل الدخول ناجح | Token: $token | UserID: $userId")
                        navController.navigate(Destination.DriverHomeScreen.route)
                    } else {
                        loginError = "🚨 لم يتم استلام التوكن أو معرف المستخدم من السيرفر"
                    }
                } else {
                    loginError = response.errorBody()?.string() ?: response.message()
                }
            } catch (e: Exception) {
                loginError = "⚠️ حدث خطأ أثناء تسجيل الدخول: ${e.localizedMessage}"
            } finally {
                isLoading = false
            }
        }
    }





    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())

            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {


        Text(
            text = stringResource(R.string.signin),
            fontSize = responsiveTextSize(fraction = 0.06f, minSize = 20.sp, maxSize = 32.sp),

            fontFamily = CustomFontFamily,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(40.dp))
        LoginForm(
            email = email,
            password = password,
            onEmailChange = { email = it },
            onPasswordChange = { password = it },
            passwordVisible = passwordVisible,
            onPasswordToggle = { passwordVisible = !passwordVisible }
        )

        Spacer(modifier = Modifier.height(16.dp))
        Box(modifier = Modifier.align(alignment = Alignment.End)) {
            ForgetPassword(navController)
        }
        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (isLocationGranted) {
                    // Start the login process when the button is clicked
                    isLoading = true
                } else {
                    navController.navigate(Destination.searchForLocation.route)
                }


            },
            modifier = Modifier
                .fillMaxWidth()


                .height(60.dp),
            colors = ButtonDefaults.buttonColors(colorResource(R.color.primary_color)),
            shape = RoundedCornerShape(8.dp)


        ) {
            Text(
                text = stringResource(R.string.signin),
                fontSize = responsiveTextSize(fraction = 0.06f, minSize = 14.sp, maxSize = 18.sp),


                fontFamily = CustomFontFamily,
                color = Color.Black
            )

        }

        // Show loading indicator while isLoading is true
        if (isLoading) {
            CircularProgressIndicator()
        }

        if (loginError != null) {
            // Show error message if login fails
            Text(
                text = loginError ?: "",
                color = Color.Red,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(60.dp))


        Text(
            text = stringResource(R.string.sign_in_with),
            color = Color.Black,
            fontSize = responsiveTextSize(fraction = 0.06f, minSize = 14.sp, maxSize = 20.sp),

            fontFamily = CustomFontFamily,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(40.dp))
        userMediaLoginOption()

        Spacer(modifier = Modifier.height(60.dp))

        // SignUp Text
        Row {
            Text(
                text = stringResource(id = R.string.Dont_have_an_account),
                fontSize = responsiveTextSize(fraction = 0.06f, minSize = 14.sp, maxSize = 20.sp),



                fontFamily = CustomFontFamily,
                )

            Spacer(modifier = Modifier.width(4.dp))

            Text(
                text = stringResource(id = R.string.SignUp),
                color = colorResource(R.color.primary_color),
                fontSize = responsiveTextSize(fraction = 0.06f, minSize = 14.sp, maxSize = 20.sp),



                fontFamily = CustomFontFamily,
                modifier = Modifier.clickable {
                    navController.navigate(Destination.driverSignUp.route)
                }
            )
        }
    }
}    // ✅ تحديث FCM Token في Firestore

@Composable
fun driverMapViewComposable(
    startPoint: GeoPoint? = GeoPoint(30.0444, 31.2357), // نقطة البداية (القاهرة)
    endPoint: GeoPoint? = GeoPoint(30.0626, 31.2497), // نقطة النهاية (حي مصر الجديدة)
    routePoints: List<GeoPoint>? = null,
) {
    val context = LocalContext.current
    val mapView = remember { MapView(context) }
    val handler = remember { Handler(Looper.getMainLooper()) }
    var currentPosition by remember { mutableStateOf(0) }
    val carMarker = remember { Marker(mapView) }

    // تعيين المصدر لأول مرة
    LaunchedEffect(mapView) {
        mapView.setTileSource(org.osmdroid.tileprovider.tilesource.TileSourceFactory.MAPNIK)
        mapView.controller.setZoom(15)
        mapView.controller.setCenter(startPoint ?: GeoPoint(30.033, 31.233))
    }

    // إضافة علامة السيارة
    LaunchedEffect(mapView) {
        carMarker.setIcon(ContextCompat.getDrawable(context, R.drawable.uber)) // أيقونة السيارة
        carMarker.position = startPoint
        mapView.overlays.add(carMarker)
    }

    // تحريك السيارة عبر المسار
    LaunchedEffect(routePoints) {
        routePoints?.let { points ->
            val runnable = object : Runnable {
                override fun run() {
                    if (currentPosition < points.size) {
                        carMarker.position = points[currentPosition]
                        mapView.controller.setCenter(points[currentPosition])
                        currentPosition++
                        handler.postDelayed(this, 1000) // تأخير 1 ثانية بين كل نقطة
                    }
                }
            }
            handler.post(runnable)
        }
    }

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { mapView },
        update = { mapView ->

            // إضافة نقطة البداية والنهاية
            startPoint?.let { start ->
                val startMarker = Marker(mapView)
                startMarker.position = start
                startMarker.title = "Start"
                mapView.overlays.add(startMarker)
            }

            endPoint?.let { end ->
                val endMarker = Marker(mapView)
                endMarker.position = end
                endMarker.title = "End"
                mapView.overlays.add(endMarker)
            }

            // إضافة المسار بين النقاط إذا كان موجودًا
            routePoints?.let { points ->
                val polyline = Polyline(mapView)
                polyline.setPoints(points)
                mapView.overlays.add(polyline)
            }

            // تحديث الخريطة عند تغيير النقاط
            startPoint?.let { mapView.controller.setCenter(it) }
            endPoint?.let { mapView.controller.setZoom(15) }
        }
    )
}

// وظيفة فك تشفير Polyline
fun decodePolyline(encoded: String): List<GeoPoint> {
    val polyline = mutableListOf<GeoPoint>()
    var index = 0
    val len = encoded.length
    var lat = 0
    var lng = 0

    while (index < len) {
        var shift = 0
        var result = 0
        while (true) {
            val byte = encoded[index++].toInt() - 63
            result = result or ((byte and 0x1f) shl shift)
            shift += 5
            if (byte < 0x20) break
        }
        val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
        lat += dlat

        shift = 0
        result = 0
        while (true) {
            val byte = encoded[index++].toInt() - 63
            result = result or ((byte and 0x1f) shl shift)
            shift += 5
            if (byte < 0x20) break
        }
        val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
        lng += dlng

        polyline.add(GeoPoint(lat / 1E5, lng / 1E5))
    }
    return polyline
}
