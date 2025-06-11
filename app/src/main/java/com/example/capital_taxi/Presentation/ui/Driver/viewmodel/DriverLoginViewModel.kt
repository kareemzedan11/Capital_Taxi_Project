package com.example.capital_taxi.Presentation.ui.Driver.viewmodel
import LoginRequest
import android.app.Application
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import retrofit2.HttpException

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue

import kotlinx.coroutines.tasks.await
import org.json.JSONObject
import java.io.IOException

class DriverLoginViewModel @Inject constructor(
    private val context: Application
) : AndroidViewModel(context) {

    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var passwordVisible by mutableStateOf(false)

    var isLoading by mutableStateOf(false)
    var loginError by mutableStateOf<String?>(null)

    private val _loginSuccess = MutableStateFlow(false)
    val loginSuccess: StateFlow<Boolean> = _loginSuccess

    fun onLogin(navController: NavController) {
        viewModelScope.launch {
            isLoading = true
            loginError = null

            val role = "driver"
            val request = LoginRequest(email, password, role)

            try {
                val response = LoginApiClient.loginApiService.loginuser(request)

                if (response.isSuccessful) {
                    val responseBody = response.body()
                    val token = responseBody?.token
                    val userId = responseBody?.account?.userId

                    if (token != null && userId != null) {
                        val sharedPreferences = context.getSharedPreferences("your_prefs", Context.MODE_PRIVATE)
                        sharedPreferences.edit().apply {
                            putString("driver_token", token)
                            putString("driver_id", userId)
                            putString("user_type", "driver")
                            apply()
                        }

                        val driverDocsPref = context.getSharedPreferences("DriverDocuments", Context.MODE_PRIVATE)
                        val updates = hashMapOf<String, Any>(
                            "id" to userId,
                            "updatedAt" to FieldValue.serverTimestamp()
                        )

                        val db = FirebaseFirestore.getInstance()
                        val driverRef = db.collection("drivers")
                            .whereEqualTo("email", email)
                            .get()
                            .await()

                        if (!driverRef.isEmpty) {
                            val driverDoc = driverRef.documents[0]
                            driverDoc.reference.update(updates)
                                .addOnSuccessListener {
                                    driverDocsPref.edit().clear().apply()
                                    _loginSuccess.value = true
                                }
                                .addOnFailureListener {
                                    loginError = "Failed to update driver data. Please try again."
                                }
                        } else {
                            loginError = "No driver account found with this email."
                        }
                    } else {
                        loginError = "Internal error: Missing token or user ID in response."
                    }

                } else {
                    val errorString = response.errorBody()?.string()
                    loginError = if (!errorString.isNullOrBlank()) {
                        try {
                            val message = JSONObject(errorString).optString("message", "Login failed.")
                            when {
                                message.contains("Invalid credential", ignoreCase = true) -> "Incorrect email or password."
                                else -> message
                            }
                        } catch (e: Exception) {
                            "Error while parsing server error message."
                        }
                    } else {
                        "Login failed. Please try again: ${response.message()}"
                    }
                }

            } catch (e: IOException) {
                loginError = "No internet connection. Please check your network and try again."
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                loginError = if (!errorBody.isNullOrBlank()) {
                    try {
                        val json = JSONObject(errorBody)
                        val message = json.optString("message", "Login failed.")
                        when {
                            message.contains("Invalid credential", ignoreCase = true) -> "Incorrect email or password."
                            else -> message
                        }
                    } catch (ex: Exception) {
                        "Unexpected error while processing server response."
                    }
                } else {
                    "Failed to connect to server. Please try again later."
                }
            } catch (e: Exception) {
                loginError = "An error occurred during login: ${e.localizedMessage ?: "Unknown error"}"
            } finally {
                isLoading = false
            }
        }
    }
}
