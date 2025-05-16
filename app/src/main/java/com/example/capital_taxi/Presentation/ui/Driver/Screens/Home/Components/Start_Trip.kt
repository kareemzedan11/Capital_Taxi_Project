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
import android.util.Base64
import android.util.Log
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.DisposableEffect
import com.example.capital_taxi.data.utils.DirectionsPrefs
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.ByteArrayOutputStream
import java.io.IOException

@Composable
fun StartTrip(tripId:String,TripEnd:()->Unit,driverId:String,totalFare:Double) {
    val backgroundColor = colorResource(id = R.color.secondary_color)
    val primaryColor = colorResource(id = R.color.primary_color)
    val Icons_color = colorResource(id = R.color.Icons_color)

    var isLoading by remember { mutableStateOf(false) }
    var tripStarted by remember { mutableStateOf(false) }
    var triggerStart by remember { mutableStateOf(false) }
    var showToast by remember { mutableStateOf(false) }
    var destination by remember { mutableStateOf("") }
    var distanceInKm by remember { mutableStateOf(0.0)  }
    var Time by remember { mutableStateOf(0L) }

    var isDataLoading by remember { mutableStateOf(true) }
    val context = LocalContext.current

    // ده اللي بيعمل delay لما الزر يتضغط
    LaunchedEffect(triggerStart) {
        if (triggerStart) {
            isLoading = true
            delay(2000) // مدة التحميل
            isLoading = false
            tripStarted = true
            triggerStart = false // نرجّعها تاني عشان نقدر نضغط الزر مرة تانية لو حبيت




        }
    }
    DisposableEffect(tripId) {
        isDataLoading = true

        val firestore = FirebaseFirestore.getInstance()
        val query = firestore.collection("trips").whereEqualTo("_id", tripId)

        val listenerRegistration = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                isDataLoading = false
                return@addSnapshotListener
            }

            if (snapshot != null && !snapshot.isEmpty) {
                val document = snapshot.documents.first()
                destination = document.get("destination") as? String ?: ""
                distanceInKm = document.get("distanceInKm") as? Double ?: 0.0
                Time = document.get("time") as? Long ?: 0
            }

            isDataLoading = false
        }

        onDispose {
            listenerRegistration.remove()
        }
    }
    fun formatMillisecondsWithSeconds(milliseconds: Long): String {
        val seconds = milliseconds / 1000
        val minutes = seconds / 60
        val hours = minutes / 60

        return if (hours > 0) {
            "${hours}h ${minutes % 60}m  "  // مثال: 1h 48m 22s
        } else if (minutes > 0) {
            "${minutes}m "  // مثال: 108m 22s
        } else {
            "${seconds}s"  // مثال: 6502s
        }
    }


    // استخدام MediaRecorder لبدء التسجيل الصوتي

    // دالة لإرسال الصوت إلى Firebase في الوقت الفعلي


    Box(modifier = Modifier.fillMaxSize()) {

        Top_Navigation_Box(tripId)
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
                    Text(formatMillisecondsWithSeconds(Time), fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.padding(horizontal = 10.dp))
                    Text("-", fontSize = 30.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.padding(horizontal = 10.dp))

                    if (isDataLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            String.format("%.1f km", distanceInKm),
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
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
                        if (isDataLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = destination.take(20),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
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


                                            destination = document.get("destination") as String
                                            distanceInKm =document.get("distanceInKm") as Double
                                            if (driverLat != null && driverLng != null && originLat != null && originLng != null) {
                                                val distance = calculateDistance(driverLat, driverLng, originLat, originLng)

                                                if (distance <= 5) {
                                                    triggerStart = true
                                                    updateTripStatus(tripId, "Started")
                                                } else {
                                                    triggerStart = true
                                                    updateTripStatus(tripId, "Started")
                                                    startRecording(tripId)
//                                                    withContext(Dispatchers.Main) {
//                                                        Toast.makeText(context, "You are not at the pickup point", Toast.LENGTH_SHORT).show()
//                                                    }
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
                                                    driverId?.let { incrementDriverTrips(it) }
                                                    withContext(Dispatchers.Main) {
                                                        TripEnd()
                                                    }
                                                }else {
                                                    stopRecordingAndUpload(tripId)
                                                    updateTripStatus(tripId, "Completed")


                                                    val commissionRate = 0.2
                                                    val commission = totalFare * commissionRate

                                                    driverId?.let { id ->
                                                        incrementDriverTrips(id)

                                                        val db = Firebase.firestore
                                                        val transaction = hashMapOf(
                                                            "driverId" to id,
                                                            "type" to "ride",
                                                            "amount" to -commission,
                                                            "tripId" to tripId,
                                                            "note" to "Commission for trip $tripId",
                                                            "timestamp" to FieldValue.serverTimestamp()
                                                        )

                                                        val walletRef = db.collection("drivers")
                                                            .whereEqualTo("id", id)
                                                            .limit(1)

                                                        walletRef.get().addOnSuccessListener { result ->
                                                            if (!result.isEmpty) {
                                                                val doc = result.documents[0]
                                                                val docRef = doc.reference
                                                                val oldBalance = doc.getDouble("balance") ?: 0.0
                                                                docRef.update("balance", oldBalance - commission)

                                                                db.collection("walletTransactions").add(transaction)
                                                            }
                                                        }
                                                    }

                                                    withContext(Dispatchers.Main) {
                                                        TripEnd()
                                                    }
                                                }


//                                                    withContext(Dispatchers.Main) {
//                                                        Toast.makeText(
//                                                            context,
//                                                            "You are not at the drop-off point",
//                                                            Toast.LENGTH_SHORT
//                                                        ).show()
//                                                    }

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

var audioRecord: AudioRecord? = null
var outputStream: ByteArrayOutputStream? = null
var recordingThread: Thread? = null
var isRecording = false

@SuppressLint("MissingPermission")
fun startRecording(tripId: String) {
    val bufferSize = AudioRecord.getMinBufferSize(
        44100, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT
    )

    audioRecord = AudioRecord(
        MediaRecorder.AudioSource.MIC,
        44100,
        AudioFormat.CHANNEL_IN_MONO,
        AudioFormat.ENCODING_PCM_16BIT,
        bufferSize
    )

    outputStream = ByteArrayOutputStream()
    val buffer = ByteArray(bufferSize)
    audioRecord?.startRecording()
    isRecording = true

    recordingThread = Thread {
        while (isRecording) {
            val bytesRead = audioRecord?.read(buffer, 0, buffer.size) ?: 0
            if (bytesRead > 0) {
                outputStream?.write(buffer, 0, bytesRead)
            }
        }
    }

    recordingThread?.start()
}
fun stopRecordingAndUpload(tripId: String) {
    isRecording = false
    audioRecord?.stop()
    audioRecord?.release()
    audioRecord = null

    recordingThread?.interrupt()
    recordingThread = null

    outputStream?.let { stream ->
        val pcmData = stream.toByteArray()
        val wavData = convertPcmToWav(pcmData, 44100, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT)
        uploadAudioFileToSupabase(wavData, tripId, "wav")
        stream.close()
    }
    outputStream = null
}
fun incrementDriverTrips(driverId: String) {
    val db = FirebaseFirestore.getInstance()

    db.collection("drivers")
        .whereEqualTo("id", driverId) // البحث باستخدام حقل _id
        .get()
        .addOnSuccessListener { querySnapshot ->
            if (!querySnapshot.isEmpty) {
                val document = querySnapshot.documents[0]

                // زيادة عدد الرحلات بمقدار 1
                document.reference.update("trips", FieldValue.increment(1))
                    .addOnSuccessListener {
                        Log.d("Firestore", "تم زيادة عدد الرحلات للسائق بنجاح")
                    }
                    .addOnFailureListener { e ->
                        Log.e("Firestore", "فشل في زيادة عدد الرحلات", e)
                    }
            } else {
                Log.e("Firestore", "لم يتم العثور على سائق بالمعرف المطلوب")
            }
        }
        .addOnFailureListener { e ->
            Log.e("Firestore", "فشل في البحث عن السائق", e)
        }
}
fun convertPcmToWav(pcmData: ByteArray, sampleRate: Int, channelConfig: Int, audioFormat: Int): ByteArray {
    val channels = when (channelConfig) {
        AudioFormat.CHANNEL_IN_MONO -> 1
        AudioFormat.CHANNEL_IN_STEREO -> 2
        else -> 1
    }

    val bitsPerSample = when (audioFormat) {
        AudioFormat.ENCODING_PCM_16BIT -> 16
        AudioFormat.ENCODING_PCM_8BIT -> 8
        else -> 16
    }

    val byteRate = sampleRate * channels * bitsPerSample / 8
    val blockAlign = channels * bitsPerSample / 8
    val dataSize = pcmData.size

    val header = ByteArray(44)
    header[0] = 'R'.code.toByte() // RIFF
    header[1] = 'I'.code.toByte()
    header[2] = 'F'.code.toByte()
    header[3] = 'F'.code.toByte()

    // File size - 8
    header[4] = (dataSize + 36 and 0xff).toByte()
    header[5] = (dataSize + 36 shr 8 and 0xff).toByte()
    header[6] = (dataSize + 36 shr 16 and 0xff).toByte()
    header[7] = (dataSize + 36 shr 24 and 0xff).toByte()

    header[8] = 'W'.code.toByte() // WAVE
    header[9] = 'A'.code.toByte()
    header[10] = 'V'.code.toByte()
    header[11] = 'E'.code.toByte()

    header[12] = 'f'.code.toByte() // fmt
    header[13] = 'm'.code.toByte()
    header[14] = 't'.code.toByte()
    header[15] = ' '.code.toByte()

    header[16] = 16 // Subchunk1Size
    header[17] = 0
    header[18] = 0
    header[19] = 0

    header[20] = 1 // AudioFormat (PCM)
    header[21] = 0

    header[22] = channels.toByte()
    header[23] = 0

    header[24] = (sampleRate and 0xff).toByte()
    header[25] = (sampleRate shr 8 and 0xff).toByte()
    header[26] = (sampleRate shr 16 and 0xff).toByte()
    header[27] = (sampleRate shr 24 and 0xff).toByte()

    header[28] = (byteRate and 0xff).toByte()
    header[29] = (byteRate shr 8 and 0xff).toByte()
    header[30] = (byteRate shr 16 and 0xff).toByte()
    header[31] = (byteRate shr 24 and 0xff).toByte()

    header[32] = blockAlign.toByte()
    header[33] = 0

    header[34] = bitsPerSample.toByte()
    header[35] = 0

    header[36] = 'd'.code.toByte() // data
    header[37] = 'a'.code.toByte()
    header[38] = 't'.code.toByte()
    header[39] = 'a'.code.toByte()

    header[40] = (dataSize and 0xff).toByte()
    header[41] = (dataSize shr 8 and 0xff).toByte()
    header[42] = (dataSize shr 16 and 0xff).toByte()
    header[43] = (dataSize shr 24 and 0xff).toByte()

    return header + pcmData
}


fun uploadAudioFileToSupabase(audioData: ByteArray, tripId: String, fileExtension: String = "wav") {
    val fileName = "$tripId-${System.currentTimeMillis()}.$fileExtension"
    val url = "https://mwncdoelxuwhtlrvtnap.supabase.co/storage/v1/object/live-audio/$fileName"

    val contentType = when (fileExtension) {
        "wav" -> "audio/wav"
        "mp3" -> "audio/mpeg"
        else -> "application/octet-stream"
    }

    val requestBody = audioData.toRequestBody(contentType.toMediaType())

    val request = Request.Builder()
        .url(url)
        .addHeader("apikey", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im13bmNkb2VseHV3aHRscnZ0bmFwIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDUwMjU4NjUsImV4cCI6MjA2MDYwMTg2NX0.f5Zlz_WSLypyCUn67g2PEA5ZjHa8VsqjJDbxIgtBBTk")
        .addHeader("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im13bmNkb2VseHV3aHRscnZ0bmFwIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDUwMjU4NjUsImV4cCI6MjA2MDYwMTg2NX0.f5Zlz_WSLypyCUn67g2PEA5ZjHa8VsqjJDbxIgtBBTk") // أضف هذا الهيدر
        .addHeader("Content-Type", contentType)
        .put(requestBody)
        .build()

    val client = OkHttpClient()
    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            Log.e("Supabase", "فشل رفع الملف: ${e.message}")
        }

        override fun onResponse(call: Call, response: Response) {
            response.use {
                if (!response.isSuccessful) {
                    Log.e("Supabase", "فشل في الاستجابة: ${response.code} - ${response.body?.string()}")
                    return
                }

                val publicUrl = "https://mwncdoelxuwhtlrvtnap.supabase.co/storage/v1/object/public/live-audio/$fileName"
                Log.d("Supabase", "تم رفع الملف بنجاح: $publicUrl")

                // يمكنك هنا حفظ الرابط في Firestore إذا لزم الأمر
                saveAudioUrlToFirestore(tripId, publicUrl)
            }
        }
    })
}
fun saveAudioUrlToFirestore(tripId: String, audioUrl: String) {
    val db = FirebaseFirestore.getInstance()

    db.collection("trips")
        .whereEqualTo("_id", tripId) // البحث باستخدام الحقل _id
        .get()
        .addOnSuccessListener { querySnapshot ->
            if (!querySnapshot.isEmpty) {
                // إذا وجدنا الرحلة
                val document = querySnapshot.documents[0]

                // إنشاء التحديث مع ضمان وجود الحقل
                val updates = hashMapOf<String, Any>(
                    "audioRecordingUrl" to audioUrl,
                    "lastUpdated" to FieldValue.serverTimestamp()
                )

                document.reference.update(updates)
                    .addOnSuccessListener {
                        Log.d("Firestore", "تم تحديث رابط التسجيل بنجاح")
                    }
                    .addOnFailureListener { e ->
                        Log.e("Firestore", "فشل في تحديث الرحلة", e)
                    }
            } else {
                Log.e("Firestore", "لم يتم العثور على رحلة بالمعرف المطلوب")
            }
        }
        .addOnFailureListener { e ->
            Log.e("Firestore", "فشل في البحث عن الرحلة", e)
        }
}

