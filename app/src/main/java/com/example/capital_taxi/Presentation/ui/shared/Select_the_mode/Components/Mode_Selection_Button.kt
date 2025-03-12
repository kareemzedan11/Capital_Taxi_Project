package com.example.capital_taxi.Presentation.ui.shared.Select_the_mode.Components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.app.ui.theme.CustomFontFamily
import com.example.app.ui.theme.responsiveTextSize


@Composable
fun ModeSelectionButton(
    label: String,
    onClick: () -> Unit
) {
    Button(
        modifier = Modifier
            .fillMaxWidth(.95f)
            .padding(8.dp)
            .border(2.dp, color = Color.Transparent, RoundedCornerShape(30.dp))
            .background(Color.White, RoundedCornerShape(30.dp))
            .padding(6.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
        onClick = onClick
    ) {
        Text(
            text = label,
            fontWeight = FontWeight.Medium,

            color = Color(0XFF111111)
            ,
            fontSize = responsiveTextSize(
                fraction = 0.06f,
                minSize = 14.sp,
                maxSize = 20.sp
            ),
            fontFamily = CustomFontFamily,
        )
    }
}