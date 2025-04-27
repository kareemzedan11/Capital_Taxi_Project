package com.example.capital_taxi.Presentation.ui.Passengar.Screens.Home.UserHome.Components.Waiting_for_the_driver

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.capital_taxi.Presentation.ui.Driver.Screens.Home.Components.updateTripStatus
import com.example.capital_taxi.Presentation.ui.Passengar.Screens.Home.UserHome.Components.FromLocationToDestination
import com.example.capital_taxi.R
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun TripDetailsBottomSheetContent(tripId: String,
                                  onCancelSuccess: () -> Unit = {},
                                  onCancelFailure: (String) -> Unit = {}) {
    var origin by remember { mutableStateOf("") }
    var destination by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }

    // جلب بيانات الرحلة من Firebase
    LaunchedEffect(tripId) {
        try {
            val tripDoc = FirebaseFirestore.getInstance()
                .collection("trips")
                .whereEqualTo("_id", tripId)
                .get()
                .await()

            if (!tripDoc.isEmpty) {
                val document = tripDoc.documents.first()
                origin = document.getString("origin") ?: "Unknown"
                destination = document.getString("destination") ?: "Unknown"
            }
        } catch (e: Exception) {
            // التعامل مع الأخطاء
            origin = "Error"
            destination = "Error"
        } finally {
            isLoading = false
        }
    }

    Column(
        modifier = Modifier.padding(horizontal = 10.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.padding(16.dp))
        } else {
            Text(
                stringResource(R.string.trip_details),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.padding(bottom = 10.dp))
            HorizontalDivider(Modifier.fillMaxWidth(), thickness = 2.dp)
            Spacer(modifier = Modifier.padding(bottom = 10.dp))

            // عرض مسار الرحلة
            FromLocationToDestination(
                origin = origin,
                destination = destination,
                onEditOrigin = { /* Handle origin edit */ },
                onEditDestination = { /* Handle destination edit */ },
                onAddStop = { /* Handle add stop */ }
            )

            Spacer(modifier = Modifier.padding(bottom = 20.dp))

            // قسم مشاركة حالة الرحلة
            TripSharingSection()

            Spacer(modifier = Modifier.padding(bottom = 20.dp))

            // أزرار الإجراءات
            ActionButtons(
                tripId = tripId,
                onCancelSuccess = onCancelSuccess,
                onCancelFailure = onCancelFailure
            )
        }
    }
}

@Composable
private fun TripSharingSection() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            stringResource(R.string.share_trip_status),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.weight(1f))
        Button(
            onClick = { /* Handle share */ },
            modifier = Modifier
                .border(
                    width = 2.dp,
                    color = Color.Gray,
                    shape = RoundedCornerShape(23.dp)
                ),
            colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)
        ) {
            Text(
                stringResource(R.string.share),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray
            )
        }
    }
    HorizontalDivider(Modifier.fillMaxWidth(), thickness = 2.dp)
}
@Composable
private fun ActionButtons(
    tripId: String,
    onCancelSuccess: () -> Unit = {},
    onCancelFailure: (String) -> Unit = {}
) {
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // زر إلغاء الرحلة
        Button(
            onClick = {
                scope.launch {
                    isLoading = true
                    try {
                        updateTripStatus(tripId, "Cancelled")
                        onCancelSuccess()
                    } catch (e: Exception) {
                        onCancelFailure(e.message ?: "Unknown error")
                    } finally {
                        isLoading = false
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray),
            enabled = !isLoading,
            shape = RoundedCornerShape(8.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = Color.Red,
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(20.dp)
                )
            } else {
                Text(
                    text = stringResource(R.string.cancel_trip),
                    color = Color.Red,
                    fontSize = 18.sp,
                )
            }
        }

        Spacer(modifier = Modifier.padding(bottom = 10.dp))

        // زر الإنهاء
        Button(
            onClick = { /* Handle done */ },
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = stringResource(R.string.Done_Button),
                color = Color.White,
                fontSize = 18.sp,
            )
        }
    }
}