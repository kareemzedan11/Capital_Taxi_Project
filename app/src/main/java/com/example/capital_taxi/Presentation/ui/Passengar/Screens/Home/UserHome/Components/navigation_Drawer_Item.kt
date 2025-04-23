package com.example.capital_taxi.Presentation.ui.Passengar.Screens.Home.UserHome.Components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.app.ui.theme.CustomFontFamily
import com.example.app.ui.theme.responsiveTextSize

@Composable
fun navigationDrawerItem(onClick: () -> Unit, painter: Painter? = null, text: String) {
    NavigationDrawerItem(

        onClick = { onClick() },
        icon = {
            if (painter != null) {
                Icon(
                    modifier = Modifier

                        .padding(start = 5.dp, end = 3.dp) // Apply padding inside the background
                        .size(28.dp), // Set the size of the icon
                    contentDescription = "menu",
                    painter = painter, tint = Color.Unspecified

                )

            }
        },
        selected = false,
        label = {
            Text(
                text, fontSize = responsiveTextSize(
                    fraction = 0.06f,
                    minSize = 16.sp,
                    maxSize = 20.sp
                ),
                fontFamily = CustomFontFamily,
                fontWeight = FontWeight.Bold
            )
        },
        badge = {
            Icon(
                modifier = Modifier

                    .padding(start = 5.dp, end = 3.dp) // Apply padding inside the background
                    .size(28.dp), // Set the size of the icon
                contentDescription = "menu",
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight, tint = Color.Unspecified

            )
        },
    )

}

