package com.example.capital_taxi.utils

import android.annotation.SuppressLint
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.util.Date
import java.util.Locale

data class PeakPeriod(val start: String, val end: String)

@SuppressLint("NewApi")
suspend fun isNowInPeakHourFromServer(): Boolean {
    val serverTime = getServerTime() ?: return false
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    val currentTimeStr = timeFormat.format(serverTime)
    val currentTime = LocalTime.parse(currentTimeStr)

    val periods = getPeakPeriods() // نفس اللي عندك من Firestore
    return periods.any { period ->
        val start = LocalTime.parse(period.start)
        val end = LocalTime.parse(period.end)
        currentTime.isAfter(start) && currentTime.isBefore(end)
    }
}

suspend fun getPeakPeriods(): List<PeakPeriod> {
    val doc = Firebase.firestore.collection("settings").document("peak_hours").get().await()
    val list = doc["periods"] as? List<Map<String, String>> ?: return emptyList()
    return list.map { PeakPeriod(it["start"] ?: "", it["end"] ?: "") }
}
fun calculatePriceWithPeak(originalPrice: Double, isPeak: Boolean): Double {
    return if (isPeak) originalPrice * 1.2 else originalPrice
}
suspend fun getServerTime(): Date? {
    val ref = Firebase.firestore.collection("utils").document("server_time")
    // نكتب timestamp جديد
    ref.set(mapOf("timestamp" to FieldValue.serverTimestamp())).await()

    // نقرأه تاني بعد الكتابة
    val snap = ref.get().await()
    return snap.getTimestamp("timestamp")?.toDate()
}
