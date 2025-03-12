package com.example.capital_taxi.Presentation.ui.Passengar.Screens.InviteFriendsPage.Components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
fun DetailCard(title: String, description: String, backgroundColor: Color) {
    Card(
        modifier = Modifier
            .width(200.dp)
            .wrapContentHeight()

            .padding(10.dp),
        elevation = CardDefaults.cardElevation(10.dp)
    ) {
        Column(Modifier.padding(10.dp)) {
            Text(
                text = title,
                fontSize = responsiveTextSize(
                    fraction = 0.06f,
                    minSize = 14.sp,
                    maxSize = 18.sp
                ),
                fontFamily = CustomFontFamily,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF333333)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = description,
                fontSize = responsiveTextSize(
                    fraction = 0.06f,
                    minSize = 12.sp,
                    maxSize = 14.sp
                ),
                fontFamily = CustomFontFamily,
                color = Color(0xFF666666)
            )
        }
    }
}