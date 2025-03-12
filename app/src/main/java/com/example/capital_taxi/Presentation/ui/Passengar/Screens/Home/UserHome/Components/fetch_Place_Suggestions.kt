package com.example.capital_taxi.Presentation.ui.Passengar.Screens.Home.UserHome.Components

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

fun fetchPlaceSuggestions(
    query: String,
    onResult: (List<String>) -> Unit
) {
    val url = "https://nominatim.openstreetmap.org/search?format=json&q=$query"

    CoroutineScope(Dispatchers.IO).launch {
        try {
            val connection = URL(url).openConnection() as HttpURLConnection
            connection.connectTimeout = 10000
            connection.readTimeout = 10000
            connection.requestMethod = "GET"
            connection.setRequestProperty("User-Agent", "Capital Taxi") // مطلوب في Nominatim

            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                val response = connection.inputStream.bufferedReader().readText()
                Log.d("API Response", response) // Debugging
                val jsonArray = JSONArray(response)
                val suggestions = mutableListOf<String>()

                for (i in 0 until jsonArray.length()) {
                    val displayName = jsonArray.getJSONObject(i).getString("display_name")
                    suggestions.add(displayName)
                }

                withContext(Dispatchers.Main) {
                    onResult(suggestions)
                }
            } else {
                withContext(Dispatchers.Main) {
                    onResult(emptyList())
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            withContext(Dispatchers.Main) {
                onResult(emptyList())
            }
        }
    }
}
fun fetchLocationCoordinates(
    query: String,
    onResult: (Double, Double) -> Unit
) {
    Log.d("fetchLocationCoordinates", "Started fetching for query: $query") // إضافة Log في بداية الدالة
    val url = "https://nominatim.openstreetmap.org/search?format=json&q=$query"
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val connection = URL(url).openConnection() as HttpURLConnection
            connection.connectTimeout = 10000
            connection.readTimeout = 10000
            connection.requestMethod = "GET"
            connection.setRequestProperty("User-Agent", "YourAppName") // مطلوب في Nominatim
            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                val response = connection.inputStream.bufferedReader().readText()
                Log.d("API Response", response) // طباعة الاستجابة
                val jsonArray = JSONArray(response)
                if (jsonArray.length() > 0) {
                    val lat = jsonArray.getJSONObject(0).getDouble("lat")
                    val lon = jsonArray.getJSONObject(0).getDouble("lon")
                    Log.d("Coordinates", "Latitude: $lat, Longitude: $lon") // طباعة الإحداثيات
                    withContext(Dispatchers.Main) {
                        onResult(lat, lon)
                    }
                } else {
                    Log.d("No Results", "No results found") // طباعة حالة عدم وجود نتائج
                    withContext(Dispatchers.Main) {
                        onResult(0.0, 0.0)
                    }
                }
            } else {
                Log.d("API Error", "Error response code: ${connection.responseCode}") // طباعة خطأ الاستجابة
                withContext(Dispatchers.Main) {
                    onResult(0.0, 0.0)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("fetchLocationCoordinates", "Error: ${e.message}") // طباعة الخطأ
            withContext(Dispatchers.Main) {
                onResult(0.0, 0.0)
            }
        }
    }
}
