package com.example.capital_taxi.domain.shared

import kotlinx.serialization.Serializable
import com.google.gson.annotations.SerializedName

data class RegisterResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String,

@SerializedName("token") val token: String? // إضافة التوكين


)


data class LoginResponse(
    val token: String,
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("account") val account: Account
)

data class Account(
    @SerializedName("id") val userId: String,
    @SerializedName("balance") val balance: Double
)
