package com.example.capital_taxi.Presentation.ui.shared.Language.components



import android.content.Context
import android.content.SharedPreferences

object LanguagePreference {
    private const val PREF_NAME = "language_pref"
    private const val LANGUAGE_KEY = "selected_language"

    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun saveLanguage(context: Context, languageCode: String) {
        val preferences = getPreferences(context)
        preferences.edit().putString(LANGUAGE_KEY, languageCode).apply()
    }

    fun getSavedLanguage(context: Context): String {
        val preferences = getPreferences(context)
        return preferences.getString(LANGUAGE_KEY, "en") ?: "en"
    }
}