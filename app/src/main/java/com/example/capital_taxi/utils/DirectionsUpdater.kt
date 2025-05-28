package com.example.capital_taxi.utils


import android.os.Handler
import android.os.Looper
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

class DirectionsUpdater(
    private val tripId: String,
    private val graphHopperApiKey: String
) {
    private val firestore = FirebaseFirestore.getInstance()
    private val client = OkHttpClient()

    private val handler = Handler(Looper.getMainLooper())
    private val updateIntervalMillis = TimeUnit.MINUTES.toMillis(1) // كل دقيقة

    private var isRunning = false

    private var origin: String = ""
    private var destination: String = ""

    /**
     * حدد نقاط البداية والنهاية
     * @param originStr  مثلاً "30.0444,31.2357"
     * @param destinationStr مثلاً "30.0131,31.2089"
     */
    fun setRoute(originStr: String, destinationStr: String) {
        origin = originStr
        destination = destinationStr
    }

    /**
     * ابدأ التحديث الدوري
     */
    fun start() {
        if (origin.isEmpty() || destination.isEmpty()) {
            Log.e("DirectionsUpdater", "Origin or destination not set")
            return
        }
        if (isRunning) return
        isRunning = true
        scheduleNextUpdate()
    }

    /**
     * أوقف التحديث الدوري
     */
    fun stop() {
        isRunning = false
        handler.removeCallbacksAndMessages(null)
    }

    private fun scheduleNextUpdate() {
        if (!isRunning) return
        handler.postDelayed({
            fetchAndUpdate()
            scheduleNextUpdate()
        }, updateIntervalMillis)
    }

    /**
     * جلب الوقت والمسافة من GraphHopper وتحديث Firestore
     */
    private fun fetchAndUpdate() {
        Log.d("DirectionsUpdater", "Fetching directions from $origin to $destination")

        val url =
            "https://graphhopper.com/api/1/route?point=$origin&point=$destination&vehicle=car&locale=ar&key=$graphHopperApiKey"

        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("DirectionsUpdater", "Failed to fetch directions: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!it.isSuccessful) {
                        Log.e("DirectionsUpdater", "Unexpected response code: ${response.code}")
                        return
                    }
                    val bodyString = it.body?.string() ?: return
                    try {
                        val json = JSONObject(bodyString)
                        val paths = json.getJSONArray("paths")
                        if (paths.length() == 0) {
                            Log.e("DirectionsUpdater", "No paths found")
                            return
                        }
                        val path = paths.getJSONObject(0)
                        val distance = path.getDouble("distance") // بالمتر
                        val time = path.getLong("time") // بالملي ثانية


                        Log.d(
                            "DirectionsUpdater",
                            "Received distance = $distance m, time = $time s"
                        )

                        updateTripProgress(tripId, distance, time)
                    } catch (e: Exception) {
                        Log.e("DirectionsUpdater", "Failed to parse response: ${e.message}")
                    }
                }
            }
        })
    }

    private fun updateTripProgress(
        tripId: String,
        remainingDistance: Double,
        remainingTimeSeconds: Long
    ) {
        Log.d(
            "DirectionsUpdater",
            "Updating trip: $tripId with distance = $remainingDistance and time = $remainingTimeSeconds"
        )

        firestore.collection("trips")
            .whereEqualTo("_id", tripId)
            .get()
            .addOnSuccessListener { snapshot ->
                val docRef = snapshot.documents.firstOrNull()?.reference
                if (docRef != null) {
                    docRef.update(
                        mapOf(
                            "distance" to remainingDistance,
                            "time" to remainingTimeSeconds
                        )
                    ).addOnSuccessListener {
                        Log.d("DirectionsUpdater", "Successfully updated Firestore")
                    }.addOnFailureListener { e ->
                        Log.e("DirectionsUpdater", "Failed to update Firestore: ${e.message}")
                    }
                } else {
                    Log.e("DirectionsUpdater", "Trip document not found for id $tripId")
                }
            }
            .addOnFailureListener { e ->
                Log.e("DirectionsUpdater", "Firestore query failed: ${e.message}")
            }
    }
}