package com.example.capital_taxi.Presentation.ui.Driver.Components
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.capital_taxi.R
import com.example.capital_taxi.domain.driver.model.DriverStatusViewModel
import com.google.android.gms.location.LocationServices
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.osmdroid.util.GeoPoint

@Composable
fun DriverControls(
    driverId: String,
    tripLocation: GeoPoint?,
    onClick: () -> Unit,
    onClick2: () -> Unit,

    modifier: Modifier = Modifier
) {
    val viewModel: DriverStatusViewModel = viewModel()
    val isOnline by viewModel.isOnline
    val isLoading by viewModel.isLoading
    var showConfirmationDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Function to update driver status in Firestore
    fun updateDriverStatusInFirestore(isOnline: Boolean) {
        scope.launch(Dispatchers.IO) {
            try {
                val firestore = FirebaseFirestore.getInstance()
                val updateMap = mapOf(
                    "isOnline" to isOnline,
                    "lastStatusUpdate" to FieldValue.serverTimestamp()
                )

                // ابحث عن مستند السائق
                val snapshot = firestore.collection("drivers")
                    .whereEqualTo("id", driverId)
                    .get()
                    .await()

                val docRef = snapshot.documents.firstOrNull()?.reference

                if (docRef != null) {
                    docRef.set(updateMap, SetOptions.merge()).await()
                    Log.d("Firestore", "Driver status updated to ${if (isOnline) "Online" else "Offline"}")

                    // إذا تحول إلى Offline، احذف موقعه
                    if (!isOnline) {
                        FirebaseDatabase.getInstance()
                            .getReference("drivers")
                            .child(driverId)
                            .child("location")
                            .removeValue()
                            .await()
                        Log.d("RealtimeDB", "Location removed for offline driver")
                    }
                } else {
                    Log.e("Firestore", "❌ لم يتم العثور على مستند السائق لتحديث حالته")
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "تعذر العثور على السائق", Toast.LENGTH_LONG).show()
                    }
                }

            } catch (e: Exception) {
                Log.e("Firestore", "Error updating driver status", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context,
                        "فشل في تحديث الحالة: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    // Function for location updates (only when online)
    fun updateDriverLocation(driverId: String, location: GeoPoint) {
        if (!isOnline) {
            Log.d("LocationUpdate", "Skipping location update - driver is offline")
            return
        }

        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            scope.launch(Dispatchers.IO) {
                try {
                    // Update in Realtime Database
                    val database = FirebaseDatabase.getInstance()
                        .getReference("drivers")
                        .child(driverId)

                    val locationMap = mapOf(
                        "latitude" to location.latitude,
                        "longitude" to location.longitude,
                        "lastUpdate" to System.currentTimeMillis()
                    )

                    database.child("location").setValue(locationMap).await()

                    Log.d("RealtimeDB", "Location stored successfully")
                } catch (e: Exception) {
                    Log.e("RealtimeDB", "Failed to store location", e)
                }
            }
        } else {
            Log.e("Permission", "Location permission not granted")
        }
    }


    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            modifier = Modifier.size(26.dp),
            painter = painterResource(R.drawable.note),
            contentDescription = null,
            tint = colorResource(R.color.Icons_color)
        )

        Spacer(modifier = Modifier.weight(1f))

        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(40.dp),
                color = colorResource(R.color.primary_color),
                strokeWidth = 3.dp)
        } else {
            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(
                        if (isOnline) R.color.primary_color
                        else R.color.offline
                    )
                ),
                onClick = {
                    if (isOnline) {
                        showConfirmationDialog = true
                    } else {
                        scope.launch {
                            val firestore = FirebaseFirestore.getInstance()
                            try {
                                val result = firestore.collection("drivers")
                                    .whereEqualTo("id", driverId)
                                    .get()
                                    .await()

                                if (!result.isEmpty) {
                                    val doc = result.documents.first()
                                    val status = doc.getString("status")

                                    if (status != "active") {
                                        Toast.makeText(
                                            context,
                                            "You are currently not eligible to go online. Your status is $status",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    } else {
                                        // Eligible to go online
                                        onClick()
                                        viewModel.setLoading(true) // شغّل اللودينج الأول

                                        updateDriverStatusInFirestore(true) // حدّث في الفايرستور

                                        viewModel.setOnlineStatus(true) // وبعد ما تتأكد إنه Online، حدّث قيمة online

                                        tripLocation?.let {
                                            updateDriverLocation(driverId, it)
                                        }

                                        viewModel.setLoading(false) // وقفل اللودينج


                                    }

                                } else {
                                    showDialog(context, "Error", "Driver not found.")
                                }
                            } catch (e: Exception) {
                                Log.e("Firestore", "Error checking status", e)
                                showDialog(context, "Error", "Could not verify your status.")
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth(0.4f)
                    .fillMaxHeight(0.8f)
            ) {
                Text(
                    text = if (isOnline) "Online" else "Offline",
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Icon(
            tint = colorResource(R.color.Icons_color),
            modifier = Modifier.size(26.dp),
            painter = painterResource(R.drawable.tools),
            contentDescription = null
        )
    }
    if (showConfirmationDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmationDialog = false },
            title = { Text("Confirmation") },
            text = { Text("Do you want to switch to Offline?") },

            confirmButton = {
                TextButton(onClick = {
                    showConfirmationDialog = false
                    scope.launch {
                        viewModel.setLoading(true)
                        updateDriverStatusInFirestore(false)
                     onClick2()
                        viewModel.setOnlineStatus(false)
                        viewModel.setLoading(false)
                    }
                }) {
                    Text("Yes")
                }
            }
,
                    dismissButton = {
                TextButton(onClick = {
                    showConfirmationDialog = false
                }) {
                    Text("No")
                }
            }
        )
    }

}
fun showDialog(context: Context, title: String, message: String) {
    android.app.AlertDialog.Builder(context)
        .setTitle(title)
        .setMessage(message)
        .setPositiveButton("OK", null)
        .show()
}