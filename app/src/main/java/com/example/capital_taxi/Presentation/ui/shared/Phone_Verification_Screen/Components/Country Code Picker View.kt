package com.example.capital_taxi.Presentation.ui.shared.Phone_Verification_Screen.Components

import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView
import com.hbb20.CountryCodePicker


@Composable
fun CountryCodePickerView(onCountrySelected: (String) -> Unit) {
    AndroidView(factory = { context ->
        val countryCodePicker = CountryCodePicker(context).apply {
            setOnCountryChangeListener {
                onCountrySelected(selectedCountryCodeWithPlus)
            }

            isSearchAllowed = true


            setCountryForNameCode("EG")
        }
        countryCodePicker
    })
}