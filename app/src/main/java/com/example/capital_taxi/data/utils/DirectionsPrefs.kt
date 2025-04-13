package com.example.capital_taxi.data.utils

import android.content.SharedPreferences
import androidx.core.content.edit
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object DirectionsPrefs {
    private const val PREFS_NAME = "directions_prefs"
    private const val KEY_DISTANCE = "distance"
    private const val KEY_DURATION = "duration"

    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun saveDirectionsData(context: Context, distance: Double, duration: Double) {
        Log.d("Prefs", "Saving to prefs: distance=$distance, duration=$duration")
        getSharedPreferences(context).edit {
            putString(KEY_DISTANCE, distance.toString())
            putString(KEY_DURATION, duration.toString())
        }
    }

    fun getDistance(context: Context): Double {
        return getSharedPreferences(context)
            .getString(KEY_DISTANCE, "0.0")?.toDoubleOrNull() ?: 0.0
    }

    fun getDuration(context: Context): Double {
        return getSharedPreferences(context)
            .getString(KEY_DURATION, "0.0")?.toDoubleOrNull() ?: 0.0
    }

    fun clear(context: Context) {
        getSharedPreferences(context).edit().clear().apply()
    }
}

