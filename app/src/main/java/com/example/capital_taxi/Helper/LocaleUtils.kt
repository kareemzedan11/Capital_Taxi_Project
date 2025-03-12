package com.example.capital_taxi.Helper

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import java.util.Locale

object LocaleUtils {

    // [AppPrefs] is sharedpreferences or datastore
    fun setLocale(c: Context, pref: SharedPreferences) {
        val language = pref.getString("language", "en") ?: "ar"
        updateResources(c, language)
    }

    private fun updateResources(context: Context, language: String) {
        val locale = Locale(language)
        Locale.setDefault(locale)

        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)

        // For API level 17+, you should use `createConfigurationContext()` for updating resources
        context.createConfigurationContext(config)
    }
}
