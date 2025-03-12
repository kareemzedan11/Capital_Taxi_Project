package com.example.capital_taxi.Presentation.ui.Passengar.Screens.Voucher.Components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.app.ui.theme.CustomFontFamily
import com.example.app.ui.theme.responsiveTextSize
import com.example.capital_taxi.R


@Composable
fun HeaderSection(backgroundColor: Color, onClose: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .background(backgroundColor)
    ) {
        Icon(
            modifier = Modifier
                .padding(start = 16.dp, top = 60.dp)
                .size(40.dp)
                .align(Alignment.TopStart)
                .clip(CircleShape)
                .background(Color.White)
                .clickable { onClose() },
            imageVector = Icons.Default.Close,
            contentDescription = "close",
            tint = Color.Black
        )

        Text(
            text = stringResource(R.string.Coupons),
            modifier = Modifier
                .align(Alignment.Center)
                .padding(top = 60.dp),
            fontSize = responsiveTextSize(
                fraction = 0.06f,
                minSize = 20.sp,
                maxSize = 24.sp
            ),
            fontFamily = CustomFontFamily,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }
}


