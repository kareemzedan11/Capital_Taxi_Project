package com.example.capital_taxi.Presentation.ui.Passengar.Screens.Home.UserHome.Components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PickupDropOffRow(
    iconRes: Int,
    hintText: String,
    onValueChange: (String) -> Unit,
    value: String // إضافة القيمة هنا
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            modifier = Modifier.size(26.dp),
            painter = painterResource(iconRes),
            contentDescription = null,
            tint = Color.Unspecified
        )

        Box(
            modifier = Modifier
                .weight(1f)
        ) {
            OutlinedTextField(
                value = value, // ربط القيمة هنا
                onValueChange = {
                    onValueChange(it) // تحديث القيمة عند التغيير
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { androidx.compose.material3.Text(text = hintText, fontSize = 16.sp) },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = Color.Transparent
                )
            )

            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp)
                    .height(2.dp)
                    .align(Alignment.BottomCenter),
                color = Color.LightGray,
                thickness = 2.dp
            )
        }
    }
}
