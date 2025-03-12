package com.example.capital_taxi.Presentation.ui.shared.Confirm_information.Components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.capital_taxi.R

// Back Button Composable
@Composable
fun BackButton() {
    Box(
        modifier = Modifier
            .size(36.dp)
            .background(Color.Transparent)
            .border(4.dp, color = Color.Black, shape = CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            modifier = Modifier.size(26.dp),
            painter = painterResource(id = R.drawable.baseline_arrow_back_ios_new_24),
            contentDescription = "Back",
            tint = Color.Black
        )
    }
}


