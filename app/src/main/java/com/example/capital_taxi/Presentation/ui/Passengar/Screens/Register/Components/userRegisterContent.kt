package com.example.capital_taxi.Presentation.ui.Passengar.Screens.Register.Components

import RegisterDriver
import RegisterRequest
import android.content.Context
import android.net.Uri
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
import com.example.capital_taxi.Navigation.Destination
import com.example.capital_taxi.Presentation.Common.All_Register_textFields
import com.example.capital_taxi.Presentation.Common.AlreadyHaveAccount
import com.example.capital_taxi.Presentation.Common.RegisterHeader
import com.example.capital_taxi.Presentation.Common.SignUpButton
import com.example.capital_taxi.Presentation.Common.TermsAndConditionsCheckbox
import com.example.capital_taxi.Presentation.Common.userMediaLoginOption
import com.example.capital_taxi.R
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.GoTrue
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.storage


import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import kotlin.time.Duration

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
    isDriver: Boolean = false
) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            // 1. تسجيل المستخدم في API
            val role = if (isDriver) "driver" else "user"
            val request = RegisterRequest(name, username, email, password, phone, role)
            println("📤 Sending request: $request")

            val response = ApiClient.authApiService.registerUser(request)

            if (response.isSuccessful) {
                val message = response.body()?.message ?: "Registration successful"
                println("✅ Registration successful: $message")

                // 2. تخزين البيانات في Firestore
                val userData = hashMapOf(
                    "name" to name,
                    "username" to username,
                    "email" to email,
                    "phone" to phone,
                    "role" to role,
                    "id" to "" // سيتم تحديثه لاحقًا
                )

                val db = FirebaseFirestore.getInstance()
                val documentReference = db.collection("users").add(userData).await()
                println("📝 Firebase document added with ID: ${documentReference.id}")

                // 3. تحديث حقل الـ ID بالقيمة الجديدة
                documentReference.update("id", documentReference.id).await()
                println("🆔 ID updated successfully in Firestore")

                withContext(Dispatchers.Main) {
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    navController.navigate(Destination.UserLogin.route)
                }
            } else {
                val errorResponse = response.errorBody()?.string() ?: "Unknown error"
                println("❌ Registration failed: $errorResponse")
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context,
                        "Registration failed: $errorResponse",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } catch (e: Exception) {
            val errorMessage = e.localizedMessage ?: "An error occurred"
            println("❌ Exception Error: $errorMessage")
            withContext(Dispatchers.Main) {
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

object SupabaseManager {
    private const val    supabaseUrl = "https://mwncdoelxuwhtlrvtnap.supabase.co"

    private const val   supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im13bmNkb2VseHV3aHRscnZ0bmFwIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDUwMjU4NjUsImV4cCI6MjA2MDYwMTg2NX0.f5Zlz_WSLypyCUn67g2PEA5ZjHa8VsqjJDbxIgtBBTk"

    val client: SupabaseClient by lazy {
        createSupabaseClient(
            supabaseUrl = supabaseUrl,
            supabaseKey = supabaseKey
        ) {
            install(Storage)
            install(GoTrue)
        }
    }
}
suspend fun uploadFileToSupabase(
    file: File?,
    bucketName: String,
    filePath: String
): String? {
    if (file == null || !file.exists()) return null

    return try {  val fileBytes = file.readBytes()

        withContext(Dispatchers.Main) {
            SupabaseManager.client.storage
                .from(bucketName)
                .upload(
                    path = filePath,
                    data = fileBytes,
                    upsert = true
                )

            SupabaseManager.client.storage
                .from(bucketName)
                .createSignedUrl(
                    path = filePath,
                    expiresIn = Duration.parse("P7D")
                ).toString()
        }

    } catch (e: Exception) {
        Log.e("SupabaseUpload", "Error: ${e.message}")
        null
    }
}


fun sendDriverData(
    name: String,
    username: String,
    email: String,
    phone: String,
    carType: String,
    carNumber: String,
    profile: File?,

    nationalIdFront: File?,
    nationalIdBack: File?,
    licenseFront: File?,
    licenseBack: File?,
    carLicenseFront: File?,
    carLicenseBack: File?,
    fareRate: Double = 0.0,
    carModel: String = "",
    carColor: String = "",
    rating: Double = 5.0,
    balance: Double = 0.0,
    totalEarnings: Double = 0.0,
    trips: Int = 0,
    hours: Int = 0,
    context: Context
) {
    val db = FirebaseFirestore.getInstance()

    // 1. إنشاء بيانات السائق الأساسية في Firestore
    val driverData = hashMapOf(
        "id" to "",
        "name" to name,
        "username" to username,
        "email" to email,
        "phone" to phone,
        "carType" to carType,
        "carNumber" to carNumber,
        "carModel" to carModel,
        "carColor" to carColor,
        "fareRate" to fareRate,
        "rating" to rating,
        "balance" to balance,
        "totalEarnings" to totalEarnings,
        "trips" to trips,
        "hours" to hours,
        "fareLastUpdated" to FieldValue.serverTimestamp(),
        "createdAt" to FieldValue.serverTimestamp(),
        "status" to "pending",
        "documentsUploaded" to false
    )

    db.collection("drivers")
        .add(driverData)
        .addOnSuccessListener { documentRef ->
            val driverId = documentRef.id

            // 2. تحديث حقل ID
            db.collection("drivers").document(driverId)
                .update("id", driverId)
                .addOnSuccessListener {

                    // 3. رفع المستندات إلى Supabase (باستخدام Coroutine)
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            val uploadResults = listOf(
                                uploadFileToSupabase(profile, "driver-documents", "$driverId/profile.jpg"),

                                uploadFileToSupabase(nationalIdFront, "driver-documents", "$driverId/nationalIdFront.jpg"),
                                uploadFileToSupabase(nationalIdBack, "driver-documents", "$driverId/nationalIdBack.jpg"),
                                uploadFileToSupabase(licenseFront, "driver-documents", "$driverId/licenseFront.jpg"),
                                uploadFileToSupabase(licenseBack, "driver-documents", "$driverId/licenseBack.jpg"),
                                uploadFileToSupabase(carLicenseFront, "driver-documents", "$driverId/carLicenseFront.jpg"),
                                uploadFileToSupabase(carLicenseBack, "driver-documents", "$driverId/carLicenseBack.jpg")
                            )

                            // 4. تحديث Firestore بروابط المستندات
                            val updates = hashMapOf<String, Any>(
                                "nationalIdFrontUrl" to (uploadResults[0] ?: ""),
                                "nationalIdBackUrl" to (uploadResults[1] ?: ""),
                                "licenseFrontUrl" to (uploadResults[2] ?: ""),
                                "licenseBackUrl" to (uploadResults[3] ?: ""),
                                "carLicenseFrontUrl" to (uploadResults[4] ?: ""),
                                "carLicenseBackUrl" to (uploadResults[5] ?: ""),
                                "documentsUploaded" to true
                            )

                            db.collection("drivers").document(driverId)
                                .update(updates)
                            withContext(Dispatchers.Main) {
                                Toast.makeText(context, "تم الحفظ بنجاح", Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: Exception) {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(context, "خطأ: ${e.message}", Toast.LENGTH_LONG).show()
                            }
                        }


                    }
                }
        }
        .addOnFailureListener { e ->
            Toast.makeText(
                context,
                "خطأ في حفظ البيانات الأساسية: ${e.message}",
                Toast.LENGTH_SHORT
            ).show()
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
