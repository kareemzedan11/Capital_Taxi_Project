package com.example.capital_taxi.Presentation.ui.Driver.Screens.Home.Components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.app.ui.theme.CustomFontFamily
import com.example.app.ui.theme.responsiveTextSize


@Composable
fun InfoRow(icon: Int, label: String, value: String) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(7.dp))

            Text(
                text = label,
                color = Color.Gray,
                fontSize = responsiveTextSize(fraction = 0.06f, minSize = 12.sp, maxSize = 14.sp),


                fontFamily = CustomFontFamily,
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = value,
                fontWeight = FontWeight.Bold,
                fontSize = responsiveTextSize(fraction = 0.06f, minSize = 12.sp, maxSize = 14.sp),


                fontFamily = CustomFontFamily,
            )


        }
        HorizontalDivider(
            thickness = 1.dp,
            color = Color.Gray,
            modifier = Modifier.padding(horizontal = 5.dp, vertical = 10.dp)
        )
    }
}