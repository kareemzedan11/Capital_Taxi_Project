package com.example.capital_taxi.Helper.User_Image

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64

fun decodeBase64ToImage(encodedImage: String): Bitmap? {
    return try {
        val decodedString = Base64.decode(encodedImage, Base64.DEFAULT)
        BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun getImageFromSharedPreferences(context: Context): Uri? {
    val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("user_preferences", Context.MODE_PRIVATE)
    val savedImageUri = sharedPreferences.getString("user_photo", null)
    return savedImageUri?.let { Uri.parse(it) }
}

