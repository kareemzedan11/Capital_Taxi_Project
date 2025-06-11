package com.example.capital_taxi.Presentation.ui.shared.OTP.Components

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.firestore
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import java.util.concurrent.TimeUnit


interface NotiFireApi {
    @POST("send/message")
    suspend fun sendMessage(@Body body: MessageBody): Response<NotiFireResponse>
}

data class MessageBody(
    @SerializedName("device_id") val deviceId: String,
    val to: String,
    val message: String
)

data class NotiFireResponse(
    val status: Boolean,
    val data: Data?
)

data class Data(
    val success: Boolean,
    val message: String
)
class OtpViewModel : ViewModel() {
    private val db = Firebase.firestore

    private val _otpState = MutableStateFlow<OtpState>(OtpState.Idle)
    val otpState: StateFlow<OtpState> = _otpState

    fun sendOtp(phone: String) {
        viewModelScope.launch {
            _otpState.value = OtpState.Loading

            val otp = (1000..9999).random().toString()
            val message = "Your OTP is: $otp"

            try {
                val retrofit = Retrofit.Builder()
                    .baseUrl("https://noti-fire.com/api/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

                val api = retrofit.create(NotiFireApi::class.java)
                val response = api.sendMessage(
                    MessageBody(
                        deviceId = "df733e19-50ac-4db4-abc6-a642404fe408",
                        to = phone,
                        message = message
                    )
                )

                if (response.isSuccessful && response.body()?.data?.success == true) {
                    val expiry = System.currentTimeMillis() + 5 * 60 * 1000
                    db.collection("otp").document(phone).set(
                        mapOf("otp" to otp, "expires" to expiry)
                    ).await()

                    _otpState.value = OtpState.CodeSent
                } else {
                    _otpState.value = OtpState.Error("Failed to send OTP")
                }
            } catch (e: Exception) {
                _otpState.value = OtpState.Error("Something went wrong: ${e.message}")
            }
        }
    }

    fun resetState() {
        _otpState.value = OtpState.Idle
    }
    fun verifyOtp(
        phone: String,
        enteredCode: String,
        onResult: (Boolean) -> Unit
    ) {
        _otpState.value = OtpState.Loading

        val formattedPhone = if (!phone.startsWith("+")) "+2$phone" else phone
        Firebase.firestore.collection("otp").document(formattedPhone)
            .get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    val otpFromFirestore = doc.get("otp").toString().trim()
                    val enteredOtp = enteredCode.trim()
                    val expires = doc.getLong("expires") ?: 0L
                    val currentTime = System.currentTimeMillis()

                    // طباعة البيانات للتأكد
                    Log.d("OTP_DEBUG", "Phone: $formattedPhone")
                    Log.d("OTP_DEBUG", "OTP from Firestore: $otpFromFirestore")
                    Log.d("OTP_DEBUG", "Entered OTP: $enteredOtp")
                    Log.d("OTP_DEBUG", "Expires at: $expires")
                    Log.d("OTP_DEBUG", "Current time: $currentTime")

                    if (currentTime > expires) {
                        Log.d("OTP_DEBUG", "OTP has expired")
                        _otpState.value = OtpState.Error("OTP has expired")
                        onResult(false)
                        return@addOnSuccessListener
                    }

                    if (enteredOtp == otpFromFirestore) {
                        Log.d("OTP_DEBUG", "OTP matched successfully!")
                        _otpState.value = OtpState.Success
                        onResult(true)
                    } else {
                        Log.d("OTP_DEBUG", "OTP does not match")
                        _otpState.value = OtpState.Error("Invalid OTP")
                        onResult(false)
                    }

                } else {
                    Log.d("OTP_DEBUG", "No OTP document found for $formattedPhone")
                    _otpState.value = OtpState.Error("No OTP found")
                    onResult(false)
                }
            }
            .addOnFailureListener { e ->
                Log.e("OTP_DEBUG", "Failed to get OTP: ${e.message}")
                _otpState.value = OtpState.Error("Something went wrong")
                onResult(false)
            }
    }
}


sealed class OtpState {
    object Idle : OtpState()
    object Loading : OtpState()
    object CodeSent : OtpState()
    object Success : OtpState()
    data class Error(val message: String) : OtpState()
}