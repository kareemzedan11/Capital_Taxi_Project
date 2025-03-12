package com.example.capital_taxi.Presentation.ui.shared.Confirm_information.Components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.app.ui.theme.CustomFontFamily
import com.example.app.ui.theme.responsiveTextSize
import com.example.capital_taxi.R

// Next Button Composable
@Composable
fun NextButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
        colors = ButtonDefaults.buttonColors(colorResource(R.color.primary_color))
    ) {
        Text(
            stringResource(R.string.next_button),
            color = Color.Black,   fontSize = responsiveTextSize(
                fraction = 0.06f,
                minSize = 16.sp,
                maxSize = 20.sp
            ),
            fontFamily = CustomFontFamily, fontWeight = FontWeight.W400
        )
    }
}
