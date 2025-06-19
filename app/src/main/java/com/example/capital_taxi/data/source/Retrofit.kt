package com.example.capital_taxi.data.source

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

object ApiClient {
    private val retrofit = Retrofit.Builder()
        .baseUrl("http://10.0.2.2:5000/api/users/") // غيرها حسب السيرفر
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val api: UserApiService = retrofit.create(UserApiService::class.java)
}
interface UserApiService {
    @POST("forgot-password")
    suspend fun forgotPassword(@Body body: EmailRequest): Response<ApiResponse>

    @POST("reset-password")
    suspend fun resetPassword(@Body body: ResetPasswordRequest): Response<ApiResponse>
}
data class EmailRequest(
    val email: String
)

data class ResetPasswordRequest(
    val email: String,
    val resetCode: String,
    val newPassword: String
)

data class ApiResponse(
    val message: String,
    val error: String? = null
)
class AuthViewModel : ViewModel() {

    var loading by mutableStateOf(false)
    var message by mutableStateOf("")
    var error by mutableStateOf("")

    fun sendResetCode(email: String) {
        viewModelScope.launch {
            loading = true
            try {
                val response = ApiClient.api.forgotPassword(EmailRequest(email))
                if (response.isSuccessful) {
                    message = response.body()?.message ?: "Code sent."
                } else {
                    error = response.errorBody()?.string() ?: "Failed to send code"
                }
            } catch (e: Exception) {
                error = e.message ?: "Error sending reset code"
            } finally {
                loading = false
            }
        }
    }

    fun resetPassword(email: String, code: String, newPass: String) {
        viewModelScope.launch {
            loading = true
            try {
                val response = ApiClient.api.resetPassword(
                    ResetPasswordRequest(email, code, newPass)
                )
                if (response.isSuccessful) {
                    message = response.body()?.message ?: "Password reset."
                } else {
                    error = response.errorBody()?.string() ?: "Reset failed"
                }
            } catch (e: Exception) {
                error = e.message ?: "Error resetting password"
            } finally {
                loading = false
            }
        }
    }
}
