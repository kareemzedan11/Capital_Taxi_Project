package com.example.capital_taxi.Presentation.ui.Driver.Screens.Home.Components

import android.annotation.SuppressLint
import android.media.AudioFormat
import android.media.MediaRecorder
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.TabRowDefaults.Divider
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.capital_taxi.R
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.delay
import android.media.AudioRecord
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

@Composable
fun StartTrip(tripId:String,TripEnd:()->Unit) {
    val backgroundColor = colorResource(id = R.color.secondary_color)
    val primaryColor = colorResource(id = R.color.primary_color)
    val Icons_color = colorResource(id = R.color.Icons_color)

    var isLoading by remember { mutableStateOf(false) }
    var tripStarted by remember { mutableStateOf(false) }
    var triggerStart by remember { mutableStateOf(false) }
    var showToast by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // ده اللي بيعمل delay لما الزر يتضغط
    LaunchedEffect(triggerStart) {
        if (triggerStart) {
            isLoading = true
            delay(2000) // مدة التحميل
            isLoading = false
            tripStarted = true
            triggerStart = false // نرجّعها تاني عشان نقدر نضغط الزر مرة تانية لو حبيت


            //  showToast = true  // عرض الرسالة بعد بدء الرحلة
          //  startRecording() // بدء التسجيل الصوتي هنا
        }
    }

    // استخدام MediaRecorder لبدء التسجيل الصوتي

    // دالة لإرسال الصوت إلى Firebase في الوقت الفعلي


    Box(modifier = Modifier.fillMaxSize()) {
        Card(
            elevation = CardDefaults.cardElevation(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .align(Alignment.BottomCenter)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .background(backgroundColor),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Ride in Progress",
                        color = primaryColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text("16 min", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.padding(horizontal = 10.dp))
                    Text("-", fontSize = 30.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.padding(horizontal = 10.dp))
                    Text("2.3km", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                }

                Divider(
                    color = Color.LightGray,
                    thickness = 1.dp,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = "Dropping off",
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Rebbeca",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    Button(
                        onClick = {
                            if (!isLoading) {
                                if (!tripStarted) {
                                    // فحص المسافة قبل بدء الرحلة
                                    CoroutineScope(Dispatchers.IO).launch {
                                        val tripDoc = FirebaseFirestore.getInstance()
                                            .collection("trips")
                                            .whereEqualTo("_id", tripId)
                                            .get()
                                            .await()

                                        if (!tripDoc.isEmpty) {
                                            val document = tripDoc.documents.first()

                                            val driverLocationMap = document.get("driverLocation") as? Map<*, *>
                                            val driverLat = driverLocationMap?.get("latitude") as? Double
                                            val driverLng = driverLocationMap?.get("longitude") as? Double

                                            val originMap = document.get("originMap") as? Map<*, *>
                                            val originLat = originMap?.get("lat") as? Double
                                            val originLng = originMap?.get("lng") as? Double

                                            if (driverLat != null && driverLng != null && originLat != null && originLng != null) {
                                                val distance = calculateDistance(driverLat, driverLng, originLat, originLng)

                                                if (distance <= 5) {
                                                    triggerStart = true
                                                    updateTripStatus(tripId, "Started")
                                                } else {
                                                    withContext(Dispatchers.Main) {
                                                        Toast.makeText(context, "You are not at the pickup point", Toast.LENGTH_SHORT).show()
                                                    }
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    CoroutineScope(Dispatchers.IO).launch {
                                        val tripDoc = FirebaseFirestore.getInstance()
                                            .collection("trips")
                                            .whereEqualTo("_id", tripId)
                                            .get()
                                            .await()

                                        if (!tripDoc.isEmpty) {
                                            val document = tripDoc.documents.first()

                                            val driverLocationMap =
                                                document.get("driverLocation") as? Map<*, *>
                                            val driverLat =
                                                driverLocationMap?.get("latitude") as? Double
                                            val driverLng =
                                                driverLocationMap?.get("longitude") as? Double

                                            val destinationMap =
                                                document.get("destinationMap") as? Map<*, *>
                                            val destLat = destinationMap?.get("lat") as? Double
                                            val destLng = destinationMap?.get("lng") as? Double

                                            if (driverLat != null && driverLng != null && destLat != null && destLng != null) {
                                                val distance = calculateDistance(
                                                    driverLat,
                                                    driverLng,
                                                    destLat,
                                                    destLng
                                                )

                                                if (distance <= 5) {
                                                    updateTripStatus(tripId, "Completed")
                                                    withContext(Dispatchers.Main) {
                                                        TripEnd()
                                                    }
                                                } else {
                                                    withContext(Dispatchers.Main) {
                                                        Toast.makeText(
                                                            context,
                                                            "You are not at the drop-off point",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }}

                            },
                        modifier = Modifier.height(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = primaryColor,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(8.dp),
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = if (tripStarted) "End Trip" else "Start Trip",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }

    // الرسالة التنبيهية عند بدء الرحلة
    if (showToast) {
        Toast.makeText(LocalContext.current, "بدأنا تسجيل الصوت حفاظًا على سلامة الرحلة", Toast.LENGTH_SHORT).show()
        showToast = false
    }
}

// كود بدء التسجيل الصوتي وإرسال البيانات إلى Firebase
@SuppressLint("MissingPermission")
fun startRecording() {
    // إعداد AudioRecord (أفضل من MediaRecorder)
    val bufferSize = AudioRecord.getMinBufferSize(44100, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT)
    val audioRecord = AudioRecord(
        MediaRecorder.AudioSource.MIC,
        44100,
        AudioFormat.CHANNEL_IN_MONO,
        AudioFormat.ENCODING_PCM_16BIT,
        bufferSize
    )

    val outputStream = ByteArrayOutputStream()

    val buffer = ByteArray(bufferSize)
    audioRecord.startRecording()

    // دالة لقراءة الصوت وإرساله إلى Firebase
    Thread {
        while (true) {
            val bytesRead = audioRecord.read(buffer, 0, buffer.size)
            if (bytesRead > 0) {
                outputStream.write(buffer, 0, bytesRead)

                // إرسال البيانات الصوتية إلى Firebase
                sendAudioToFirebase(outputStream.toByteArray())

                // مسح البيانات المؤقتة
                outputStream.reset()
            }
        }
    }.start()

    // بمجرد ما تنتهي من التسجيل
    // audioRecord.stop()
}

fun sendAudioToFirebase(audioData: ByteArray) {
    val databaseRef = FirebaseDatabase.getInstance().getReference("live_audio/12222")
    // إرسال البيانات الصوتية إلى Firebase
    databaseRef.push().setValue(audioData)
}