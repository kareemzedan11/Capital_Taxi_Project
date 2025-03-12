package com.example.capital_taxi.Presentation.ui.Passengar.Screens.Register.Components

import RegisterDriver
import RegisterRequest
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.capital_taxi.Presentation.Common.All_Register_textFields
import com.example.capital_taxi.Presentation.Common.AlreadyHaveAccount
import com.example.capital_taxi.Presentation.Common.RegisterHeader
import com.example.capital_taxi.Presentation.Common.SignUpButton
import com.example.capital_taxi.Presentation.Common.TermsAndConditionsCheckbox
import com.example.capital_taxi.Presentation.Common.userMediaLoginOption
import com.example.capital_taxi.R


import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

@Composable
fun UserRegisterContent(navController: NavController) {
    val context = LocalContext.current

    val name = remember { mutableStateOf("") }
    val username = remember { mutableStateOf("") } // إضافة متغير `username`
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val confirmPassword = remember { mutableStateOf("") }
    val phone = remember { mutableStateOf("") }
    val isChecked = remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        RegisterHeader()
        Spacer(modifier = Modifier.height(16.dp))

        // تمرير القيم إلى الحقول مع `username`
        All_Register_textFields(
            name = name,
            username = username,
            email = email,
            password = password,
            confirmPassword = confirmPassword,
            phone = phone
        )

        Spacer(modifier = Modifier.height(16.dp))

        TermsAndConditionsCheckbox(
            isChecked = isChecked.value,
            onCheckedChange = { isChecked.value = it }
        )

        Spacer(modifier = Modifier.height(20.dp))

        SignUpButton(
            isEnabled = isChecked.value,
            onClick = {
                Log.d(
                    "RegisterDebug",
                    "Name: ${name.value}, Username: ${username.value}, Email: ${email.value}, Password: ${password.value}, Phone: ${phone.value}"
                )

                registerUser(
                    name.value,
                    username.value,
                    email.value,
                    password.value,
                    phone.value,
                    navController,
                    context
                )

            },
            text = R.string.SignUp
        )

        Spacer(modifier = Modifier.padding(30.dp))

        userMediaLoginOption()

        Spacer(modifier = Modifier.padding(22.dp))

        AlreadyHaveAccount(navController)
    }
}

fun registerUser(
    name: String,
    username: String,
    email: String,
    password: String,
    phone: String,
    navController: NavController,
    context: Context,
    isDriver: Boolean = false // تعيين `false` افتراضيًا ليكون مستخدم عادي، وإذا أردت سائق استخدم `true`
) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val role = if (isDriver) "driver" else "user" // تحديد الدور تلقائيًا
            val request = RegisterRequest(name, username, email, password, phone, role)
            println("📤 Sending request: $request")

            val response = ApiClient.authApiService.registerUser(request)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    val message = response.body()?.message ?: "Registration successful"
                    println("✅ Registration successful: $message")
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    navController.navigate("homeScreen")
                } else {
                    val errorResponse = response.errorBody()?.string() ?: "Unknown error"
                    println("❌ Registration failed: $errorResponse")
                    Toast.makeText(
                        context,
                        "Registration failed: $errorResponse",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                val errorMessage = e.localizedMessage ?: "An error occurred"
                println("❌ Exception Error: $errorMessage")
                Toast.makeText(context, "Error: $errorMessage", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
fun registerDriver(
    name: String,
    username: String,
    email: String,
    password: String,
    phone: String,
    navController: NavController,
    context: Context,

    nationalIdFront: File?,
    nationalIdBack: File?,
    licenseFront: File?,
    licenseBack: File?,
    carLicenseFront: File?,
    carLicenseBack: File?
) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            // تحويل النصوص إلى RequestBody
            val namePart = name.toRequestBody("text/plain".toMediaTypeOrNull())
            val usernamePart = username.toRequestBody("text/plain".toMediaTypeOrNull())
            val emailPart = email.toRequestBody("text/plain".toMediaTypeOrNull())
            val passwordPart = password.toRequestBody("text/plain".toMediaTypeOrNull())
            val phonePart = phone.toRequestBody("text/plain".toMediaTypeOrNull())
            val rolePart = "driver".toRequestBody("text/plain".toMediaTypeOrNull())

            // تحويل الملفات إلى MultipartBody.Part
            val nationalIdFrontPart = nationalIdFront?.toMultipart("nationalIdFront")
            val nationalIdBackPart = nationalIdBack?.toMultipart("nationalIdBack")
            val licenseFrontPart = licenseFront?.toMultipart("licenseFront")
            val licenseBackPart = licenseBack?.toMultipart("licenseBack")
            val carLicenseFrontPart = carLicenseFront?.toMultipart("carLicenseFront")
            val carLicenseBackPart = carLicenseBack?.toMultipart("carLicenseBack")



            val response = ApiClient.authApiService.registerDriver(
                namePart, usernamePart, emailPart, passwordPart, phonePart, rolePart,
                nationalIdFrontPart, nationalIdBackPart, licenseFrontPart, licenseBackPart, carLicenseFrontPart, carLicenseBackPart
            )


            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    val message = response.body()?.message ?: "Registration successful"
                    Log.d("RegisterDriver", "Name: $name, Email: $email, Phone: $nationalIdFrontPart")
                    Log.d("RegisterDriver", "Uploading file: ${nationalIdFront?.name}")

                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    navController.navigate("homeScreen")
                } else {
                    val errorResponse = response.errorBody()?.string() ?: "Unknown error"
                    Toast.makeText(context, "Registration failed: $errorResponse", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}


fun File.toMultipart(partName: String): MultipartBody.Part {
    val requestFile = this.asRequestBody("image/jpeg".toMediaTypeOrNull())
    return MultipartBody.Part.createFormData(partName, this.name, requestFile)
}

//fun registerUser(
//    name: String,
//    username: String,
//    email: String,
//    password: String,
//    phone: String,
//    navController: NavController,
//    context: Context
//) {
//    CoroutineScope(Dispatchers.IO).launch {
//        val role = "user"
//        try {
//            val response = ApiClient.authApiService.registerUser(
//                name, username, email, password, phone,role
//            )
//
//            withContext(Dispatchers.Main) {
//                if (response.isSuccessful) {
//                    val message = response.body()?.message ?: "User registration successful"
//                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
//                    navController.navigate("homeScreen")
//                } else {
//                    val errorResponse = response.errorBody()?.string() ?: "Unknown error"
//                    Toast.makeText(context, "User registration failed: $errorResponse", Toast.LENGTH_SHORT).show()
//                }
//            }
//        } catch (e: Exception) {
//            withContext(Dispatchers.Main) {
//                Toast.makeText(context, "Error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
//}
