package com.example.capital_taxi.Presentation.ui.Driver.Screens.Home.drawerTabs.income.Components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.app.ui.theme.CustomFontFamily
import com.example.app.ui.theme.responsiveTextSize


@Composable
fun IncomeItem(name: String, date: String, amount: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, shape = RoundedCornerShape(8.dp))
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(name,  fontSize = responsiveTextSize(fraction = 0.06f, minSize = 14.sp, maxSize = 18.sp),



                fontFamily = CustomFontFamily, fontWeight = FontWeight.Bold)
            Text(date, style = MaterialTheme.typography.body2, color = Color.Gray)
        }
        Text(
            amount,
            fontSize = responsiveTextSize(fraction = 0.06f, minSize = 14.sp, maxSize = 18.sp),



            fontFamily = CustomFontFamily,
            fontWeight = FontWeight.Bold,
            color = Color.Green
        )
    }
}