package com.example.capital_taxi.Helper

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.core.content.ContextCompat


class PermissionViewModel : ViewModel() {
    private val _isLocationGranted = MutableStateFlow(false)
    val isLocationGranted: StateFlow<Boolean> get() = _isLocationGranted

    fun updateLocationPermission(isGranted: Boolean) {
        Log.d("PermissionViewModel", "Location Permission Granted: $isGranted")
        _isLocationGranted.value = isGranted
    }
}

fun requestLocationPermission(context: Context) {
    val locationPermissionLauncher =
        (context as? ComponentActivity)?.registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                // تعامل مع الإذن إذا تم منحه
                Toast.makeText(context, "Location permission granted", Toast.LENGTH_SHORT).show()
            } else {
                // إذا تم رفض الإذن، اعرض رسالة
                Toast.makeText(context, "Location permission is required", Toast.LENGTH_SHORT).show()

                // عرض زر لفتح الإعدادات لتمكين الأذونات يدويًا
                showPermissionSettings(context)
            }
        }

    locationPermissionLauncher?.launch(Manifest.permission.ACCESS_FINE_LOCATION)
}
fun checkLocationPermission(context: Context, permissionViewModel: PermissionViewModel) {
    if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
        permissionViewModel.updateLocationPermission(true)
    } else {
        permissionViewModel.updateLocationPermission(false)
    }
}
fun showPermissionSettings(context: Context) {
    try {

        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.parse("package:${context.packageName}")
        }
        context.startActivity(intent)
    } catch (e: Exception) {

        Toast.makeText(context, "Unable to open settings", Toast.LENGTH_SHORT).show()
    }
}